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
    private final int humansLost;
    /** The number of aliens lost. */
    private final int aliensLost;
    /** The amount of credits lost or gained. */
    private final int credits;

    @JsonCreator
    public AbandonedShip(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("humansLost") int humansLost ,
                   @JsonProperty("aliensLost")  int aliensLost,
                   @JsonProperty("credits")  int credits )
            {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.humansLost = humansLost;
        this.aliensLost = aliensLost;
        this.credits = credits;
    }
    /**
     * Constructs an AbandonedShip card with specified losses.
     *
     * @param humansLost The number of humans lost.
     * @param aliensLost The number of aliens lost.
     * @param credits The amount of credits lost or gained.
     */
    public AbandonedShip(int humansLost, int aliensLost, int credits) {
        this.humansLost = humansLost;
        this.aliensLost = aliensLost;
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
    /**
     * Gets the number of humans lost.
     *
     * @return The number of humans lost.
     */
    public int getHumansLost() {
        return humansLost;
    }

    /**
     * Gets the number of aliens lost.
     *
     * @return The number of aliens lost.
     */
    public int getAliensLost() {
        return aliensLost;
    }
    /**
     * Gets the amount of credits associated with the abandoned ship.
     *
     * @return The credit amount.
     */
    public int getCredits() {
        return credits;
    }
}
