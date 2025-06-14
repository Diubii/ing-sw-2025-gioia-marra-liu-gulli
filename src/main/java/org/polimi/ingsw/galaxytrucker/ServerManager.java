package org.polimi.ingsw.galaxytrucker;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.server.ServerRMI;
import org.polimi.ingsw.galaxytrucker.network.server.ServerRMIInterface;
import org.polimi.ingsw.galaxytrucker.network.server.ServerSocket;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerManager {
    public static void main(String[] args) {
        try {
            ServerController serverController = new ServerController();

            ServerRMIInterface serverRMI = new ServerRMI(serverController);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("GameServer", serverRMI);

            ServerSocket serverSocket = new ServerSocket(serverController);
            new Thread(serverSocket).start();

            System.out.println("[ServerManager] Server RMI e Socket avviati!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
