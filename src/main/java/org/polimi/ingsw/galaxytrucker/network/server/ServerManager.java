package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ServerManager {
    public static void main(String[] args) {
        try {

            ArrayList<LobbyManager> GameModels = new ArrayList<>();

            ServerController serverController = new ServerController(GameModels);


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
