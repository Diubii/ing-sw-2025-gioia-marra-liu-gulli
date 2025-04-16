package org.polimi.ingsw.galaxytrucker.network.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {

    // Il client invia una mossa al server
    void sendMove(String move) throws RemoteException;

    // Il client può chiedere la lista delle mosse effettuate
    String getMoves() throws RemoteException;
}
