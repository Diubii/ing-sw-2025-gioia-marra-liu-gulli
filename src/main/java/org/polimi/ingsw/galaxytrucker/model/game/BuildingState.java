package org.polimi.ingsw.galaxytrucker.model.game;

import org.polimi.ingsw.galaxytrucker.enums.GameStateType;

public class BuildingState extends AbstractGameState {


    @Override
    public GameStateType getStateType() {
        return GameStateType.BUILDING;
    }
}
