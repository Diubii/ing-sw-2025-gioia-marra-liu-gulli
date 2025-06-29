package it.polimi.ingsw.galaxytrucker.exceptions;

/**
 * Represents a generic exception specific to the game logic.
 */
public class GameException extends Exception {
    public GameException(String message) {
        super(message);
    }
}
