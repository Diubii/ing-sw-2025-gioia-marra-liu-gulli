package it.polimi.ingsw.galaxytrucker.enums;

public enum GameState {
    LOBBY, BUILDING_START, BUILDING_TIMER, BUILDING_END, SHIP_CHECK, CREW_INIT, FLIGHT, END;

    public boolean isBefore(GameState gameState) {
        return this.ordinal() < gameState.ordinal();
    }
}
