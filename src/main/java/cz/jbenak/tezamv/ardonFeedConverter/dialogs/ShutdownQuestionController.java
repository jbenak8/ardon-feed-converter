package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ShutdownQuestionController implements Initializable {

    private ShutdownQuestion dialog;

    protected void setDialog(ShutdownQuestion dialog) {
        this.dialog = dialog;
    }

    @FXML
    private void yesPressed() {
        dialog.yes();
    }

    @FXML
    private void noPressed() {
        dialog.no();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
