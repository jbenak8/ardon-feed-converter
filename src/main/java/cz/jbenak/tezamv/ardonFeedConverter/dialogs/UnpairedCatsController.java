package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

public class UnpairedCatsController implements Initializable {

    private Stage dialogStage;
    @FXML
    private Label title;
    @FXML
    private TextArea content;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setItems(HashSet<String> items) {
        if (items == null || items.isEmpty()) {
            content.setText("Nebyly zjištěny žádné nespárované kategorie.");
        } else {
            StringBuilder sb = new StringBuilder();
            items.forEach(itm -> sb.append(itm).append("\n"));
            content.setText(sb.toString());
        }
    }

    public void setTitle(String supplierName) {
        title.setText(title.getText().replace("${supplier}", supplierName));
    }

    @FXML
    private void copyToClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(content.getString());
        clipboard.setContent(content);
    }

    @FXML
    private void close() {
        dialogStage.close();
    }

    @FXML
    private void keyPressed(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ESCAPE) {
            close();
        }
        if (evt.isControlDown() && evt.getCode() == KeyCode.C) {
            copyToClipboard();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
