package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class LoginConnectController extends GenericSceneController {

    @FXML private Text TxtError;
    @FXML private javafx.scene.control.TextField TxfServerAddr;
    @FXML private javafx.scene.control.TextField TxfPort;
    @FXML private javafx.scene.control.TextField TxfNickname;

    private GuiJavaFx mainViewController;
    private  Stage primaryStage;
    private ClientController clientController;  // Riferimento al controller del client

    public LoginConnectController() {}


    // Metodo per impostare il riferimento al controller del client
    public void initialSetup(GuiJavaFx mainViewController,ClientController clientController, Stage primaryStage) {
        this.clientController = clientController;
        this.primaryStage = primaryStage;
        this.mainViewController = mainViewController;
        TxfPort.setPromptText(this.clientController.getIsSocket() ? "Default Socket: 5000" : "Default RMI: 1099");
    }


    public void ConnectToServer(ActionEvent e) {
        int port;
        String address = TxfServerAddr.getText().trim();

        //Default se vuoto
        if (address.isEmpty()) {address="127.0.0.1";}
        //Default se vuoto
        if(TxfPort.getText().isEmpty()){
            port = this.clientController.getIsSocket() ? 5000 : 1099;
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
            //Todo: Mattia dovrebbe lanciare eccezioni non fare direttamente view.ShowGenericMessage
            clientController.handleServerInfo(new SERVER_INFO(address, port));
        } catch (Exception ex) {
            TxfPort.setText(ex.toString());
            return;
        }
        //Se ha successo si manda Nickname
        try {
            clientController.handleNicknameInput(TxfNickname.getText().trim());
        } catch (IOException ex) {
            TxtError.setText("IOException.");
        } catch (ExecutionException ex) {
            TxtError.setText("ExecutionException.");
        } catch (InterruptedException ex) {
            TxtError.setText("InterruptedException.");
        }



    }
}
