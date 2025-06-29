package it.polimi.ingsw.galaxytrucker.observer;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
/**
 * Observer interface for receiving updates from observable components.
 * Typically used in conjunction with the {@link Observable} interface.
 */
public interface Observer {

    /**
     * Called when an observable object sends a structured network message.
     *
     * @param message the {@link NetworkMessage} sent by the observable.
     */
    void update(NetworkMessage message);

}
