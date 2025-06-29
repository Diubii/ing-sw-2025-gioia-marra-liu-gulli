package it.polimi.ingsw.galaxytrucker.enums;

/**
 * Represents the various phases a player can be in during the game.
 */
public enum PLAYER_PHASE {
    START, LOBBY, BUILDING, BUILDING_TIMER, FINISH_BUILDING, CHECK_SHIP, CREW_CHOICE, FLIGHT;

    public static Boolean isBefore(PLAYER_PHASE phase, PLAYER_PHASE otherPhase){
        return phase.ordinal() < otherPhase.ordinal();
    }

}
