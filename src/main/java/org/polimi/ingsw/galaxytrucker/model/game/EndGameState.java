package org.polimi.ingsw.galaxytrucker.model.game;

import org.polimi.ingsw.galaxytrucker.enums.GameStateType;

public class EndGameState extends AbstractGameState {
    @Override
    public GameStateType getStateType() {
        return GameStateType.ENDGAME;
    }
}
