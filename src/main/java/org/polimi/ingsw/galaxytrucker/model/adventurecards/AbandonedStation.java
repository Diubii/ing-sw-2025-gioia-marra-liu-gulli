package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.adventurecards.AdventureCardVisitorsInterface;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.io.Serial;
import java.util.ArrayList;

/**
 * Represents an Abandoned Station adventure card.
 * The card allows the player to gain or lose goods based on the card's effect.
 */
public class AbandonedStation extends AdventureCard {
    @Serial
    private static final long serialVersionUID = 99199L;

    /**
     * A list of goods associated with the abandoned station.
     */
    private final ArrayList<Good> goods;


    private final int requiredCrewMembers;




    @JsonCreator
    public AbandonedStation(@JsonProperty("id") int id,
                            @JsonProperty("level") int level,
                            @JsonProperty("daysLost") int daysLost,
                            @JsonProperty("name") String name,
                            @JsonProperty("learningFlight") boolean learningFlight,
                            @JsonProperty("goods") ArrayList<Good> goods,
                            @JsonProperty("requiredCrewMembers") int requiredCrewMembers,
                            @JsonProperty("affectsAll") boolean affectsAll) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.goods = goods;
        this.requiredCrewMembers = requiredCrewMembers;
    }

    /**
     * Gets the list of goods found at the abandoned station.
     *
     * @return A list of {@link Good} objects associated with the abandoned station.
     */
    public ArrayList<Good> getGoods() {
        return goods;
    }

    public int getRequiredCrewMembers() {
        return requiredCrewMembers;
    }

    /**
     * Activates the effect of the Abandoned Station card by triggering the effect
     * through the given {@link AdventureCardVisitorsInterface}.
     *
     * @param visitor The activator responsible for triggering the Abandoned Station's effect.
     */
    @Override
    public <T> T accept(AdventureCardVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
