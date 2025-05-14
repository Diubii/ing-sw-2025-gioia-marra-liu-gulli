package org.polimi.ingsw.galaxytrucker.view.Gui;

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
    private Stage primaryStage;
    private ClientController clientController;  // Riferimento al controller del client

    public void initialSetup(GuiJavaFx mainViewController,ClientController clientController, Stage primaryStage) {
        this.clientController = clientController;
        this.primaryStage = primaryStage;
        this.mainViewController = mainViewController;
        playWavSound("NGGYU.wav");
    }

    public void ShowGenericMessage(String message) {
        TxtErr.setText(message);
    }

    private static void playWavSound(String sound){
            try {
                InputStream raw = MainMenuController.class.getResourceAsStream("/org/polimi/ingsw/galaxytrucker/Sounds/"+sound);
                if (raw == null) {
                    throw new IllegalArgumentException("File audio non trovato!");
                }

                // Wrappa in BufferedInputStream
                BufferedInputStream bufferedIn = new BufferedInputStream(raw);

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void newLobby(ActionEvent e) {
        try{
            clientController.handleCreateOrJoinChoice("a");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void listLobby(ActionEvent e) {
        try{

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exit(ActionEvent e) {
        //Chiamate a clientController per disconnessione se serve

        //Chiusura tutto, ad esempio la musica va fermata

        //chiusura finestra
        primaryStage.close();
    }


}
