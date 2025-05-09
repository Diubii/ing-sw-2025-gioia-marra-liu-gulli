package org.polimi.ingsw.galaxytrucker.model.essentials;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.enums.Color;

import java.io.Serial;
import java.io.Serializable;


/**
 * Represents a good in the game, characterized by its color.
 * Each good has a color associated with it, and its value is determined
 * by the color of the good.
 */

public class Good implements Serializable {

    @Serial
    private static final long serialVersionUID = 14242424244L;

    private Color color;

    /**
     * Constructs a Good with the specified color.
     *
     * @param color The color of the good.
     */
    @JsonCreator
    public Good(@JsonProperty("color") Color color) {
        this.color = color;
    }

    /**
     * Gets the color of the good.
     *
     * @return The color of the good.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the good.
     *
     * @param color The new color of the good.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the value of the good based on its color.
     * The value is assigned as follows:
     * - RED: 4 points
     * - YELLOW: 3 points
     * - GREEN: 2 points
     * - BLUE: 1 point
     *
     * @return The value of the good.
     */
    @JsonIgnore
    public int getValue() {
        switch (color) {
            case RED -> {
                return 4;
            }
            case YELLOW -> {
                return 3;
            }
            case GREEN -> {
                return 2;
            }
            case BLUE -> {
                return 1;
            }


        }
        return 0;
    }
}
