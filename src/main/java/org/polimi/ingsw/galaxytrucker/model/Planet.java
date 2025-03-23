package org.polimi.ingsw.galaxytrucker.model;

import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.util.ArrayList;

/**
 * Represents a planet in the game.
 * A planet has a state of occupation (whether it is occupied or not)
 * and a collection of goods that can be associated with it.
 */
public class Planet {
    /** Indicates whether the planet is occupied. */
    private boolean occupied;
    private final ArrayList<Good> goods;

    /**
     * Checks whether the planet is occupied.
     *
     * @return {@code true} if the planet is occupied, otherwise {@code false}.
     */
    public boolean isOccupied() {
        return occupied;
    }
    public ArrayList<Good> getGoods() {
        return goods;
    }

    public Planet(boolean occupied, ArrayList<Good> goods) {
        this.occupied = occupied;
        this.goods = goods;
    }
}
