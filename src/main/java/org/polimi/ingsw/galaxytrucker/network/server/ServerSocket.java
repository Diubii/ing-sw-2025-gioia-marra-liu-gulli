package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;

import java.io.*;
import java.net.Socket;

public class ServerSocket implements Runnable {
    private final GameNetworkModel model;
    private static final int PORT = 5000;
    ServerController controller;

    public ServerSocket(GameNetworkModel model, ServerController serverController) {
        this.model = model;
        this.controller = serverController;
    }

    @Override
    public void run() {
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(PORT)) {
            System.out.println("[Socket Server] In ascolto sulla porta " + PORT);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                controller.addSocket(socket);
                new Thread(new ClientHandler(socket, model,controller)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

