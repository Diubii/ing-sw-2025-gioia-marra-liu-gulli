package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class LoginConnectController extends GenericSceneController {

    @FXML private Label TxtError;
    @FXML private javafx.scene.control.TextField TxfServerAddr;
    @FXML private javafx.scene.control.TextField TxfPort;
    @FXML private javafx.scene.control.TextField TxfNickname;
    @FXML private RadioButton socketRadio;
    @FXML private RadioButton RMIradio;

    private GuiJavaFx mainViewController;
    private Stage primaryStage;
    private  MusicManager musicManager;


    public LoginConnectController() {}


    // Metodo per impostare il riferimento al controller del client
    public void initialSetupSmall(GuiJavaFx mainViewController,Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;
            }

    public void updateHint(){
        TxfPort.setPromptText(socketRadio.isSelected() ? "Default Socket: 5000" : "Default RMI: 1099");
    }

    @Override
    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController, ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        //Not used
    }

    public void ShowGenericMessage(String message){
        TxtError.setText(message);
    }

    @Override
    public String pageName() {
        return "LoginConnectPage";
    }

    public void ConnectToServer(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        int port;
        String address = TxfServerAddr.getText().trim();

        ClientController controller = new ClientController(null, socketRadio.isSelected()); // true = socket, false = RMI
        controller.setView(mainViewController);
        mainViewController.initializeController(controller);

        //Default se vuoto
        if (address.isEmpty()) {address="127.0.0.1";}
        //Default se vuoto
        if(TxfPort.getText().isEmpty()){
            port = controller.getIsSocket() ? 5000 : 1099;
        }
        else{
            try {
                port = Integer.parseInt(TxfPort.getText().trim());
            } catch (NumberFormatException ex) {
                TxtError.setText("Invalid port number.");
                return;
            }
        }
        //Si effettua connessione
        try{
            controller.handleServerInfo(new SERVER_INFO(address, port));
        } catch (Exception ex) {
            TxfPort.setText(ex.toString());
            return;
        }
        //Se ha successo si manda Nickname
        try {
            controller.handleNicknameInput(TxfNickname.getText().trim());
        } catch (IOException ex) {
            TxtError.setText("IOException.");
        } catch (ExecutionException ex) {
            TxtError.setText("ExecutionException.");
        } catch (InterruptedException ex) {
            TxtError.setText("InterruptedException.");
        }



    }
}
