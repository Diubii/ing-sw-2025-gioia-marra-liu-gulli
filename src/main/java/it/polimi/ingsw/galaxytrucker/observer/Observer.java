package it.polimi.ingsw.galaxytrucker.observer;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

public interface Observer {
    void update(NetworkMessage message);
    void update(String genericMessage);

}
