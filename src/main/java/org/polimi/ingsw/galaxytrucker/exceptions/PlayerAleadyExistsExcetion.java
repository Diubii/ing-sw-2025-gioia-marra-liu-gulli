package org.polimi.ingsw.galaxytrucker.exceptions;

public class PlayerAleadyExistsExcetion extends GameException {
    public PlayerAleadyExistsExcetion(String nickname) {
        super("Player " + nickname + " already exists");
    }
}
