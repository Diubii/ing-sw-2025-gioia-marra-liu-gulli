package org.polimi.ingsw.galaxytrucker.network.client;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.observer.Observable;

import java.io.IOException;
import java.net.Socket;

public  interface Client  {

    void sendMessage(NetworkMessage message) throws IOException;

    void receiveMessage();

    void create(String address, int port) throws IOException;

    public Socket getSocket();
}
