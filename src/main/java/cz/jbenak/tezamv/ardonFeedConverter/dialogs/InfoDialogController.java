package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import cz.jbenak.tezamv.ardonFeedConverter.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoDialogController implements Initializable {

    private Stage dialogStage;
    @FXML
    private Label os;
    @FXML
    private Label java;
    @FXML
    private Label app;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void btnPressed(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ENTER || evt.getCode() == KeyCode.ESCAPE) {
            closedPressed();
        }
    }

    @FXML
    private void closedPressed() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        app.setText(Main.VERSION);
        java.setText(System.getProperty("java.vendor") + " - " + System.getProperty("java.version"));
        os.setText(System.getProperty("os.name") + ", verze " + System.getProperty("os.version"));
    }
}
