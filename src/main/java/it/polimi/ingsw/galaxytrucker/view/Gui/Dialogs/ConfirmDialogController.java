package it.polimi.ingsw.galaxytrucker.view.Gui.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
/**
 * Controller for a confirmation dialog in the GUI.
 * Provides functionality for displaying a message and handling user confirmation.
 */
public class ConfirmDialogController {
    @FXML
    private Label messageLabel;
    private boolean confirmed = false;
    private Stage dialogStage;


    /**
     * Handles the "Yes" button click. Marks the confirmation and closes the dialog.
     */
    @FXML
    private void handleYes() {
        confirmed = true;
        dialogStage.close();
    }
    /**
     * Handles the "No" button click. Simply closes the dialog.
     */
    @FXML
    private void handleNo() {
        dialogStage.close();
    }
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    public boolean isConfirmed() {
        return confirmed;
    }

}

