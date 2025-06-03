package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.controller.ServerControllerHandles;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

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
