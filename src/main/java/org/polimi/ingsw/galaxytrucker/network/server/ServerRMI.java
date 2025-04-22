package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageNameVisitor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

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
    public void receiveMessage(NetworkMessage message, ClientInterfaceRMI clientRMI) throws RemoteException, ExecutionException, InterruptedException {
        try {
            System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerRMI, TuiColor.YELLOW, "message: " + message.accept(new NetworkMessageNameVisitor())));
        } catch (TooManyPlayersException | PlayerAlreadyExistsException | InvalidTilePosition e) {
            throw new RuntimeException(e);
        }
        RMIClientHandler handler = (RMIClientHandler) clientMap.get(clientRMI);
        controller.getMessageManager().handle(message, handler);

    }


    @Override
    public void handleRMIRegistration(ClientInterfaceRMI clientStub) {

        RMIClientHandler handler = new RMIClientHandler(clientStub);

        synchronized (controller.getClients()) {
            controller.addClient(handler);

        }
        clientMap.put(clientStub, handler); // Salvi il riferimento
    }


}
