package it.polimi.ingsw.galaxytrucker.network.client.rmi;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.server.RMIClientHandler;
import it.polimi.ingsw.galaxytrucker.network.server.ServerRMIInterface;
import it.polimi.ingsw.galaxytrucker.observer.Observable;
import it.polimi.ingsw.galaxytrucker.observer.Observer;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of the RMI client for Galaxy Trucker.
 * Handles communication with the server using Java RMI and dispatches received messages to observers.
 * Acts as both an RMI remote object and an {@link Observable} for message propagation.
 */
public class ClientRMI extends UnicastRemoteObject implements ClientInterfaceRMI, Observable {

    private final ArrayList<Observer> observers = new ArrayList<Observer>();
    Registry registry;
    ServerRMIInterface server;
    ClientInterfaceRMI stub;

    NetworkMessageNameVisitor nmnv = new NetworkMessageNameVisitor();


    /**
     * Constructs an RMI client and registers it with the RMI registry and server.
     *
     * @param address     the RMI server address
     * @param port        the RMI registry port
     * @param controller  the {@link ClientController} to observe game updates
     * @throws RemoteException   if RMI setup fails
     * @throws NotBoundException if the server object is not found in registry
     */
    public ClientRMI(String address, int port, ClientController controller) throws RemoteException, NotBoundException {
        super();
        registry = LocateRegistry.getRegistry(address, port);
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
    public void notifyObservers(NetworkMessage message) throws IOException, ExecutionException, InvalidTilePosition {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }


}
