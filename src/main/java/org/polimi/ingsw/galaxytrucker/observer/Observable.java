package org.polimi.ingsw.galaxytrucker.observer;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public interface Observable {


    public void addObserver(Observer observer);

    public void removeObserver(Observer observer);

    public void notifyObservers(NetworkMessage message) throws IOException, ExecutionException ;

    public void notifyObservers(String message) throws IOException, ExecutionException;


}
