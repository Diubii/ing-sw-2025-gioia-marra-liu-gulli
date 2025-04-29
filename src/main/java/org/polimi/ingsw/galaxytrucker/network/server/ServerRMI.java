package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.FinishBuildingRequest;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;

import java.io.IOException;
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
    public void receiveMessage(NetworkMessage message, ClientInterfaceRMI clientRMI) throws IOException, ExecutionException, InterruptedException {
        try {

            if (message.accept(new NetworkMessageNameVisitor()).equals(NetworkMessageType.FinishBuildingRequest)){
                FinishBuildingRequest mess = (FinishBuildingRequest) message;
                System.out.println("FINISH FROM + " + mess.name);
            }

            System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerRMI, TuiColor.YELLOW, "message: " + message.accept(new NetworkMessageNameVisitor())));
        } catch (TooManyPlayersException | PlayerAlreadyExistsException | InvalidTilePosition e) {
            throw new RuntimeException(e);
        }
        RMIClientHandler handler = (RMIClientHandler) clientMap.get(clientRMI);



//        String nickname = controller.getLobbyFromHandler(handler).getPlayerHandlers().entrySet().stream().filter(pair -> pair.getValue().equals(handler)).map(Map.Entry::getKey).findFirst().get();
//
//        if (controller.getLobbyFromHandler(handler).getPlayerHandlers().entrySet().stream().filter(pair -> pair.getValue().equals(handler)).map(Map.Entry::getKey).findFirst().isPresent())
//        System.out.println("FROM: " + nickname);



        new Thread(()->{
            try {
                controller.getMessageManager().handle(message, handler);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

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
