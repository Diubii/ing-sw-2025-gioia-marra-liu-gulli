package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.*;
import java.net.Socket;

class ClientHandler implements Runnable {
    private final GameNetworkModel model;
    private final Object inputLock;
    private final Object outputLock;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket clientSocket;
    private ServerController serverController;

    public ClientHandler(Socket socket, GameNetworkModel model, ServerController controller) {
        this.clientSocket = socket;
        this.model = model;
        inputLock = new Object();
        outputLock = new Object();
        serverController = controller;
        try {
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error in ClientHandler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            ConnectionManager();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client " + clientSocket.getInetAddress() + " connection dropped.");
        }
    }

    private void ConnectionManager() throws IOException, ClassNotFoundException {
        while(!Thread.currentThread().isInterrupted()) {
            NetworkMessage nm = (NetworkMessage) input.readObject();
            System.out.println(nm.toString());
        }
    }
}