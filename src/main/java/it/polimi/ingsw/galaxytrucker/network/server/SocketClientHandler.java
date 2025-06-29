package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles the communication between the server and a single client using sockets.
 * Reads incoming messages, processes them using a visitor, and sends responses.
 * Each handler runs in its own thread.
 */
public class SocketClientHandler implements Runnable, ClientHandler {
    private final Object inputLock;


    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final Socket clientSocket;
    private final ServerController serverController;
    NetworkMessageNameVisitor nmnv = new NetworkMessageNameVisitor();
    private final UUID clientID;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Constructs a handler for a given client socket.
     *
     * @param socket     the client's socket.
     * @param controller the server controller managing game logic.
     */
    public SocketClientHandler(Socket socket, ServerController controller) {
        this.clientSocket = socket;
        inputLock = new Object();
        serverController = controller;
        clientID = UUID.randomUUID();

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
            //e.printStackTrace();
            System.out.println("Client " + clientSocket.getInetAddress() + " connection dropped.");
        }
    }

    /**
     * Continuously reads messages from the client and delegates handling.
     */
    private void ConnectionManager() throws IOException, ExecutionException, InterruptedException {
        serverController.startNewHeartbeat(this);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (inputLock) {

                    NetworkMessage message = (NetworkMessage) input.readObject();
                    NetworkMessageType type = message.accept(nmnv);
                    if (type.equals(NetworkMessageType.NicknameRequest)) {
                        NicknameRequest request = (NicknameRequest) message;
                        System.out.println("Nickname received: " + request.getNickname());
                    }
                    if (type != NetworkMessageType.HeartbeatRequest) {
                        System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerSocket, TuiColor.GREEN, "MESSAGE " + type + " RECEIVED FROM " + clientSocket.getInetAddress().toString()));
                    }

                    executor.submit(() -> {
                        try {
                            serverController.getMessageManager().handle(message, this);  //Semplicemente visitor
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            executor.shutdownNow();
        }
        clientSocket.close();
    }


    @Override
    public UUID getClientID() {
        return clientID;
    }

    @Override
    public synchronized void sendMessage(NetworkMessage message) {
        try {
            output.reset();  // Forza la riscrittura dell'intero oggetto
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.ServerSocket, TuiColor.GREEN, "Tried to send a message to a closed socket: " + clientSocket.getInetAddress().toString()));
        }
    }

    @Override
    public String toString() {
        return clientSocket.getInetAddress().toString();
    }
}
