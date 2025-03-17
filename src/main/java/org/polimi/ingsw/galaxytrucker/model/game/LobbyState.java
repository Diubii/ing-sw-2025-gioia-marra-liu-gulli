package org.polimi.ingsw.galaxytrucker.model.game;


import org.polimi.ingsw.galaxytrucker.enums.GameStateType;
import org.polimi.ingsw.galaxytrucker.exceptions.IllegalStateOperationException;

public class LobbyState extends AbstractGameState {

    @Override
    public void handlePlayerAction(Game game, String player, String action) throws IllegalStateOperationException {

    }

    @Override
    public void enter(Game game) throws IllegalStateOperationException {
        super.enter(game);
    }

    @Override
    public void exit() throws IllegalStateOperationException {
        super.exit();
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.LOBBY;
    }
}
