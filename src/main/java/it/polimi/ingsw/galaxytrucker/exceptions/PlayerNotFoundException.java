package it.polimi.ingsw.galaxytrucker.exceptions;

public class PlayerNotFoundException extends GameException {
    public PlayerNotFoundException(String nickname) {
        super("Player " + nickname + " not found");
    }
}
