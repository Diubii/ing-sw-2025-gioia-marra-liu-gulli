package it.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.visitors.adventurecards.AdventureCardVisitorsInterface;

import java.io.Serial;

public class Epidemic extends AdventureCard {
    @Serial
    private static final long serialVersionUID = 919999L;

    public Epidemic() {
    }

    @JsonCreator
    public Epidemic(@JsonProperty("id") int id,
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
