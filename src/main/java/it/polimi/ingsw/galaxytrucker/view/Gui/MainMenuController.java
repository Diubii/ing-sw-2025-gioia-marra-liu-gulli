package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;


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

    @Override
    public String pageName() {
        return "MainMenuPage";
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
