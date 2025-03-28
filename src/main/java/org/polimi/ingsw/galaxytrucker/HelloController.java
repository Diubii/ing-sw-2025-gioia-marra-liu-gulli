package org.polimi.ingsw.galaxytrucker;

import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void connectToServerSocket() throws IOException {
        Socket socket = new Socket("localhost", 6969);
    }

    @FXML
    protected void connectToServerRMI() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 42069);
        ProvaRMI stub = (ProvaRMI) registry.lookup("prova_rmi");
        welcomeText.setText("RMI Server says: " + stub.sayHelloRMI());
    }
}