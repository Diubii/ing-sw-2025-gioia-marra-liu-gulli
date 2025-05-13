package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import javafx.event.ActionEvent;

public class CreateLobbyController extends GenericSceneController{
    private GuiJavaFx mainViewController;
    private Stage primaryStage;
    private ClientController clientController;  // Riferimento al controller del client

    public void initialSetup(GuiJavaFx mainViewController,ClientController clientController, Stage primaryStage) {
        this.clientController = clientController;
        this.primaryStage = primaryStage;
        this.mainViewController = mainViewController;
    }

    public void backToMainMenu(ActionEvent e) {
        mainViewController.askJoinOrCreateRoom();
    }
}
