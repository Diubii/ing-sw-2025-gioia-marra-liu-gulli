package it.polimi.ingsw.galaxytrucker;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkingUtils;
import it.polimi.ingsw.galaxytrucker.network.server.ServerRMI;
import it.polimi.ingsw.galaxytrucker.network.server.ServerRMIInterface;
import it.polimi.ingsw.galaxytrucker.network.server.ServerSocket;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

public class ServerManager {
    public static void main(String[] args) {
        try {
            String address = NetworkingUtils.getLocalIP();

            if(!address.equals(NetworkingUtils.LOOPBACK_ADDRESS)) System.out.println("[ServerManager] Server IP: " + address);
            else System.out.println("[WARNING] This server will only work on localhost.");

            System.setProperty("java.rmi.server.hostname", address);

            ServerController serverController = new ServerController();

            try {
                new ServerRMI(serverController);
            }
            catch (RemoteException e) {
                System.err.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerRMI, TuiColor.BLUE, "Non è stato possibile avviare ServerRMI: " + e.getMessage()));
            }

            ServerSocket serverSocket = new ServerSocket(serverController);
            new Thread(serverSocket).start();

            //System.out.println("[ServerManager] Server RMI e Socket avviati!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
