package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
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
    ServerController controller;
    private final Map<ClientInterfaceRMI, ClientHandler> clientMap = new ConcurrentHashMap<>();
    NetworkMessageNameVisitor nmnv = new NetworkMessageNameVisitor();

    public ServerRMI(ServerController serverController) throws RemoteException {
        super();
        this.controller = serverController;
    }

    public ServerController getController() throws RemoteException {
        return controller;
    }


    @Override
    public void receiveMessage(NetworkMessage message, ClientInterfaceRMI clientRMI) throws RemoteException, ExecutionException, InterruptedException {
        NetworkMessageType type = message.accept(nmnv);

//            if (message.accept(new NetworkMessageNameVisitor()).equals(NetworkMessageType.FinishBuildingRequest)){
//                FinishBuildingRequest mess = (FinishBuildingRequest) message;
//                System.out.println("FINISH FROM + " + mess.name);
//            }

        if (type != NetworkMessageType.HeartbeatRequest) {
            System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerRMI, TuiColor.YELLOW, "message: " + type));
        }
        RMIClientHandler handler = (RMIClientHandler) clientMap.get(clientRMI);


//        String nickname = controller.getLobbyFromHandler(handler).getPlayerHandlers().entrySet().stream().filter(pair -> pair.getValue().equals(handler)).map(Map.Entry::getKey).findFirst().get();
//
//        if (controller.getLobbyFromHandler(handler).getPlayerHandlers().entrySet().stream().filter(pair -> pair.getValue().equals(handler)).map(Map.Entry::getKey).findFirst().isPresent())
//        System.out.println("FROM: " + nickname);


        new Thread(() -> {
            controller.getMessageManager().handle(message, handler);
        }).start();

    }


    @Override
    public void handleRMIRegistration(ClientInterfaceRMI clientStub) {

        RMIClientHandler handler = new RMIClientHandler(clientStub, controller);

        synchronized (controller.getClients()) {
            controller.addClient(handler);

        }
        clientMap.put(clientStub, handler); // Salvi il riferimento
    }


}
