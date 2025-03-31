package org.polimi.ingsw.galaxytrucker.observer;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;

public interface Observer {

    void update(NetworkMessage message) throws IOException;
}
