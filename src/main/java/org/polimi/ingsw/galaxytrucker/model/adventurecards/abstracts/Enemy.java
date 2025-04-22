package org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts;
import  org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;

/**
 * Represents an enemy adventure card that extends {@link AdventureCard}.
 * An enemy card introduces a hostile entity with a specific firepower.
 */
public abstract class Enemy extends AdventureCard {
    /**
     * The firepower of the enemy.
     */
    private int firePower;

    /**
     * Gets the firepower of the enemy.
     *
     * @return The firepower value.
     */
    public int getFirePower() {
        return firePower;
    }
}
