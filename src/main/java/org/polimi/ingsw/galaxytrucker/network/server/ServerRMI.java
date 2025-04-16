package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRMI extends UnicastRemoteObject implements ServerRMIInterface {
    ServerController controller;
    private final Map<ClientInterfaceRMI, ClientHandler> clientMap = new ConcurrentHashMap<>();

    public ServerRMI(ServerController serverController) throws RemoteException {
        super();
        this.controller = serverController;

    }

    public ServerController getController() throws RemoteException {
        return controller;
    }


    @Override
    public void receiveMessage(NetworkMessage message, ClientInterfaceRMI clientRMI) throws RemoteException {
        System.out.println("RECEIVED MESSAGE\n");
        RMIClientHandler handler = (RMIClientHandler) clientMap.get(clientRMI);
        controller.getMessageManager().handle(message, handler);

    }


    @Override
    public void handleRMIRegistration(ClientInterfaceRMI clientStub) {

        RMIClientHandler handler = new RMIClientHandler(clientStub);

        synchronized (controller.getClients()){
            controller.addClient(handler);
            System.out.println(TuiColor.GREEN + "CURRENT SERVER-WIDE PLAYERS NUM " + controller.getClients().size() + TuiColor.RESET);

        }
        clientMap.put(clientStub, handler); // Salvi il riferimento
    }



}
