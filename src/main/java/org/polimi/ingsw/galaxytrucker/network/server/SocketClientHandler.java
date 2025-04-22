package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageNameVisitor;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClientHandler implements Runnable, ClientHandler {
    private final Object inputLock;
    private final Object outputLock;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final Socket clientSocket;
    private final ServerController serverController;

    private String testSignal;

    private final ExecutorService executor = Executors.newCachedThreadPool();


    public SocketClientHandler(Socket socket, ServerController controller) {
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
            System.out.println("Client " + clientSocket.getInetAddress() + " connected to " + clientSocket.getRemoteSocketAddress());
            ConnectionManager();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Client " + clientSocket.getInetAddress() + " connection dropped.");
        }
    }

    private void ConnectionManager() throws IOException, ExecutionException, InterruptedException {
        NetworkMessageNameVisitor nmnv = new NetworkMessageNameVisitor();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (inputLock) {

                    NetworkMessage message = (NetworkMessage) input.readObject();
                    //logica per gestire i messaggi

                    if (message.accept(nmnv).equals(NetworkMessageType.NicknameRequest)) {
                        NicknameRequest request = (NicknameRequest) message;
                        System.out.println("Nickname received: " + request.getNickname());
                    }

                    System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerSocket, TuiColor.GREEN,"MESSAGE " + message.accept(nmnv) + " RECEIVED FROM " + clientSocket.getInetAddress().toString()));
                    executor.submit(() -> {
                        try {
                            serverController.getMessageManager().handle(message, this);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            executor.shutdownNow();
            System.out.println("Invalid stream from client");
        } catch (PlayerAlreadyExistsException | TooManyPlayersException | InvalidTilePosition e) {
            throw new RuntimeException(e);
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

    //@Test


}
