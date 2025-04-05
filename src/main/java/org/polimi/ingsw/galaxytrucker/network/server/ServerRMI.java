package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.LOBBY_INFO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRMI extends UnicastRemoteObject implements ServerRMIInterface {
    private final GameNetworkModel model;
    ServerController controller;
    private final Map<ClientInterfaceRMI, ClientHandler> clientMap = new ConcurrentHashMap<>();

    public ServerRMI(GameNetworkModel model, ServerController serverController) throws RemoteException {
        super();
        this.model = model;
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
            Boolean flag = false;

            LOBBY_INFO message = new LOBBY_INFO();
            message.setIsFirst(false);

            System.out.println("PLAYERS NUM " + controller.getClients().size());
            if (controller.getClients().getFirst().equals(handler)) {

                message.setIsFirst(true);
//                    output.writeObject(model);
                System.out.println("Client  connected. is first");

            }

            handler.sendMessage(message);
        }
        clientMap.put(clientStub, handler); // Salvi il riferimento
    }



}
