package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;


/**
 * GUI Controller for the Main Menu Page.
 * <p>
 * Handles user interactions from the main menu, such as creating a new lobby,
 * listing available lobbies, or exiting the application.
 */
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



    /**
     * Handles the action when the user chooses to create a new lobby.
     * Triggers the controller to initiate lobby creation flow.
     *
     * @param e The action event from the button click.
     */
    public void newLobby(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        try{
            clientController.handleCreateOrJoinChoice("a");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Handles the action when the user chooses to list existing lobbies.
     * Triggers the controller to request available rooms from the server.
     *
     * @param e The action event from the button click.
     */
    public void listLobby(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        try{
            clientController.handleCreateOrJoinChoice("b");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Handles the action when the user chooses to exit the game.
     * Plays a sound and requests the main view controller to close the application.
     *
     * @param e The action event from the button click.
     */
    public void exit(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        //Chiamate a clientController per disconnessione se serve
        mainViewController.CloseApplication();
    }




}
