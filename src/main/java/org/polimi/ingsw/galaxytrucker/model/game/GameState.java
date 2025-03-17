package org.polimi.ingsw.galaxytrucker.model.game;

import org.polimi.ingsw.galaxytrucker.enums.GameStateType;
import org.polimi.ingsw.galaxytrucker.exceptions.IllegalStateOperationException;

public interface GameState {
    void enter(Game game)throws IllegalStateOperationException;
    void runPhase(Game game) throws IllegalStateOperationException;
    void handlePlayerAction (Game game, String player, String action) throws IllegalStateOperationException;
    void exit() throws IllegalStateOperationException;
    GameStateType getStateType();
}
