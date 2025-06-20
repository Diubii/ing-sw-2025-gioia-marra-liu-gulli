package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.controller.ServerControllerHandles;
import it.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRMI extends UnicastRemoteObject implements ServerRMIInterface {
    ServerControllerHandles serverControllerHandles;
    private final Map<ClientInterfaceRMI, ClientHandler> clientMap = new ConcurrentHashMap<>();

    public ServerRMI(ServerControllerHandles serverControllerHandles) throws RemoteException {
        super();
        this.serverControllerHandles = serverControllerHandles;
    }

    public ServerControllerHandles getControllerHandles() throws RemoteException {
        return serverControllerHandles;
    }

    @Override
    public RMIClientHandler getClientHandler(ClientInterfaceRMI clientInterfaceRMI) throws RemoteException{
        return (RMIClientHandler) clientMap.get(clientInterfaceRMI);
    }

    @Override
    public void handleRMIRegistration(ClientInterfaceRMI clientStub) {

        ServerController controller = (ServerController) serverControllerHandles;

        RMIClientHandler handler = new RMIClientHandler(clientStub, controller);

        synchronized (controller.getClients()) {
            controller.addClient(handler);

        }
        clientMap.put(clientStub, handler); // Salvi il riferimento
    }


}
