package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import javafx.event.ActionEvent; //OCCHIO A INCLUDERE GIUSTE
import javafx.fxml.FXML;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;


public class MainMenuController extends GenericSceneController {

    @FXML private Label TxtErr;

    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;


    public void initialSetup(GuiJavaFx mainViewController,ClientController clientController,ClientModel mymodel,Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;
    }

    public void ShowGenericMessage(String message) {
        TxtErr.setText(message);
    }



    public void newLobby(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        try{
            clientController.handleCreateOrJoinChoice("a");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void listLobby(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        try{
            clientController.handleCreateOrJoinChoice("b");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exit(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        //Chiamate a clientController per disconnessione se serve
        mainViewController.CloseApplication();
    }




}
