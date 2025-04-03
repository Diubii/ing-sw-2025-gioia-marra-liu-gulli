package org.polimi.ingsw.galaxytrucker.network.client.socket;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.observer.Observer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSocket extends Observable implements Client {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ExecutorService readExecutionQueue;
    String address;
    int port;
    private final ExecutorService taskQueue;

    public ClientSocket(String address, Integer port) throws IOException {
    this.port = port;
    this.address = address;
    taskQueue = Executors.newSingleThreadExecutor();
}

@Override
 public void sendMessage(NetworkMessage message) throws IOException {
     outputStream.writeObject(message);
     outputStream.reset();
 }

    @Override
    public void receiveMessage() {
        readExecutionQueue.execute(() -> {

            while (!readExecutionQueue.isShutdown()) {
                NetworkMessage message = null;
                try {
                    message = (NetworkMessage) inputStream.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    readExecutionQueue.shutdownNow();
                }
                try {
                    notifyObservers(message);
                } catch (IOException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void create(String address, int port) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(address, port));
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.readExecutionQueue = Executors.newSingleThreadExecutor();
    }

    public Socket getSocket(){
        return socket;
    }


}
