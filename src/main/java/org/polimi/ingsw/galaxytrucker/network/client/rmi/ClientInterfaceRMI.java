package org.polimi.ingsw.galaxytrucker.network.client.rmi;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

public interface ClientInterfaceRMI extends Remote, Client {
    public void sendMessage(NetworkMessage message);

    public void receiveMessage(NetworkMessage message) throws IOException, ExecutionException, TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition;
}
