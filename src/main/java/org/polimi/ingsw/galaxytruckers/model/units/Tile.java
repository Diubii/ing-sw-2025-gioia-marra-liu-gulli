package org.polimi.ingsw.galaxytruckers.model.units;

public class Tile {
    private int Rotation;

    public Tile(int rotation) {
        Rotation = rotation;
    }
    public int getRotation() {
        return Rotation;
    }

    public void rotate(int addRotation) {
        Rotation += addRotation;
        Rotation %= 360;
    }
}
