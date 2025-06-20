package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.util.UUID;

public interface ClientHandler {
    UUID getClientID();
    void sendMessage(NetworkMessage message);
}
