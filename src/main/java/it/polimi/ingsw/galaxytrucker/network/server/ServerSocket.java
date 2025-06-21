package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkingUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.io.IOException;
import java.net.Socket;

public class ServerSocket implements Runnable {
    ServerController controller;

    public ServerSocket(ServerController serverController) {
        this.controller = serverController;
    }

    @Override
    public void run() {
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(NetworkingUtils.SOCKET_DEFAULT_PORT)) {
            System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerSocket, TuiColor.GREEN, "In ascolto sulla porta " + NetworkingUtils.SOCKET_DEFAULT_PORT));
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                SocketClientHandler socketClientHandler = new SocketClientHandler(socket, controller);
                //System.out.println(controller.getClients().size());
                controller.addClient(socketClientHandler);
                //controller.addClient();
                //System.out.println(controller.getClients().size());


                new Thread(socketClientHandler).start();
            }
        } catch (IOException e) {
            System.err.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerSocket, TuiColor.GREEN, "Non è stato possibile avviare ServerSocket: " + e.getMessage()));
        }
    }

}

