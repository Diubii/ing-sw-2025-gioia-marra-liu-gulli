package it.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.model.Projectile;
import it.polimi.ingsw.galaxytrucker.visitors.adventurecards.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

/**
 * Represents a Meteor Swarm adventure card, which launches multiple meteors at players' ships.
 */
public class MeteorSwarm extends AdventureCard {

    @Serial
    private static final long serialVersionUID = 9999329L;

    private final ArrayList<Projectile> meteors;

    public ArrayList<Projectile> getMeteors() {
        return meteors;
    }

    @JsonCreator
    public MeteorSwarm(@JsonProperty("id") int id,
                       @JsonProperty("level") int level,
                       @JsonProperty("daysLost") int daysLost,
                       @JsonProperty("name") String name,
                       @JsonProperty("learningFlight") boolean learningFlight,
                       @JsonProperty("meteors") ArrayList<Projectile> meteors,
                       @JsonProperty("affectsAll") boolean affectsAll) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = true;
        this.meteors = meteors;
    }

    public MeteorSwarm(ArrayList<Projectile> meteors) {
        this.meteors = meteors;
    }

    @Override
    public <T> T accept(AdventureCardVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
