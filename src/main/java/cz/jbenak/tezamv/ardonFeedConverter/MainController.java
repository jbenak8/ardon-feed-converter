package cz.jbenak.tezamv.ardonFeedConverter;

import cz.jbenak.tezamv.ardonFeedConverter.dialogs.CatsMappingDialogController;
import cz.jbenak.tezamv.ardonFeedConverter.dialogs.InfoDialogController;
import cz.jbenak.tezamv.ardonFeedConverter.dialogs.PasswordDialogController;
import cz.jbenak.tezamv.ardonFeedConverter.engine.Engine;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.*;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import static cz.jbenak.tezamv.ardonFeedConverter.dialogs.CatsMappingDialogController.CatsType;

public class MainController implements Initializable {

    @FXML
    private MFXProgressBar progressBar;
    @FXML
    private Label infoMessage;
    @FXML
    private TextArea logView;
    @FXML
    private MFXButton btnStop;
    @FXML
    private MFXCheckbox loadFeedArdon;
    @FXML
    private MFXCheckbox loadFeedLuma;
    @FXML
    private TextField feedUrlArdon;
    @FXML
    private TextField downloadedFileArdon;
    @FXML
    private TextField processedFileArdon;
    @FXML
    private TextField feedUploadArdon;
    @FXML
    private TextField imagesFolderArdon;
    @FXML
    private TextField feedUrlLuma;
    @FXML
    private TextField downloadedFileLuma;
    @FXML
    private TextField processedFileLuma;
    @FXML
    private TextField feedUploadLuma;
    @FXML
    private TextField imagesUrlArdon;
    @FXML
    private TextField imagesUrlReplaceArdon;
    @FXML
    private MFXButton btnStartArdon;
    @FXML
    private MFXButton btnStartLuma;
    @FXML
    private MFXCheckbox notDownloadImagesArdon;
    @FXML
    private TextField uploadImagesUrlArdon;
    private final Engine engine = new Engine(this);

    @FXML
    private void selectDownloadedFeedArdon() {
        openSaveDialog(downloadedFileArdon, false, "ArdonFeedStazeno.xml");
    }

    @FXML
    private void selectProcessedFeedArdon() {
        openSaveDialog(processedFileArdon, false, "ArdonKImportu.xml");
    }

    @FXML
    private void selectImageDirectoryArdon() {
        openSaveDialog(imagesFolderArdon, true, null);
    }

    @FXML
    private void selectDownloadedFeedLuma() {
        openSaveDialog(downloadedFileLuma, false, "LumaFeedStazeno.xml");
    }

    @FXML
    private void selectProcessedFeedLuma() {
        openSaveDialog(processedFileLuma, false, "LumaKImportu.xml");
    }

    public MFXProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getInfoMessage() {
        return infoMessage;
    }

    public String getArdonFeedUrl() {
        return feedUrlArdon.getText().trim();
    }

    public String getLumaFeedUrl() {
        return feedUrlLuma.getText().trim();
    }

    public String getArdonDownloadedFile() {
        return downloadedFileArdon.getText().trim();
    }

    public String getLumaDownloadedFile() {
        return downloadedFileLuma.getText().trim();
    }

    public String getArdonProcessedFile() {
        return processedFileArdon.getText().trim();
    }

    public String getLumaProcessedFile() {
        return processedFileLuma.getText().trim();
    }

    public String getArdonImagesFolder() {
        return imagesFolderArdon.getText().trim();
    }

    public String getArdonImagesUrlPath() {
        return imagesUrlArdon.getText().trim();
    }

    public String getArdonImagesUrlPathReplaceTo() {
        return imagesUrlReplaceArdon.getText().trim();
    }

    public MFXButton getBtnStop() {
        return btnStop;
    }

    public MFXButton getBtnStartArdon() {
        return btnStartArdon;
    }

    public MFXButton getBtnStartLuma() {
        return btnStartLuma;
    }

