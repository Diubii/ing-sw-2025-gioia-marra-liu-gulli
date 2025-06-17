package org.polimi.ingsw.galaxytrucker.network.client.rmi;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.polimi.ingsw.galaxytrucker.network.server.RMIClientHandler;
import org.polimi.ingsw.galaxytrucker.network.server.ServerRMIInterface;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientRMI extends UnicastRemoteObject implements ClientInterfaceRMI, Observable {

    private final ArrayList<Observer> observers = new ArrayList<Observer>();
    Registry registry;
    ServerRMIInterface server;
    ClientInterfaceRMI stub;

    NetworkMessageNameVisitor nmnv = new NetworkMessageNameVisitor();

    public ClientRMI(int port, ClientController controller) throws RemoteException, NotBoundException {
        super();
            registry = LocateRegistry.getRegistry("localhost", port);
            server = (ServerRMIInterface) registry.lookup("GameServer");
            stub = (ClientInterfaceRMI) this;

            addObserver(controller);
            server.handleRMIRegistration(stub);
    }

    @Override
    public void sendMessage(NetworkMessage message) throws RemoteException{
        if(server == null) throw new RemoteException();

        RMIClientHandler handler = server.getClientHandler(this);

        NetworkMessageVisitor nmv = new NetworkMessageVisitor(server.getControllerHandles(), handler);
        message.accept(nmv);
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
            observer.update(message);
        }
    }

    @Override
    public void notifyObservers(String message) throws IOException, ExecutionException {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
