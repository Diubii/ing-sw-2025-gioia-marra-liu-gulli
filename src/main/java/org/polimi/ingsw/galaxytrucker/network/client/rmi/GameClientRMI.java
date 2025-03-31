package org.polimi.ingsw.galaxytrucker.network.client.rmi;

import org.polimi.ingsw.galaxytrucker.network.common.GameInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class GameClientRMI {
    public  GameClientRMI(int port) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", port);
            GameInterface server = (GameInterface) registry.lookup("GameServer");

            Scanner scanner = new Scanner(System.in);
            System.out.println("[Client RMI] Inserisci una mossa:");
            String move = scanner.nextLine();

            server.sendMove(move);
            System.out.println("[Client RMI] Mossa inviata! Mosse totali: " + server.getMoves());

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
