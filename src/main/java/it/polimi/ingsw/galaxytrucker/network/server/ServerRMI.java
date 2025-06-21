package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.controller.ServerControllerHandles;
import it.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkingUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.net.BindException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRMI extends UnicastRemoteObject implements ServerRMIInterface {
    ServerControllerHandles serverControllerHandles;
    private final Map<ClientInterfaceRMI, ClientHandler> clientMap = new ConcurrentHashMap<>();

    public ServerRMI(ServerControllerHandles serverControllerHandles) throws RemoteException {
        super();
        Registry registry = LocateRegistry.createRegistry(NetworkingUtils.RMI_DEFAULT_PORT);
        registry.rebind("GameServer", this);
        System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerRMI, TuiColor.BLUE, "Avviato sulla porta " + NetworkingUtils.RMI_DEFAULT_PORT));
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
    public void handleRMIRegistration(ClientInterfaceRMI clientStub) throws RemoteException {
        ServerController controller = (ServerController) serverControllerHandles;
        RMIClientHandler handler = new RMIClientHandler(clientStub, controller);
        synchronized (controller.getClients()) {
            controller.addClient(handler);
        }
        clientMap.put(clientStub, handler); // Salvi il riferimento
    }
}
