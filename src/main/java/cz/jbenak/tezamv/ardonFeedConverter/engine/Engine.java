package cz.jbenak.tezamv.ardonFeedConverter.engine;

import cz.jbenak.tezamv.ardonFeedConverter.Main;
import cz.jbenak.tezamv.ardonFeedConverter.MainController;
import cz.jbenak.tezamv.ardonFeedConverter.dialogs.UnpairedCatsController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Engine {

    private final MainController controller;
    private final Processes processes;
    private final ExecutorService taskExecutor = Executors.newCachedThreadPool(runnable -> {
        Thread processor = Executors.defaultThreadFactory().newThread(runnable);
        processor.setDaemon(true);
        return processor;
    });
    private Task<Void> currentTask;

    public Engine(MainController controller) {
        this.controller = controller;
        this.processes = new Processes(controller);
    }

    public Task<Void> getCurrentTask() {
        return currentTask;
    }

    public void processLuma() {
        currentTask = new Task<>() {
            @Override
            protected Void call() {
                Main.getInstance().setProcessing(true);
                controller.appendTextToLog("Stahuji XML feed dodavatele Luma Trading s.r.o.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Stahuji XML feed Lumy"));
                controller.getProgressBar().setVisible(true);
                controller.getBtnStartLuma().setDisable(true);
                controller.getBtnStop().setDisable(false);
                try {
                    processes.downloadFile(controller.getLumaFeedUrl(), controller.getLumaDownloadedFile());
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze stáhnout feed", "Nepodařilo se stáhnout XML feed partnera Luma:", e));
                    controller.appendTextToLog("Stažení XML feedu nebylo možno dokončit.");
                    controller.appendTextToLog(e.getLocalizedMessage());
                }
                controller.appendTextToLog("Stažení XML feedu dodavatele Luma dokončeno.");
                if (checkFileExists(controller.getLumaDownloadedFile())) {
                    try {
                        processes.loadLuma();
                    } catch (Exception e) {
                        Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Luma:", e));
                        controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                        controller.appendTextToLog(e.getLocalizedMessage());
                    }
                } else {
                    Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Luma:", new IOException("Vstupní soubor " + controller.getLumaDownloadedFile() + " neexistuje, nebo se ho nepodařilo otevřít.")));
                    controller.appendTextToLog("Stažení XML feedu nebylo možno dokončit.");
                }
                controller.appendTextToLog("Zpracování XML feedu dodavatele Luma dokončeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
                return null;
            }

            @Override
            protected void cancelled() {
                try {
                    processes.cancelProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                controller.appendTextToLog("Zpracování XML feedu dodavatele Luma zrušeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
            }
        };
        taskExecutor.submit(currentTask);
    }

    public void showUnpairedCatsLuma() {
        currentTask = new Task<>() {
            @Override
            protected Void call() {
                Main.getInstance().setProcessing(true);
                controller.appendTextToLog("Zpracovávám stažený XML feed dodavatele Luma Trading s.r.o.");
                controller.getProgressBar().setVisible(true);
                controller.getBtnStartLuma().setDisable(true);
                controller.getBtnStop().setDisable(false);
                HashSet<String> unpairedCats = null;
                if (checkFileExists(controller.getLumaDownloadedFile())) {
                    try {
                        try {
                            unpairedCats = processes.getUnpairedCatsLuma();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Luma:", e));
                            controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                            controller.appendTextToLog(e.getLocalizedMessage());
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Luma:", e));
                        controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                        controller.appendTextToLog(e.getLocalizedMessage());
                    }
                    if (!isCancelled() && unpairedCats != null) {
                        HashSet<String> finalUnpairedCats = unpairedCats;
                        Platform.runLater(() -> {
                            try {
                                showUnpairedCatsDialog("Luma Trading s.r.o.", finalUnpairedCats);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zobrazit seznam", "Nelze zobrazit dialog pro zobrazení nespárovaných kategorií dodavatele Luma:", new IOException("Vstupní soubor " + controller.getLumaDownloadedFile() + " neexistuje, nebo se ho nepodařilo otevřít.")));
                                controller.appendTextToLog("Nelze zobrazit dialog pro zobrazení nespárovaných kategorií dodavatele Luma:\n" + e.getLocalizedMessage());
                            }
                        });
                    }
                } else {
                    Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Luma:", new IOException("Vstupní soubor " + controller.getLumaDownloadedFile() + " neexistuje, nebo se ho nepodařilo otevřít.")));
                    controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                }
                controller.appendTextToLog("Zpracování XML feedu dodavatele Luma pro účely zobrazení nespárovaných kategorií dokončeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
                return null;
            }

            @Override
            protected void cancelled() {
                try {
                    processes.cancelProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                controller.appendTextToLog("Zpracování XML feedu z důvodu zobrazení nespárovaných kategoriií dodavatele Luma zrušeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
            }
        };
        taskExecutor.submit(currentTask);
    }

    public void processArdon() {
        currentTask = new Task<>() {
            @Override
            protected Void call() {
                Main.getInstance().setProcessing(true);
                controller.appendTextToLog("Stahuji XML feed dodavatele ARDON Safety s.r.o.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Stahuji XML feed Ardonu"));
                controller.getProgressBar().setVisible(true);
                controller.getBtnStartArdon().setDisable(true);
                controller.getBtnStop().setDisable(false);
                if (!this.isCancelled()) {
                    try {
                        processes.downloadFile(controller.getArdonFeedUrl(), controller.getArdonDownloadedFile());
                    } catch (Exception e) {
                        Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze stáhnout feed", "Nepodařilo se stáhnout XML feed partnera Ardon:", e));
                        controller.appendTextToLog("Stažení XML feedu nebylo možno dokončit.");
                        controller.appendTextToLog(e.getLocalizedMessage());
                    }
                    controller.appendTextToLog("Stažení XML feedu dodavatele Ardon dokončeno.");
                }
                if (!this.isCancelled() && checkFileExists(controller.getArdonDownloadedFile())) {
                    try {
                        processes.loadArdon();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Ardon:", e));
                        controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                        controller.appendTextToLog(e.getMessage());
                    }
                } else {
                    Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Ardon:", new IOException("Vstupní soubor " + controller.getArdonDownloadedFile() + " neexistuje, nebo se ho nepodařilo otevřít.")));
                    controller.appendTextToLog("Stažení XML feedu nebylo možno dokončit.");
                }
                controller.appendTextToLog("Zpracování XML feedu dodavatele Ardon dokončeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartArdon().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
                return null;
            }

            @Override
            protected void cancelled() {
                try {
                    processes.cancelProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                controller.appendTextToLog("Zpracování XML feedu dodavatele Ardon zrušeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartArdon().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
            }
        };
        taskExecutor.submit(currentTask);
    }

    public void showUnpairedCatsArdon() {
        currentTask = new Task<>() {
            @Override
            protected Void call() {
                Main.getInstance().setProcessing(true);
                controller.appendTextToLog("Zpracovávám stažený XML feed dodavatele Ardon Safety s.r.o.");
                controller.getProgressBar().setVisible(true);
                controller.getBtnStartLuma().setDisable(true);
                controller.getBtnStop().setDisable(false);
                HashSet<String> unpairedCats = null;
                if (checkFileExists(controller.getArdonDownloadedFile())) {
                    try {
                        try {
                            unpairedCats = processes.getUnpairedCatsArdon();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Ardon:", e));
                            controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                            controller.appendTextToLog(e.getLocalizedMessage());
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Ardon:", e));
                        controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                        controller.appendTextToLog(e.getLocalizedMessage());
                    }
                    if (!isCancelled() && unpairedCats != null) {
                        HashSet<String> finalUnpairedCats = unpairedCats;
                        Platform.runLater(() -> {
                            try {
                                showUnpairedCatsDialog("Ardon Safety s.r.o.", finalUnpairedCats);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zobrazit seznam", "Nelze zobrazit dialog pro zobrazení nespárovaných kategorií dodavatele Ardon:", new IOException("Vstupní soubor " + controller.getArdonDownloadedFile() + " neexistuje, nebo se ho nepodařilo otevřít.")));
                                controller.appendTextToLog("Nelze zobrazit dialog pro zobrazení nespárovaných kategorií dodavatele Ardon:\n" + e.getLocalizedMessage());
                            }
                        });
                    }
                } else {
                    Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze zpracovat feed", "Nepodařilo se zpracovat XML feed partnera Ardon:", new IOException("Vstupní soubor " + controller.getArdonDownloadedFile() + " neexistuje, nebo se ho nepodařilo otevřít.")));
                    controller.appendTextToLog("Zpracování XML feedu nebylo možno dokončit.");
                }
                controller.appendTextToLog("Zpracování XML feedu dodavatele Ardon pro účely zobrazení nespárovaných kategorií dokončeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
                return null;
            }

            @Override
            protected void cancelled() {
                try {
                    processes.cancelProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                controller.appendTextToLog("Zpracování XML feedu z důvodu zobrazení nespárovaných kategoriií dodavatele Ardon zrušeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
            }
        };
        taskExecutor.submit(currentTask);
    }

    public void downloadArdonImages(boolean processOnly) {
        currentTask = new Task<>() {
            @Override
            protected Void call() {
                Main.getInstance().setProcessing(true);
                controller.appendTextToLog("Stahuji obrázky z XML feedu dodavatele ARDON Safety s.r.o.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Stahuji obrázky produktů Ardon"));
                controller.getProgressBar().setVisible(true);
                controller.getBtnStartArdon().setDisable(true);
                controller.getBtnStop().setDisable(false);
                if (!this.isCancelled()) {
                    try {
                        processes.loadImagesArdon(processOnly);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze stáhnout obrázky", "Nepodařilo se stáhnout obrázky produktů partnera Ardon:", e));
                        controller.appendTextToLog("Stažení obrázků produktů nebylo možno dokončit.");
                        controller.appendTextToLog(e.getLocalizedMessage());
                    }
                    controller.appendTextToLog("Stažení obrázků produktů a přepis adres obrázků v XML feedu dodavatele Ardon dokončeno.");
                }
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartArdon().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
                return null;
            }

            @Override
            protected void cancelled() {
                try {
                    processes.cancelProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                controller.appendTextToLog("Stažení obrázků produktů a přepis adres obrázků v XML feedu dodavatele Ardon zrušeno.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartArdon().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
            }
        };
        taskExecutor.submit(currentTask);
    }

    public void uploadArdonImages() {
        currentTask = new Task<>() {
            @Override
            protected Void call() {
                Main.getInstance().setProcessing(true);
                controller.getProgressBar().setVisible(true);
                controller.getBtnStartLuma().setDisable(true);
                controller.getBtnStop().setDisable(false);
                processes.uploadImagesArdon();
                controller.appendTextToLog("Upload obrázků dodavatele Ardon dokončen.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
                return null;
            }

            @Override
            protected void cancelled() {
                try {
                    processes.cancelProcess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                controller.appendTextToLog("Upload obrázků dodavatele Ardon zrušen.");
                Platform.runLater(() -> controller.getInfoMessage().setText("Připraven"));
                controller.getProgressBar().setVisible(false);
                controller.getBtnStartLuma().setDisable(false);
                controller.getBtnStop().setDisable(true);
                Main.getInstance().setProcessing(false);
            }
        };
        taskExecutor.submit(currentTask);
    }

    private boolean checkFileExists(String path) {
        File toCheck = new File(path);
        return toCheck.exists();
    }

    private void showUnpairedCatsDialog(String supplier, HashSet<String> data) throws Exception {
        Stage dialog = new Stage();
        dialog.initOwner(Main.getInstance().getMainStage());
        dialog.setTitle("Nespárované kategorie");
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("dialogs/unpaired-cats.fxml"));
        dialog.setScene(new Scene(loader.load()));
        UnpairedCatsController unpairedCatsController = loader.getController();
        unpairedCatsController.setTitle(supplier);
        unpairedCatsController.setDialogStage(dialog);
        unpairedCatsController.setItems(data);
        dialog.show();
    }
}
