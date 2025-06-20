package it.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.galaxytrucker.visitors.adventurecards.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

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
public abstract class AdventureCard implements Serializable {

    @Serial
    private static final long serialVersionUID = 99999L;
    int id;
    int level;
    int daysLost;
    String name;
    /**
     * Indicates whether the card has to be used in learning flights
     */
    boolean learningFlight;
    /**
     * Indicates whether the effect of the card applies to all players.
     */
    boolean affectsAll; //OpenSpace, Stardust, Epidemic, MeteorSwarm
    /**
     * Indicates whether a player can choose to not activate the card. Used for {@link Planets}, {@link AbandonedShip} and {@link AbandonedStation}.
     */
    boolean facultative = false;

    /**
     * Activates the effect of the adventure card.
     *
     * @param visitor The activator responsible for triggering the card's effect.
     */
    public abstract <T> T accept(AdventureCardVisitorsInterface<T> visitor);

    /**
     * Gets the unique identifier of the card.
     *
     * @return The card ID.
     */
    public int getID() {
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
     * Checks if the card has to be used in learning flights.
     *
     * @return {@code true} if the card has to be used in learning flights, otherwise {@code false}.
     */
    public boolean isLearningFlight() {
        return learningFlight;
    }

    /**
     * Checks if the effect of the card applies to all players.
     *
     * @return {@code true} if the effect applies to all players, otherwise {@code false}.
     */
    public boolean doesAffectAll() {
        return affectsAll;
    }

    /**
     * Checks if the player can choose to not activate the card. Used for {@link Planets}, {@link AbandonedShip} and {@link AbandonedStation}.
     */
    public boolean isFacultative() {
        return facultative;
    }
}