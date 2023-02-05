package cz.jbenak.tezamv.ardonFeedConverter;

import cz.jbenak.tezamv.ardonFeedConverter.dialogs.ShutdownQuestion;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.i18n.Language;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Properties;

public class Main extends Application {

    public static final String VERSION = "1.0.0.0";
    private static Main instance;
    private Properties appSettings;
    private boolean isProcessing = false;
    private Stage mainStage;
    private MainController mainController;

    public Main() {
        super();
        synchronized (Main.class) {
            if (instance != null) {
                throw new UnsupportedOperationException("Main application class loaded twice!");
            }
            instance = this;
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public Properties getAppSettings() {
        return appSettings;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) {
        try {
            I18N.setLanguage(Language.CZECH);
            loadSettings();
            mainStage = stage;
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("main.fxml"));
            stage.setScene(new Scene(loader.load()));
            mainController = loader.getController();
            stage.setTitle("TEZA MV feed converter (Ardon, Luma)");
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("app-icon-v3.png"))));
            stage.setResizable(false);
            stage.setOnCloseRequest(windowEvent -> {
                windowEvent.consume();
                if (isProcessing) {
                    ShutdownQuestion question = new ShutdownQuestion();
                    if (question.showDialog()) {
                        saveSettings();
                        Platform.exit();
                    }
                } else {
                    saveSettings();
                    Platform.exit();
                }
            });
            stage.show();
        } catch (Exception e) {
            showErrorDialog("Program nelze spustit", "Nastala chyba při spouštění programu:", e);
            Platform.exit();
        }
    }

    public void showErrorDialog(String title, String header, Exception e) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(title);
        error.setHeaderText(header);
        error.setContentText(e.getLocalizedMessage());
        error.showAndWait();
    }

    private void loadSettings() throws Exception {
        appSettings = new Properties();
        try (FileInputStream fis = new FileInputStream("conf/app.properties")) {
            appSettings.load(fis);
        }
        if (appSettings.isEmpty()) {
            throw new RuntimeException("Soubor nastavení programu je prázdný. Přeinstalujte program.");
        }
        appSettings.setProperty("ardon.upload.password", Utils.getStringEncryptor().decrypt(appSettings.getProperty("ardon.upload.password", "")));
        appSettings.setProperty("ardon.upload.username", Utils.getStringEncryptor().decrypt(appSettings.getProperty("ardon.upload.username", "")));
        appSettings.setProperty("luma.upload.password", Utils.getStringEncryptor().decrypt(appSettings.getProperty("luma.upload.password", "")));
        appSettings.setProperty("luma.upload.username", Utils.getStringEncryptor().decrypt(appSettings.getProperty("luma.upload.username", "")));
        appSettings.setProperty("ardon.upload.images.password", Utils.getStringEncryptor().decrypt(appSettings.getProperty("ardon.upload.images.password", "")));
        appSettings.setProperty("ardon.upload.images.username", Utils.getStringEncryptor().decrypt(appSettings.getProperty("ardon.upload.images.username", "")));
    }

    private void saveSettings() {
        mainController.appendTextToLog("Ukládám nastavení.");
        mainController.setProperties();
        appSettings.setProperty("ardon.upload.password", Utils.getStringEncryptor().encrypt(appSettings.getProperty("ardon.upload.password")));
        appSettings.setProperty("ardon.upload.username", Utils.getStringEncryptor().encrypt(appSettings.getProperty("ardon.upload.username")));
        appSettings.setProperty("luma.upload.password", Utils.getStringEncryptor().encrypt(appSettings.getProperty("luma.upload.password")));
        appSettings.setProperty("luma.upload.username", Utils.getStringEncryptor().encrypt(appSettings.getProperty("luma.upload.username")));
        appSettings.setProperty("ardon.upload.images.password", Utils.getStringEncryptor().encrypt(appSettings.getProperty("ardon.upload.images.password", "")));
        appSettings.setProperty("ardon.upload.images.username", Utils.getStringEncryptor().encrypt(appSettings.getProperty("ardon.upload.images.username", "")));
        try {
            try (FileOutputStream fos = new FileOutputStream("conf/app.properties")) {
                appSettings.store(fos, null);
            }
        } catch (Exception e) {
            showErrorDialog("Nastavení nelze uložit", "Nastala chyba při ukládání nastavení:", e);
        }
    }
}