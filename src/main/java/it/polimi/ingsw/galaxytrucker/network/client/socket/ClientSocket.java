package it.polimi.ingsw.galaxytrucker.network.client.socket;

import it.polimi.ingsw.galaxytrucker.network.client.Client;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.observer.Observable;
import it.polimi.ingsw.galaxytrucker.observer.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket-based client implementation for the Galaxy Trucker game.
 * Responsible for handling socket communication with the server using input/output streams.
 * Implements {@link Observable} to notify the client controller of incoming {@link NetworkMessage}s.
 */
public class ClientSocket implements Client, Observable {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ExecutorService readExecutionQueue;
    String address;
    int port;
    private final ExecutorService taskQueue;
    private final ArrayList<Observer> observers = new ArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();


    /**
     * Constructs a socket client with specified server address and port.
     *
     * @param address the server's IP or hostname
     * @param port    the server's port number
     * @throws IOException if the connection setup fails
     */
    public ClientSocket(String address, Integer port) throws IOException {
        this.port = port;
        this.address = address;
        taskQueue = Executors.newSingleThreadExecutor();
    }

    /**
     * Sends a {@link NetworkMessage} to the server through the output stream.
     * This method is synchronized to ensure thread safety on the stream.
     *
     * @param message the message to send
     * @throws IOException if sending fails
     */

    public void sendMessage(NetworkMessage message) throws IOException {
        synchronized (outputStream) {
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.reset();  // reset serve se mandiamo oggetti modificati
        }
    }

    /**
     * Starts a dedicated thread to continuously listen for messages from the server.
     * When a message is received, it is forwarded to all registered observers asynchronously.
     */
    public void receiveMessage() {
        readExecutionQueue.execute(() -> {

            while (!readExecutionQueue.isShutdown()) {
                NetworkMessage message = null;
                try {
                    message = (NetworkMessage) inputStream.readObject();

                } catch (IOException | ClassNotFoundException e) {
                    readExecutionQueue.shutdownNow();
                }
                NetworkMessage finalMessage = message;
                executor.submit(() -> {
                    try {
                        notifyObservers(finalMessage);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    /**
     * Initializes the socket connection and I/O streams to the specified address and port.
     *
     * @param address the server's IP or hostname
     * @param port    the server's port
     * @throws IOException if socket creation or connection fails
     */
    public void create(String address, int port) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(address, port));
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.readExecutionQueue = Executors.newSingleThreadExecutor();
    }

    /**
     * Returns the underlying socket used by this client.
     *
     * @return the connected {@link Socket} instance
     */
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }



    @Override
    public void notifyObservers(NetworkMessage message) throws IOException, ExecutionException {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }




}
