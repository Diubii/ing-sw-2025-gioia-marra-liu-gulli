package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

/**
 * This card introduces a scenario where humans, aliens, and credits are lost.
 */
public class AbandonedShip extends AdventureCard {
    /** The number of humans lost. */
    private final int crewMembersLost;
    /** The amount of credits lost or gained. */
    private final int credits;

    @JsonCreator
    public AbandonedShip(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("crewMembersLost") int crewMembersLost ,
                   @JsonProperty("credits")  int credits )
            {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.crewMembersLost = crewMembersLost;
        this.credits = credits;
    }

    /**
     * Activates the effect of the abandoned ship card.
     *
     * @param aca         The activator responsible for handling this card's effect.
     * @param flightBoard
     */
    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activateAbandonedShip(this, p, flightBoard);
    }

    public int getCrewMembersLost() {
        return crewMembersLost;
    }


    public int getCredits() {
        return credits;
    }
}
