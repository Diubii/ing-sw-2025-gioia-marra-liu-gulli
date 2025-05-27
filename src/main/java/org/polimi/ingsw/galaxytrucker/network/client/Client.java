package org.polimi.ingsw.galaxytrucker.network.client;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.observer.Observable;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

public interface Client {
    void sendMessage(NetworkMessage message) throws IOException, ExecutionException, InterruptedException;
}
