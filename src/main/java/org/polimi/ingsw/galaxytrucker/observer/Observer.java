package org.polimi.ingsw.galaxytrucker.observer;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface Observer {

    void update(NetworkMessage message) throws IOException, ExecutionException;
}
