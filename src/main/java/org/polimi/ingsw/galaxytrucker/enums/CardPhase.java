package org.polimi.ingsw.galaxytrucker.enums;

/**
 * This enum is needed to understand which phase of the card needs to be executed.
 */
public enum CardPhase {
    /**
     * Setup to get what the card needs.
     */
    Start,
    /**
     * Used if a player has decided to activate the card.
     */
    CardActivated,
    /**
     * Used once one or more components have been activated.
     */
    ComponentActivated,
    /**
     * Used once the number of crew members dictated by the card have been discarded.
     */
    CrewDiscarded,
    /**
     * Used once the goods have been loaded onto the ship.
     */
    GoodsLoaded,
    /**
     * Used once a ShipUpdate has been received.
     */
    ShipReceived,
    /**
     * Used if some checks are needed.
     */
    End
}
