package org.polimi.ingsw.galaxytrucker.exceptions;

public class PlayerAlreadyExistsException extends GameException {
    public PlayerAlreadyExistsException(String nickname) {
        super("Player " + nickname + " already exists");
    }
}
