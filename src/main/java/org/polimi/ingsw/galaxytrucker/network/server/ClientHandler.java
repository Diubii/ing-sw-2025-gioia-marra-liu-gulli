package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.LOBBY_INFO;

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
            synchronized (serverController.getClientSockets()){
                Boolean flag = false;

                LOBBY_INFO message = new LOBBY_INFO();
                message.setIsFirst(false);

                System.out.println("PLAYERS NUM " + serverController.getClientSockets().size());
                if (serverController.getClientSockets().getFirst().equals(clientSocket)) {

                    message.setIsFirst(true);
//                    output.writeObject(model);
                    System.out.println("Client " + clientSocket.getInetAddress() + " connected. is first");

                }
               output.writeObject(message);

            }

            while (!Thread.currentThread().isInterrupted()) {
                synchronized (inputLock) {
                    NetworkMessage message = (NetworkMessage) input.readObject();
                    //logica per gestire i messaggi
                    System.out.println("MESSAGE RECEIVED FROM " + clientSocket.getInetAddress().toString());
                    serverController.getMessageManager().handle(message);

                }
            }
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Invalid stream from client");
        } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
        clientSocket.close();
    }
}
