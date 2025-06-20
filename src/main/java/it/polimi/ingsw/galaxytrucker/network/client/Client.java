package it.polimi.ingsw.galaxytrucker.network.client;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;

public interface Client {
    void sendMessage(NetworkMessage message) throws IOException;
}
