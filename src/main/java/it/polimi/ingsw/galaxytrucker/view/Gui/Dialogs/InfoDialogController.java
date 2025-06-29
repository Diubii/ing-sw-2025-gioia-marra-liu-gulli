package it.polimi.ingsw.galaxytrucker.view.Gui.Dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for an informational dialog window in the GUI.
 * Displays a title and a message to the user with an option to close the dialog.
 */
public class InfoDialogController {

    @FXML private Label messageLabel;
    @FXML private Label titleLabel;
    private Stage dialogStage;

    /**
     * Sets the stage (window) for this dialog.
     *
     * @param stage the dialog's {@link Stage}
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Sets the title and message to be displayed in the dialog.
     * If the title is {@code null}, a default title is used.
     *
     * @param title   the dialog title, or {@code null} to use default
     * @param message the message body
     */
    public void setMessage(String title,String message) {
        if(title == null){
            titleLabel.setText("Comunicazione di servizio");
        }
        else{
            titleLabel.setText(title);
        }
        messageLabel.setText(message);
    }


    /**
     * Handles the "Yes" button click.
     * Closes the dialog.
     */
    @FXML
    private void handleYes() {
        dialogStage.close();
    }


    /**
     * Handles the "No" button click.
     * Closes the dialog.
     */
    @FXML
    private void handleNo() {
        dialogStage.close();
    }
}

