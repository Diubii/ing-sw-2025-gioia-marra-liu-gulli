package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

public interface ClientHandler {
    void sendMessage(NetworkMessage message);
}
