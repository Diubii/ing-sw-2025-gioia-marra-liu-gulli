package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;

public class Slavers extends AdventureCard {


    @Serial
    private static final long serialVersionUID = 99434216L;

    private final int penalty;
    private final int firePower;



    /** The amount of credits lost or gained. */
    private final int credits;
    public int getPenalty() {
        return penalty;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonCreator
    public Slavers(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("firePower") int firePower ,
                   @JsonProperty("penalty") int penalty,
                   @JsonProperty("credits") int credits,
                   @JsonProperty("affectsAll") boolean affectsAll) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.firePower = firePower;
        this.penalty = penalty;
        this.credits = credits;
        this.affectsAll = false;

    }

    public Slavers(int penalty, int firePower,int credits) {
        this.penalty = penalty;
        this.firePower = firePower;
        this.credits = credits;
        this.affectsAll = false;
    }

    /**
     * Gets the firepower of the enemy.
     *
     * @return The firepower value.
     */
    public int getFirePower() {
        return firePower;
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public <T> T accept(AdventureCardVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}