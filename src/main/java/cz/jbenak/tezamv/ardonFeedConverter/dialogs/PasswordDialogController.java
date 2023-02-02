package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import cz.jbenak.tezamv.ardonFeedConverter.Main;
import cz.jbenak.tezamv.ardonFeedConverter.Utils;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PasswordDialogController implements Initializable {

    private Stage dialogStage;
    private String userProperty;
    private String passwordProperty;
    @FXML
    private Label title;
    @FXML
    private  MFXTextField fieldUser;
    @FXML
    private MFXPasswordField fieldPassword;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCredentialsProperties(String user, String password) {
        passwordProperty = password;
        userProperty = user;
        fieldUser.setText(Main.getInstance().getAppSettings().getProperty(user));
        fieldPassword.setText(Main.getInstance().getAppSettings().getProperty(password));
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    @FXML
    private void btnOkPressed() {
        Main.getInstance().getAppSettings().setProperty(userProperty, fieldUser.getText().trim());
        Main.getInstance().getAppSettings().setProperty(passwordProperty, fieldPassword.getText().trim());
        dialogStage.close();
    }

    @FXML
    private void bntCancelPressed() {
        dialogStage.close();
    }

    @FXML
    private void keyPressed(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ESCAPE) {
            bntCancelPressed();
        }
        if (evt.isAltDown() && evt.getCode() == KeyCode.ENTER) {
            btnOkPressed();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
