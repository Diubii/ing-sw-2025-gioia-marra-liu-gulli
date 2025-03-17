package org.polimi.ingsw.galaxytrucker.model.game;

import org.polimi.ingsw.galaxytrucker.enums.GameStateType;
import org.polimi.ingsw.galaxytrucker.exceptions.IllegalStateOperationException;

/**
 * Abstract implementation of GameState, providing default behavior
 * for unhandled game phases by throwing IllegalStateOperationException.
 */
public abstract class AbstractGameState implements GameState {
    /**
     * Initializes state resources and enters the game state.
     *
     * This method is responsible for setting up necessary resources
     * when transitioning into this game state.
     */

    @Override
    public void enter(Game game) throws IllegalStateOperationException {
        throw new IllegalStateOperationException("Cannot enter this state.");
    }
    /**
     * Executes the necessary operations required for this game state.
     *
     * This method defines the core logic and actions that should be
     * performed while the game is in this state.
     */
    @Override
    public void runPhase(Game game) throws IllegalStateOperationException {
        throw new IllegalStateOperationException("Cannot execute phase in this state.");
    }
    /**
     * Executes the necessary operations based on player input.
     *
     * This method processes the player's actions and applies
     * the corresponding changes to the game state.
     *
     * @param player The player who triggered the action.
     * @param action The input provided by the player.
     */

    @Override
    public void handlePlayerAction(Game game, String player, String action) throws IllegalStateOperationException {
        throw new IllegalStateOperationException("Action " + action + " is not allowed in this state.");
    }
    /**
     * Cleans up resources from the current state that are no longer needed
     * for the next phase and transitions to the next game state.
     *
     * This method ensures that unnecessary resources are released or reset
     * before the game moves to the next phase.
     */

    @Override
    public void exit() throws IllegalStateOperationException {
        throw new IllegalStateOperationException("Cannot exit from this state.");
    }

    @Override
    public GameStateType getStateType() {
        return null;
    }
}
