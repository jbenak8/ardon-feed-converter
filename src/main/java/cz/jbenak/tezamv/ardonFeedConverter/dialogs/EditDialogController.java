package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDialogController implements Initializable {

    private EditDialog dialog;
    private CategoryMappingItem item;
    @FXML
    private Label title;
    @FXML
    private MFXTextField fieldOurCategory;
    @FXML
    private MFXTextField fieldTheirCategory;

    public void setDialog(EditDialog dialog) {
        this.dialog = dialog;
    }

    public void setItem(CategoryMappingItem item) {
        this.item = item;
        if (item != null) {
            fieldOurCategory.setText(item.getOurCategory());
            fieldTheirCategory.setText(item.getTheirCategory());
        }
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    @FXML
    private void btnOkPressed() {
        if (item == null) {
            item = new CategoryMappingItem();
        }
        item.setTheirCategory(fieldTheirCategory.getText().trim());
        item.setOurCategory(fieldOurCategory.getText().trim());
        dialog.setItem(item);
        dialog.close();
    }

    @FXML
    private void bntCancelPressed() {
        dialog.close();
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
