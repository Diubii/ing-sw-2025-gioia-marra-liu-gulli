package it.polimi.ingsw.galaxytrucker;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.network.server.ServerRMI;
import it.polimi.ingsw.galaxytrucker.network.server.ServerRMIInterface;
import it.polimi.ingsw.galaxytrucker.network.server.ServerSocket;

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
