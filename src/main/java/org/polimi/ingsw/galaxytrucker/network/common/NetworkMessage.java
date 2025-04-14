package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.Serializable;

public abstract class NetworkMessage implements Serializable {
    public abstract void accept(ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException;
}
