package org.polimi.ingsw.galaxytrucker.view.Gui.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InfoDialogController {

    @FXML private Label messageLabel;
    @FXML private Label titleLabel;

    private Stage dialogStage;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setMessage(String title,String message) {
        if(title == null){
            titleLabel.setText("Comunicazione di servizio");
        }
        else{
            titleLabel.setText(title);
        }
        messageLabel.setText(message);
    }


    @FXML
    private void handleYes() {
        dialogStage.close();
    }

    @FXML
    private void handleNo() {
        dialogStage.close();
    }
}