    private void openSaveDialog(TextField targetField, boolean directory, String suggestedFileName) {
        if (directory) {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Uložit obrázky do...");
            File selectedDir = dc.showDialog(Main.getInstance().getMainStage());
            if (selectedDir != null) {
                targetField.setText(selectedDir.getAbsolutePath());
            }
        } else {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Soubory XML", ".xml"));
            fc.setInitialFileName(suggestedFileName);
            fc.setTitle("Uložit jako...");
            File selected = fc.showSaveDialog(Main.getInstance().getMainStage());
            if (selected != null) {
                targetField.setText(selected.getAbsolutePath());
            }
        }
    }

    private void openCredentialsDialog(String title, String userProperty, String passwordProperty) {
        try {
            Stage credStage = new Stage();
            credStage.initOwner(Main.getInstance().getMainStage());
            credStage.setTitle("Přístupové údaje pro FTP");
            credStage.setResizable(false);
            credStage.initModality(Modality.APPLICATION_MODAL);
            credStage.initStyle(StageStyle.UTILITY);
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("dialogs/password-dialog.fxml"));
            credStage.setScene(new Scene(loader.load()));
            PasswordDialogController controller = loader.getController();
            controller.setDialogStage(credStage);
            controller.setTitle(title);
            controller.setCredentialsProperties(userProperty, passwordProperty);
            credStage.show();
        } catch (Exception e) {
            Main.getInstance().showErrorDialog("Interní chyba", "Nelze zobrazit dialog pro změnu přihlašovacích údajů k FTP uploadu:", e);
        }
    }

    @FXML
    private void openCredentialsDialogArdon() {
        openCredentialsDialog("Přístup pro upload ARDON", "ardon.upload.username", "ardon.upload.password");
    }

    @FXML
    private void openCredentialsDialogLuma() {
        openCredentialsDialog("Přístup pro upload Luma", "luma.upload.username", "luma.upload.password");
    }

    @FXML
    private void openCredentialsDialogImagesUpload() {
        openCredentialsDialog("Přístup pro upload obrázků", "ardon.upload.images.username", "ardon.upload.images.password");
    }

    @FXML
    private void openInfoDialog() {
        try {
            Stage infoStage = new Stage();
            infoStage.initOwner(Main.getInstance().getMainStage());
            infoStage.setTitle("O aplikaci");
            infoStage.setResizable(false);
            infoStage.initModality(Modality.APPLICATION_MODAL);
            infoStage.initStyle(StageStyle.UTILITY);
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("dialogs/info-dialog.fxml"));
            infoStage.setScene(new Scene(loader.load()));
            InfoDialogController controller = loader.getController();
            controller.setDialogStage(infoStage);
            infoStage.show();
        } catch (Exception e) {
            Main.getInstance().showErrorDialog("Interní chyba", "Nelze zobrazit dialog O Aplikaci:", e);
        }
    }

    @FXML
    private void openArdonCats() {
        openCatsEditDialog(CatsType.ARDON);
    }

    @FXML
    private void openLumaCats() {
        openCatsEditDialog(CatsType.LUMA);
    }

    @FXML
    private void startProcessArdon() {
        engine.processArdon();
    }

    @FXML
    private void startProcessLuma() {
        engine.processLuma();
    }

    @FXML
    private void startDownloadImagesArdon() {
        engine.downloadArdonImages(notDownloadImagesArdon.isSelected());
    }

    @FXML
    private void stopProcess() {
        engine.getCurrentTask().cancel(true);
    }

    private void openCatsEditDialog(CatsType type) {
        try {
            Stage catsStage = new Stage();
            catsStage.initOwner(Main.getInstance().getMainStage());
            catsStage.setTitle("Úprava mapování kategorií");
            catsStage.setResizable(true);
            catsStage.initModality(Modality.APPLICATION_MODAL);
            catsStage.initStyle(StageStyle.DECORATED);
            catsStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("app-icon.png"))));
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("dialogs/cats-mapping-dialog.fxml"));
            catsStage.setScene(new Scene(loader.load()));
            CatsMappingDialogController controller = loader.getController();
            controller.setDialogStage(catsStage);
            controller.setTypeAndLoad(type);
            catsStage.show();
        } catch (Exception e) {
            Main.getInstance().showErrorDialog("Interní chyba", "Nelze zobrazit dialog pro úpravu mapování kategorií:", e);
        }
    }

    @FXML
    private void copyLogToClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        String sb = "Protokol aplikace XML Feed converter pro TEZA MV s.r.o\n" +
                "Verze Javy: " + System.getProperty("java.vendor") + " - " + System.getProperty("java.version") + "\n" +
                "Operační systém: " + System.getProperty("os.name") + ", verze " + System.getProperty("os.version") + "\n" +
                logView.getText();
        content.putString(sb);
        clipboard.setContent(content);
    }

    @FXML
    private void emptyLog() {
        logView.clear();
    }

    public void appendTextToLog(String text) {
        logView.appendText(text + "\n");
    }

    public void setProperties() {
        Properties settings = Main.getInstance().getAppSettings();
        settings.setProperty("ardon.feedUrl", feedUrlArdon.getText().trim());
        settings.setProperty("luma.feedUrl", feedUrlLuma.getText().trim());
        settings.setProperty("luma.inputFile", downloadedFileLuma.getText().trim());
        settings.setProperty("luma.outputFile", processedFileLuma.getText().trim());
        settings.setProperty("luma.uploadUrl", feedUploadLuma.getText().trim());
        settings.setProperty("ardon.inputFile", downloadedFileArdon.getText().trim());
        settings.setProperty("ardon.outputFile", processedFileArdon.getText().trim());
        settings.setProperty("ardon.imageFolder", imagesFolderArdon.getText().trim());
        settings.setProperty("ardon.uploadUrl", feedUploadArdon.getText().trim());
        settings.setProperty("ardon.uploadEnabled", loadFeedArdon.isSelected() ? "true" : "false");
        settings.setProperty("luma.uploadEnabled", loadFeedLuma.isSelected() ? "true" : "false");
        settings.setProperty("ardon.imagesPath", imagesUrlArdon.getText().trim());
        settings.setProperty("ardon.imagesPath.replaceTo", imagesUrlReplaceArdon.getText().trim());
        settings.setProperty("ardon.notDownloadImages", notDownloadImagesArdon.isSelected() ? "true" : "false");
        settings.setProperty("ardon.upload.images.url", uploadImagesUrlArdon.getText().trim());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setVisible(false);
        btnStop.setDisable(true);
        infoMessage.setText("Program je připraven.");
        logView.appendText("Ukládání nastavení probíhá automaticky pro ukončení programu.\n");
        feedUrlArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.feedUrl"));
        downloadedFileArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.inputFile"));
        processedFileArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.outputFile"));
        feedUploadArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.uploadUrl"));
        imagesFolderArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.imageFolder"));
        feedUrlLuma.setText(Main.getInstance().getAppSettings().getProperty("luma.feedUrl"));
        downloadedFileLuma.setText(Main.getInstance().getAppSettings().getProperty("luma.inputFile"));
        processedFileLuma.setText(Main.getInstance().getAppSettings().getProperty("luma.outputFile"));
        feedUploadLuma.setText(Main.getInstance().getAppSettings().getProperty("luma.uploadUrl"));
        loadFeedArdon.setSelected(Boolean.parseBoolean(Main.getInstance().getAppSettings().getProperty("ardon.uploadEnabled", "false")));
        loadFeedLuma.setSelected(Boolean.parseBoolean(Main.getInstance().getAppSettings().getProperty("luma.uploadEnabled", "false")));
        notDownloadImagesArdon.setSelected(Boolean.parseBoolean(Main.getInstance().getAppSettings().getProperty("ardon.notDownloadImages", "false")));
        imagesUrlArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.imagesPath"));
        imagesUrlReplaceArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.imagesPath.replaceTo"));
        uploadImagesUrlArdon.setText(Main.getInstance().getAppSettings().getProperty("ardon.upload.images.url"));
    }
}
