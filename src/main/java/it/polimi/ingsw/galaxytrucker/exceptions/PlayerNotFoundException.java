package it.polimi.ingsw.galaxytrucker.exceptions;

/**
 * Thrown when a player with the given nickname cannot be found in the game.
 */
public class PlayerNotFoundException extends GameException {
    public PlayerNotFoundException(String nickname) {
        super("Player " + nickname + " not found");
    }
}
