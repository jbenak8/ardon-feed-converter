package cz.jbenak.tezamv.ardonFeedConverter.dialogs;

import cz.jbenak.tezamv.ardonFeedConverter.Main;
import cz.jbenak.tezamv.ardonFeedConverter.MainController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class ShutdownQuestion extends Stage {

    private boolean quit;

    public ShutdownQuestion() {
        this.initOwner(Main.getInstance().getMainStage());
        this.setTitle("Ukončit program?");
        this.setResizable(false);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
    }

    public boolean showDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("dialogs/shutdown-question.fxml"));
            this.setScene(new Scene(loader.load()));
            ShutdownQuestionController controller = loader.getController();
            controller.setDialog(this);
            Toolkit.getDefaultToolkit().beep();
            this.showAndWait();
        } catch (Exception e) {
            Main.getInstance().showErrorDialog("Interní chyba", "Nelze zobrazit dotaz na ukončení procesu. Aplikace bude nuceně ukončena.", e);
            Platform.exit();
        }
        return quit;
    }

    protected void yes() {
        quit = true;
        this.close();
    }

    protected void no() {
        quit = false;
        this.close();
    }
}
