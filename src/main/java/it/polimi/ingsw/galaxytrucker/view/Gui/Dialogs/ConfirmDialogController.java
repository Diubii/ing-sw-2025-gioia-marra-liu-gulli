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

    /**
     * Sets the {@link Stage} object for this dialog.
     *
     * @param stage the stage to associate with this dialog
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    /**
     * Sets the message to be displayed in the dialog.
     *
     * @param message the message text to display
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    /**
     * Returns whether the user confirmed the action.
     *
     * @return {@code true} if the user clicked "Yes", {@code false} otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }

}

