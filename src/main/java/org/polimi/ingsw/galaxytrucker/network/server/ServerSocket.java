package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.view.Tui.util.Printer;
import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.ConsoleColor;
import org.polimi.ingsw.galaxytrucker.enums.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;

import java.io.*;
import java.net.Socket;

public class ServerSocket implements Runnable {
    private final GameNetworkModel model;
    private static final int PORT = 6969;
    ServerController controller;

    public ServerSocket(GameNetworkModel model, ServerController serverController) {
        this.model = model;
        this.controller = serverController;
    }

    @Override
    public void run() {
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(PORT)) {
            Printer.printlnWithLabel(PrinterLabels.ServerSocket, ConsoleColor.ServerSocket, "In ascolto sulla porta " + PORT);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                Printer.printlnWithLabel(PrinterLabels.ServerSocket, ConsoleColor.ServerSocket, "Client connected: " + socket.getInetAddress());
                new Thread(new ClientHandler(socket, model, controller)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

