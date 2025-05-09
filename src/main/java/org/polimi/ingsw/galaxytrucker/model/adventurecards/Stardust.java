package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;

public class Stardust extends AdventureCard {

    @Serial
    private static final long serialVersionUID = 76767545L;

    @JsonCreator
    public Stardust(@JsonProperty("id") int id,
                    @JsonProperty("level") int level,
                    @JsonProperty("daysLost") int daysLost,
                    @JsonProperty("name") String name,
                    @JsonProperty("learningFlight") boolean learningFlight,
                    @JsonProperty("affectsAll") boolean affectsAll) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = true;
    }

    @Override
    public <T> T accept(AdventureCardVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
