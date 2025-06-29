package it.polimi.ingsw.galaxytrucker.observer;

import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import it.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Interface for observable objects in the Observer design pattern.
 * Allows observers to subscribe and receive updates when a {@link NetworkMessage} is triggered.
 */
public interface Observable {


    /**
     * Registers a new observer.
     *
     * @param observer the observer to add.
     */
     void addObserver(Observer observer);
    /**
     * Notifies all registered observers with a network message.
     *
     * @param message the message to send to observers.
     * @throws IOException                   if an I/O error occurs.
     * @throws ExecutionException            if observer execution fails.
     * @throws TooManyPlayersException       if adding a player exceeds limits.
     * @throws PlayerAlreadyExistsException  if a player with the same name already exists.
     * @throws InvalidTilePosition           if a tile is placed in an invalid position.
     */
     void notifyObservers(NetworkMessage message) throws IOException, ExecutionException, TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition;


}
