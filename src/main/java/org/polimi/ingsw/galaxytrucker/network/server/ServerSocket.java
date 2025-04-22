package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;

import java.io.*;
import java.net.Socket;

public class ServerSocket implements Runnable {
    private static final int PORT = 5000;
    ServerController controller;

    public ServerSocket(ServerController serverController) {
        this.controller = serverController;
    }

    @Override
    public void run() {
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(PORT)) {
            System.out.println("[Socket Server] In ascolto sulla porta " + PORT);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                SocketClientHandler socketClientHandler = new SocketClientHandler(socket, controller);
                System.out.println(controller.getClients().size());
                controller.addClient(socketClientHandler);
                System.out.println(controller.getClients().size());


                new Thread(socketClientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

