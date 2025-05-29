package org.polimi.ingsw.galaxytrucker.network.client.rmi;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.polimi.ingsw.galaxytrucker.network.server.ServerRMIInterface;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientRMI extends UnicastRemoteObject implements ClientInterfaceRMI, Observable {

    private final ArrayList<Observer> observers = new ArrayList<Observer>();
    Registry registry;
    ServerRMIInterface server;
    ClientInterfaceRMI stub;

    public ClientRMI(int port, ClientController controller) throws RemoteException {
        super();
        try {
            registry = LocateRegistry.getRegistry("localhost", port);
            server = (ServerRMIInterface) registry.lookup("GameServer");
            stub = (ClientInterfaceRMI) this;

            addObserver(controller);
            server.handleRMIRegistration(stub);


        } catch (Exception e) {
            System.err.println("Couldn't create the ClientRMI: " + e.getMessage());
        }
    }

    @Override
    public void sendMessage(NetworkMessage message) {
        try {
            server.receiveMessage(message, stub);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerRMI, TuiColor.BRIGHT_YELLOW, "Couldn't send message: " + e.getMessage()));
        }
    }

    @Override
    public void receiveMessage(NetworkMessage message){
        new Thread(() -> {
            try {
                notifyObservers(message);
            } catch (IOException | ExecutionException | InvalidTilePosition e) {
                System.err.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerRMI, TuiColor.BRIGHT_YELLOW, "Couldn't receive message: " + e.getMessage()));
            }
        }).start();

    }


    @Override
    public void addObserver(Observer observer) {

        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(NetworkMessage message) throws IOException, ExecutionException, InvalidTilePosition {


        for (Observer observer : observers) {
//            System.out.println("i\n");
            try {
                observer.update(message);
            } catch (TooManyPlayersException | PlayerAlreadyExistsException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void notifyObservers(String message) throws IOException, ExecutionException {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
