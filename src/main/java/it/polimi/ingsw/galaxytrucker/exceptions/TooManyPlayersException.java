package it.polimi.ingsw.galaxytrucker.exceptions;

/**
 * Thrown when attempting to add more players than the allowed maximum.
 */
public class TooManyPlayersException extends GameException {
    public TooManyPlayersException(int maxPlayers) {
        super("Too many players: " + maxPlayers);
    }
}
