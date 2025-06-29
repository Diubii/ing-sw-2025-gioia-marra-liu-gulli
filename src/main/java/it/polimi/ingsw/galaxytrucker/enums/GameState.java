package it.polimi.ingsw.galaxytrucker.enums;

/**
 * Represents the different phases of a match.
 */
public enum GameState {
    LOBBY, BUILDING_START, BUILDING_TIMER, BUILDING_END, SHIP_CHECK, CREW_INIT, FLIGHT, END;

    /**
     * Checks if the current state comes before the given state.
     *
     * @param gameState The state to compare to
     * @return true if this state is before the given one
     */
    public boolean isBefore(GameState gameState) {
        return this.ordinal() < gameState.ordinal();
    }
}
