package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;

import java.util.ArrayList;

/**
 * Represents an abstract adventure card with common properties and behavior.
 * Adventure cards may have various effects that influence gameplay.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AbandonedShip.class, name = "AbandonedShip"),
        @JsonSubTypes.Type(value = AbandonedStation.class, name = "AbandonedStation"),
        @JsonSubTypes.Type(value = CombatZone.class, name = "CombatZone"),
        @JsonSubTypes.Type(value = Epidemic.class, name = "Epidemic"),
        @JsonSubTypes.Type(value = MeteorSwarm.class, name = "MeteorSwarm"),
        @JsonSubTypes.Type(value = OpenSpace.class, name = "OpenSpace"),
        @JsonSubTypes.Type(value = Pirates.class, name = "Pirates"),
        @JsonSubTypes.Type(value = Planets.class, name = "Planets"),
        @JsonSubTypes.Type(value = Slavers.class, name = "Slavers"),
        @JsonSubTypes.Type(value = Smugglers.class, name = "Smugglers"),
        @JsonSubTypes.Type(value = Stardust.class, name = "Stardust"),


})
public abstract class AdventureCard {
    protected int id;
    protected int level;
    protected int daysLost;
    protected String name;
    /** Indicates whether the card grants the ability to learn flight. */
    protected boolean learningFlight;
    /** Indicates whether the effect of the card applies to all players. */
    protected boolean affectsAll;

    /**
     * Activates the effect of the adventure card.
     *
     * @param aca         The activator responsible for triggering the card's effect.
     * @param flightBoard
     */
    public abstract void activateEffect(AdventureCardActivator aca, ArrayList<Player> player, FlightBoard flightBoard);

    public abstract String[] accept(AdventureCardPrintVisitorInterface visitor);

    /**
     * Gets the unique identifier of the card.
     *
     * @return The card ID.
     */
    public int getId() {
        return id;
    }
    /**
     * Gets the level of the card.
     *
     * @return The card level.
     */
    public int getLevel() {
        return level;
    }
    /**
     * Gets the number of days lost due to the card's effect.
     *
     * @return The number of days lost.
     */
    public int getDaysLost() {
        return daysLost;
    }
    /**
     * Gets the name of the card.
     *
     * @return The card name.
     */
    public String getName() {
        return name;
    }
    /**
     * Checks if the card grants the ability to learn flight.
     *
     * @return {@code true} if the card provides the ability to learn flight, otherwise {@code false}.
     */
    public boolean isLearningFlight() {
        return learningFlight;
    }
    /**
     * Checks if the effect of the card applies to all players.
     *
     * @return {@code true} if the effect applies to all players, otherwise {@code false}.
     */
    public boolean isAffectsAll() {
        return affectsAll;
    }


}
