package it.polimi.ingsw.galaxytrucker.network.client.rmi;

import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import it.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import it.polimi.ingsw.galaxytrucker.network.client.Client;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

public interface ClientInterfaceRMI extends Remote, Client {
    void sendMessage(NetworkMessage message) throws RemoteException;

    void receiveMessage(NetworkMessage message) throws IOException, ExecutionException, TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition;
}
