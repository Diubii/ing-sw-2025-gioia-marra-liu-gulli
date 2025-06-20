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


    //test
    public String testSignal;

    public ClientSocket(String address, Integer port) throws IOException {
        this.port = port;
        this.address = address;
        taskQueue = Executors.newSingleThreadExecutor();
    }

    public void sendMessage(NetworkMessage message) throws IOException {
        synchronized (outputStream) {
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.reset();  // reset serve se mandiamo oggetti modificati
        }
    }

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

    public void create(String address, int port) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(address, port));
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.readExecutionQueue = Executors.newSingleThreadExecutor();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(NetworkMessage message) throws IOException, ExecutionException {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    @Override
    public void notifyObservers(String message) throws IOException, ExecutionException {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }


}
