package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerManager {
    public static void main(String[] args) {
        try {
            GameNetworkModel model = new GameNetworkModel();
            ServerController serverController = new ServerController(model);


            ServerRMI serverRMI = new ServerRMI(model, serverController);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("GameServer", serverRMI);

            ServerSocket serverSocket = new ServerSocket(model, serverController);
            new Thread(serverSocket).start();

            System.out.println("[ServerManager] Server RMI e Socket avviati!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
