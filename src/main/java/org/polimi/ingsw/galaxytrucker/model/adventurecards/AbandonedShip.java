package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;

/**
 * This card introduces a scenario where humans, aliens, and credits are lost.
 */
public class AbandonedShip extends AdventureCard {

    @Serial
    private static final long serialVersionUID = 999991L;
    /**
     * The number of humans lost.
     */
    private final int requiredCrewMembers;
    /**
     * The amount of credits lost or gained.
     */
    private final int credits;

    @JsonCreator
    public AbandonedShip(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("requiredCrewMembers") int requiredCrewMembers,
                         @JsonProperty("credits") int credits,
                         @JsonProperty("affectsAll") boolean affectsAll) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.requiredCrewMembers = requiredCrewMembers;
        this.credits = credits;
        this.facultative = true;
    }

    @Override
    public <T> T accept(AdventureCardVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public int getRequiredCrewMembers() {
        return requiredCrewMembers;
    }


    public int getCredits() {
        return credits;
    }
}
