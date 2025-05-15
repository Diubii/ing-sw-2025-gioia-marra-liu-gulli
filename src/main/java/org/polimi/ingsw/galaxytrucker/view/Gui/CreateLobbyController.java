package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import javafx.event.ActionEvent;

public class CreateLobbyController extends GenericSceneController{

    @FXML
    private Text TxtErr;

    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private Stage primaryStage;
    private MusicManager musicManager;


    public void initialSetup(GuiJavaFx mainViewController,ClientController clientController,Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;

    }

    @Override
    public void ShowGenericMessage(String message) {
        TxtErr.setText(message);
    }

    public void backToMainMenu(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        mainViewController.askJoinOrCreateRoom();
    }
}
