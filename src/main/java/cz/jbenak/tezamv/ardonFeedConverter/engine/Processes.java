package cz.jbenak.tezamv.ardonFeedConverter.engine;

import cz.jbenak.tezamv.ardonFeedConverter.Main;
import cz.jbenak.tezamv.ardonFeedConverter.MainController;
import cz.jbenak.tezamv.ardonFeedConverter.luma.LumaShop;
import cz.jbenak.tezamv.ardonFeedConverter.mappers.ArdonMapper;
import cz.jbenak.tezamv.ardonFeedConverter.source.Shop;
import cz.jbenak.tezamv.ardonFeedConverter.target.TargetShop;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;
import javafx.application.Platform;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class Processes {

    private final MainController controller;
    private InputStream currentInputStream;
    private Stream<String> currentStream;
    private FTPClient client;
    private boolean cancelled;

    public Processes(MainController controller) {
        this.controller = controller;
    }

    public void cancelProcess() throws Exception {
        if (currentStream != null) {
            currentStream.close();
        }
        if (currentInputStream != null) {
            currentInputStream.close();
        }
        if (client != null && client.isConnected()) {
            client.abort();
            client.logout();
            client.disconnect();
        }
        cancelled = true;
    }

    public void downloadFile(String fileUrl, String destinationFile) throws Exception {
        currentInputStream = new URL(fileUrl).openStream();
        Files.copy(currentInputStream, Paths.get(destinationFile), StandardCopyOption.REPLACE_EXISTING);
    }

    private Shop unmarshallArdon() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Shop.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        return (Shop) unmarshaller.unmarshal(new File(controller.getArdonDownloadedFile()));
    }

    private TargetShop unmarshallProcessedArdon() throws Exception {
        JAXBContext context = JAXBContext.newInstance(TargetShop.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        return (TargetShop) unmarshaller.unmarshal(new File(controller.getArdonProcessedFile()));
    }

    private void marshallArdon(TargetShop toSave) throws Exception {
        JAXBContext context = JAXBContext.newInstance(TargetShop.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(toSave, new File(controller.getArdonProcessedFile()));
    }

    private LumaShop unmarshallLuma() throws Exception {
        JAXBContext context = JAXBContext.newInstance(LumaShop.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        return (LumaShop) unmarshaller.unmarshal(new File(controller.getLumaDownloadedFile()));
    }

    private void marshallLuma(LumaShop toSave) throws Exception {
        JAXBContext context = JAXBContext.newInstance(LumaShop.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(toSave, new File(controller.getLumaProcessedFile()));
    }

    private void correctCData(String xmlFile) throws Exception {
        Path of = Path.of(xmlFile);
        String cdataCorrection = Files.readString(of, StandardCharsets.UTF_8);
        cdataCorrection = cdataCorrection.replaceAll("&lt;", "<");
        cdataCorrection = cdataCorrection.replaceAll("&gt;", ">");
        Files.writeString(of, cdataCorrection);
    }

    private Map<String, String> getCategoryMapping(String mappingConfig) throws Exception {
        Map<String, String> catsMap = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(mappingConfig), StandardCharsets.UTF_8)) {
            currentStream = stream;
            stream.forEach(line -> {
                String[] keyVal = line.split("=");
                catsMap.put(keyVal[0].trim(), keyVal[1].trim());
            });
        }
        return catsMap;
    }

    private void uploadFeed(String sourceFile, String targetUrl, String remoteDir, String name, String password) {
        client = new FTPClient();
        controller.appendTextToLog("Zahajuji upload zpracovaného XML feedu.");
        Platform.runLater(() -> controller.getInfoMessage().setText("Nahrávám zpracovaný XML feed"));
        try {
            client.connect(targetUrl);
            if (!client.isConnected()) {
                throw new Exception("Nelze se připojit k FTP serveru. Zkontrolujte prosím jeho adresu.");
            }
            if (client.login(name, password)) {
                controller.appendTextToLog("Přihlášení k FTP serveru bylo úspěšné.");
                if (!client.changeWorkingDirectory(remoteDir)) {
                    client.changeToParentDirectory();
                }
                client.setFileType(FTP.BINARY_FILE_TYPE);
                File uploadFile = new File(sourceFile);
                if (uploadFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(uploadFile)) {
                        client.storeFile(uploadFile.getName(), fis);
                        controller.appendTextToLog("Upload XML feedu dokončen.");
                    }
                } else {
                    throw new Exception("Soubor s feedem pro upload " + uploadFile.getAbsolutePath() + " neexistuje.");
                }
            } else {
                controller.appendTextToLog("Nebylo možno se přihlásit k FTP serveru.");
                throw new Exception("Nelze se přihlásit k FTP serveru. Zkontrolujte prosím přihlašovací údaje.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze nahrát feed", "Nepodařilo se nahrát upravený XML feed:", e));
            controller.appendTextToLog("Upload XML feedu nebylo možno dokončit.");
            controller.appendTextToLog(e.getLocalizedMessage());
        } finally {
            try {
                if (client.isConnected()) {
                    client.logout();
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> Main.getInstance().showErrorDialog("Problém s odpojením od FTP", "Nastal problém s odpojením od FTP serveru:", e));
                controller.appendTextToLog("Nastal problém s odpojením od FTP serveru:");
                controller.appendTextToLog(e.getLocalizedMessage());
            }
        }
    }

    public void loadArdon() throws Exception {
        cancelled = false;
        Platform.runLater(() -> controller.getInfoMessage().setText("Zpracovávám stažený XML feed Ardonu"));
        controller.appendTextToLog("Zpracovávám stažený XML feed dodavatele Ardon.");
        ArdonMapper mapper = new ArdonMapper(unmarshallArdon());
        mapper.createOutput();
        TargetShop outputShop = mapper.getTarget();
        Map<String, String> catsMap = getCategoryMapping("conf/ardon-categories.txt");
        if (!cancelled) {
            outputShop.getItems().forEach(itm -> itm.setCategories(catsMap.get(itm.getCategories())));
            controller.appendTextToLog("Bude uloženo " + outputShop.getItems().size() + " položek.");
            marshallArdon(outputShop);
            correctCData(controller.getArdonProcessedFile());
        }
        if (!cancelled && controller.uploadFeedArdon()) {
            uploadFeed(controller.getArdonProcessedFile(), controller.getArdonUploadFeedUrl(), controller.getArdonUploadFeedRemoteDir(), Main.getInstance().getAppSettings().getProperty("ardon.upload.username"), Main.getInstance().getAppSettings().getProperty("ardon.upload.password"));
        }
    }

    public HashSet<String> getUnpairedCatsArdon() throws Exception {
        cancelled = false;
        final HashSet<String> unpaired = new HashSet<>();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám stažený XML feed Ardonu"));
        controller.appendTextToLog("Načítám stažený XML feed dodavatele Ardon pro zjištění nespárovaných kategorií.");
        ArdonMapper mapper = new ArdonMapper(unmarshallArdon());
        mapper.createOutput();
        TargetShop outputShop = mapper.getTarget();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám párování kategorií Ardonu"));
        controller.appendTextToLog("Načítám nastavení párování kategorií pro dodavatele ArdonSafety s.r.o.");
        Map<String, String> catsMap = getCategoryMapping("conf/ardon-categories.txt");
        Set<String> theirCats = catsMap.keySet();
        Platform.runLater(() -> controller.getInfoMessage().setText("Připravuji seznam nespárovaných kategorií Ardonu"));
        controller.appendTextToLog("Připravuji seznam nespárovaných kategorií pro dodavatele Ardon Safety s.r.o.");
        outputShop.getItems().forEach(itm -> {
            if (!theirCats.contains(itm.getCategories())) {
                unpaired.add(itm.getCategories());
            }
        });
        return unpaired;
    }

    public void loadLuma() throws Exception {
        cancelled = false;
        controller.appendTextToLog("Zahajuji zpracování XML feedu dodavatele Luma.");
        Platform.runLater(() -> controller.getInfoMessage().setText("Zpracovávám XML feed."));
        LumaShop shop = unmarshallLuma();
        Map<String, String> catsMap = getCategoryMapping("conf/luma-categories.txt");
        if (!cancelled) {
            shop.getItems().forEach(itm -> itm.setCategory(catsMap.get(itm.getCategory())));
            controller.appendTextToLog("Bude uloženo " + shop.getItems().size() + " položek.");
            marshallLuma(shop);
            correctCData(controller.getLumaProcessedFile());
            controller.appendTextToLog("XML feed dodavatele Luma byl zpracován.");
        }
        if (!cancelled && controller.uploadFeedLuma()) {
            uploadFeed(controller.getLumaProcessedFile(), controller.getLumaUploadFeedUrl(), controller.getLumaUploadFeedRemoteDir(), Main.getInstance().getAppSettings().getProperty("luma.upload.username"), Main.getInstance().getAppSettings().getProperty("luma.upload.password"));
        }
    }

    public HashSet<String> getUnpairedCatsLuma() throws Exception {
        cancelled = false;
        final HashSet<String> unpaired = new HashSet<>();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám stažený XML feed Lumy"));
        controller.appendTextToLog("Načítám stažený XML feed dodavatele Luma pro zjištění nespárovaných kategorií.");
        LumaShop shop = unmarshallLuma();
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám párování kategorií Lumy"));
        controller.appendTextToLog("Načítám nastavení párování kategorií pro dodavatele Luma Trading s.r.o.");
        Map<String, String> catsMap = getCategoryMapping("conf/luma-categories.txt");
        Set<String> theirCats = catsMap.keySet();
        Platform.runLater(() -> controller.getInfoMessage().setText("Připravuji seznam nespárovaných kategorií Lumy"));
        controller.appendTextToLog("Připravuji seznam nespárovaných kategorií pro dodavatele Luma Trading s.r.o.");
        shop.getItems().forEach(itm -> {
            if (!theirCats.contains(itm.getCategory())) {
                unpaired.add(itm.getCategory());
            }
        });
        return unpaired;
    }

    public void loadImagesArdon(boolean processOnly) throws Exception {
        cancelled = false;
        Platform.runLater(() -> controller.getInfoMessage().setText("Načítám seznam obrázků"));
        controller.appendTextToLog("Načítám seznam obrázků.");
        TargetShop loaded = unmarshallProcessedArdon();
        Set<String> images = new HashSet<>();
        loaded.getItems().forEach(itm -> {
            String[] imgarray = itm.getImages().split("\\|");
            images.addAll(Arrays.asList(imgarray));
            itm.setImages(itm.getImages().replaceAll(controller.getArdonImagesUrlPath(), controller.getArdonImagesUrlPathReplaceTo()));
        });
        if (processOnly) {
            controller.appendTextToLog("Obrázky nebudou staženy. Pouze se přepíše adresa ve feedu.");
            controller.appendTextToLog("!!! PRO STAŽENÍ OBRÁZKŮ JE NUTNO ZNOVU STÁHNOUT CELÝ FEED !!!");
        } else {
            controller.appendTextToLog("Počet obrázků ke stažení: " + images.size());
            int index = 1;
            for (String img : images) {
                if (!cancelled) {
                    try {
                        int finalIndex = index;
                        Platform.runLater(() -> controller.getInfoMessage().setText("Stahuji " + finalIndex + " z " + images.size() + ": " + img));
                        controller.appendTextToLog("Stahuji " + index + " z " + images.size() + ": " + img);
                        String fileName = img.substring(img.lastIndexOf("/") + 1);
                        downloadFile(img, controller.getArdonImagesFolder() + "/" + fileName);
                        Platform.runLater(() -> controller.getProgressBar().setProgress((double) finalIndex / (double) images.size()));
                    } catch (IOException e) {
                        //Optional - by ardon are most common malformed URL. e.printStackTrace();
                        controller.appendTextToLog("Nemohu stáhnout: " + img + ":\n" + e.getLocalizedMessage());
                    }
                    index++;
                } else {
                    break;
                }
            }
        }
        if (!cancelled) {
            controller.getProgressBar().setProgress(-1);
            Platform.runLater(() -> controller.getInfoMessage().setText("Zapisuji nové adresy obrázků do feedu"));
            controller.appendTextToLog("Zapisuji nové adresy obrázků do feedu.");
            marshallArdon(loaded);
            correctCData(controller.getArdonProcessedFile());
        }
    }

    public void uploadImagesArdon() {
        client = new FTPClient();
        controller.appendTextToLog("Zahajuji upload obrázků produktů dodavatele Ardon Safety, s.r.o..");
        Platform.runLater(() -> controller.getInfoMessage().setText("Nahrávám obrázky na FTP"));
        try {
            client.connect(controller.getImagesUrlUploadArdon());
            if (!client.isConnected()) {
                throw new Exception("Nelze se připojit k FTP serveru. Zkontrolujte prosím jeho adresu.");
            }
            if (client.login(Main.getInstance().getAppSettings().getProperty("ardon.upload.images.username"), Main.getInstance().getAppSettings().getProperty("ardon.upload.images.password"))) {
                controller.appendTextToLog("Přihlášení k FTP serveru bylo úspěšné.");
                if (!client.changeWorkingDirectory("/" + controller.getImagesUrlUploadRootArdon() + controller.getArdonImagesUrlPathReplaceTo())) {
                    client.changeToParentDirectory();
                }
                client.setFileType(FTP.BINARY_FILE_TYPE);
                List<Path> imagesToUpload = new ArrayList<>();
                try (Stream<Path> images = Files.walk(Paths.get(controller.getArdonImagesFolder()))) {
                    images.filter(file -> file.toString().endsWith(".jpg") || file.toString().endsWith(".jpeg") || file.toString().endsWith(".png"))
                            .forEach(imagesToUpload::add);
                }
                int imgNumber = 1;
                for (Path path : imagesToUpload) {
                    try (FileInputStream fis = new FileInputStream(path.toFile())) {
                        controller.appendTextToLog("Nahrávám obrázek č. " + imgNumber + " z " + imagesToUpload.size() + " - " + path.getFileName());
                        int finalImgNumber = imgNumber;
                        Platform.runLater(() -> controller.getInfoMessage().setText("Stahuji " + finalImgNumber + " z " + imagesToUpload.size() + ": " + path.getFileName().toString()));
                        Platform.runLater(() -> controller.getProgressBar().setProgress((double) finalImgNumber / (double) imagesToUpload.size()));
                        client.storeFile(path.getFileName().toString(), fis);
                    } catch (Exception e) {
                        controller.appendTextToLog("Nebylo možno nahrát obrázek č. " + imgNumber + " - " + path);
                        controller.appendTextToLog(e.getLocalizedMessage());
                    }
                    imgNumber++;
                }
            } else {
                controller.appendTextToLog("Nebylo možno se přihlásit k FTP serveru.");
                throw new Exception("Nelze se přihlásit k FTP serveru. Zkontrolujte prosím přihlašovací údaje.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> Main.getInstance().showErrorDialog("Nelze nahrát obrázky", "Nepodařilo se nahrát obrázky pro Ardon:", e));
            controller.appendTextToLog("Upload obrázků nebylo možno dokončit.");
            controller.appendTextToLog(e.getLocalizedMessage());
        } finally {
            try {
                if (client.isConnected()) {
                    client.logout();
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> Main.getInstance().showErrorDialog("Problém s odpojením od FTP", "Nastal problém s odpojením od FTP serveru pro upload obrázků:", e));
                controller.appendTextToLog("Nastal problém s odpojením od FTP serveru pro upload obrázků:");
                controller.appendTextToLog(e.getLocalizedMessage());
            }
        }
    }
}
