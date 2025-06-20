package it.polimi.ingsw.galaxytrucker.exceptions;

public class TooManyPlayersException extends GameException {
    public TooManyPlayersException(int maxPlayers) {
        super("Too many players: " + maxPlayers);
    }
}
