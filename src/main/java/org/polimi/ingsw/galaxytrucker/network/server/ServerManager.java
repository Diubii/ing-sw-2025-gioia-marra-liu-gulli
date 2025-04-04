package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.NetworkMessageMethods;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkingConstants;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerManager {
    public static void main(String[] args) {
        try {
            System.out.println("Launching server...");
            GameNetworkModel model = new GameNetworkModel();
            ServerController serverController = new ServerController(model);

            //ServerRMI serverRMI = new ServerRMI(defaultRMIport);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(NetworkingConstants.RMI_DEFAULT_PORT));
            registry.rebind("server", new NetworkMessageMethods());
            System.out.println("RMI server started.");

            ServerSocket serverSocket = new ServerSocket(model, serverController);
            new Thread(serverSocket).start();
            System.out.println("Launch complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}