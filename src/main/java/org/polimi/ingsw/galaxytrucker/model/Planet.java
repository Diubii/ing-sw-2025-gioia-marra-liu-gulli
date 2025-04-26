package org.polimi.ingsw.galaxytrucker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a planet in the game.
 * A planet has a state of occupation (whether it is occupied or not)
 * and a collection of goods that can be associated with it.
 */
public class Planet  implements Serializable {
    @Serial
    private static final long serialVersionUID = 7823187187L;

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

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @JsonCreator
    public Planet(@JsonProperty("occupied") boolean occupied, @JsonProperty("goods") ArrayList<Good> goods) {
        this.occupied = occupied;
        this.goods = goods;
    }
}
