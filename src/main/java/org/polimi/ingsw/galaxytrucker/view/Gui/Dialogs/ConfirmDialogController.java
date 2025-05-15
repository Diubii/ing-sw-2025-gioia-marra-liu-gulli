package org.polimi.ingsw.galaxytrucker.view.Gui.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmDialogController {
    @FXML
    private Label messageLabel;

    private boolean confirmed = false;
    private Stage dialogStage;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleYes() {
        confirmed = true;
        dialogStage.close();
    }

    @FXML
    private void handleNo() {
        dialogStage.close();
    }
}

