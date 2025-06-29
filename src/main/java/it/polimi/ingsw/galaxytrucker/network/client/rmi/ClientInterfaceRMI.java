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

/**
 * Remote interface for RMI-based client communication in the Galaxy Trucker game.
 * Extends the {@link Client} interface and {@link java.rmi.Remote} for remote method invocation.
 */
public interface ClientInterfaceRMI extends Remote, Client {
    /**
     * Sends a network message to the server via RMI.
     *
     * @param message the {@link NetworkMessage} to send
     * @throws RemoteException if an RMI communication error occurs
     */
    void sendMessage(NetworkMessage message) throws RemoteException;

    /**
     * Receives a message from the server. May trigger actions or exceptions depending on game state.
     *
     * @param message the incoming {@link NetworkMessage}
     * @throws IOException if a local I/O error occurs
     * @throws ExecutionException if an async task fails
     * @throws TooManyPlayersException if the game has reached the maximum number of players
     * @throws PlayerAlreadyExistsException if a player with the same nickname already exists
     * @throws InvalidTilePosition if a tile is placed at an invalid position
     */
    void receiveMessage(NetworkMessage message) throws IOException, ExecutionException, TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition;
}
