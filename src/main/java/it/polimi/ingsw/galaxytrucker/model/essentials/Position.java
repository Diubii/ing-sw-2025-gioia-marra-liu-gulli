package it.polimi.ingsw.galaxytrucker.model.essentials;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a position in a 2D Cartesian space.
 * This class is used for managing positions within the ship's matrix.
 * ...
 */
public class Position implements Serializable {

    @Serial
    private static final long serialVersionUID = 6767L;

    private int x;
    private int y;

    /**
     * Constructs a Position object with the specified x and y coordinates.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Compares this position to another object for equality.
     *
     * @param obj The object to compare with.
     * @return {@code true} if the object is a Position and has the same x and y coordinates, otherwise {@code false}.
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}