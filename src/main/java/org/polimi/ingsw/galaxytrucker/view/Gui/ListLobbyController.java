package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;

public class ListLobbyController extends GenericSceneController{

    @FXML  private Text TxtErr;
    private GuiJavaFx mainViewController;
    private Stage primaryStage;
    private ClientController clientController;  // Riferimento al controller del client

    public void initialSetup(GuiJavaFx mainViewController,ClientController clientController, Stage primaryStage) {
        this.clientController = clientController;
        this.primaryStage = primaryStage;
        this.mainViewController = mainViewController;
    }

    public void ShowGenericMessage(String message) {
        TxtErr.setText(message);
    }

}
