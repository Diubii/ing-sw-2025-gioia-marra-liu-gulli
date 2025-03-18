package org.polimi.ingsw.galaxytrucker.model.essentials;

/**
 * Used to manage position in a cartesian space
 * Used for the position in the ship matrix
 * ...
 */
public class Position {
    private int x;
    private int y;

    public Position(int y, int x) {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

}
