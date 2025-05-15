package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import javafx.event.ActionEvent; //OCCHIO A INCLUDERE GIUSTE
import javafx.fxml.FXML;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;


public class MainMenuController extends GenericSceneController {

    @FXML private Label TxtErr;

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
