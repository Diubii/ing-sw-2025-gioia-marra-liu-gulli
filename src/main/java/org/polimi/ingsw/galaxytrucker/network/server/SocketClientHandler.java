package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.*;
import java.net.Socket;

public class SocketClientHandler implements Runnable, ClientHandler {
    private final Object inputLock;
    private final Object outputLock;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket clientSocket;
    private ServerController serverController;


    public SocketClientHandler(Socket socket,  ServerController controller) {
        this.clientSocket = socket;
        inputLock = new Object();
        outputLock = new Object();
        serverController = controller;
        try {
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error in ClientHandler");
        }
    }

    @Override
    public void run() {
        try {

            ConnectionManager();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Client " + clientSocket.getInetAddress() + " connection dropped.");
        }
    }

    private void ConnectionManager() throws IOException {
        try {

            while (!Thread.currentThread().isInterrupted()) {
                synchronized (inputLock) {
                    NetworkMessage message = (NetworkMessage) input.readObject();
                    //logica per gestire i messaggi
                    System.out.println("MESSAGE RECEIVED FROM " + clientSocket.getInetAddress().toString());
                    serverController.getMessageManager().handle(message, this);

                }
            }
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Invalid stream from client");
        }
        clientSocket.close();
    }


    public synchronized void sendMessage(NetworkMessage message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
