package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import cz.jbenak.tezamv.ardonFeedConverter.Main;
import cz.jbenak.tezamv.ardonFeedConverter.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditDialog extends Stage {

    private CategoryMappingItem item;

    public EditDialog(Stage parent) {
        this.initOwner(parent);
        this.item = null;
    }

    public EditDialog(Stage parent, CategoryMappingItem item) {
        this.initOwner(parent);
        this.item = item;
    }

    protected void setItem(CategoryMappingItem item) {
        this.item = item;
    }

    public CategoryMappingItem showDialog() {
        try {
            this.setTitle(item == null ? "Nová položka" : "Úprava položky");
            this.setResizable(false);
            this.initModality(Modality.APPLICATION_MODAL);
            this.initStyle(StageStyle.UTILITY);
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("dialogs/edit-dialog.fxml"));
            this.setScene(new Scene(loader.load()));
            EditDialogController controller = loader.getController();
            controller.setDialog(this);
            controller.setItem(item);
            controller.setTitle(item == null ? "Nové mapování kategorií" : "Úprava mapování kategorií");
            this.showAndWait();
        } catch (Exception e) {
            Main.getInstance().showErrorDialog("Interní chyba", "Nelze zobrazit dialog pro úpravu položky mapování kategorií:", e);
        }
        return item;
    }
}
