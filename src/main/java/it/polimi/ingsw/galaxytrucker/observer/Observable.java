package it.polimi.ingsw.galaxytrucker.observer;

import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import it.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface Observable {


    public void addObserver(Observer observer);

    public void removeObserver(Observer observer);

    public void notifyObservers(NetworkMessage message) throws IOException, ExecutionException, TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition;

    public void notifyObservers(String message) throws IOException, ExecutionException;


}
