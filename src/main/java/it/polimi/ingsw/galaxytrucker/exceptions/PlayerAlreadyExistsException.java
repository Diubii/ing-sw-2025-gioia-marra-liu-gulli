package it.polimi.ingsw.galaxytrucker.exceptions;

/**
 * Thrown when attempting to add a player with a nickname that already exists in the game.
 */
public class PlayerAlreadyExistsException extends GameException {
    public PlayerAlreadyExistsException(String nickname) {
        super("Player " + nickname + " already exists");
    }
}
