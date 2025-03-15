package org.polimi.ingsw.galaxytruckers.model.units;

public class Tile {
    private int Rotation;
    protected Slot mySlot;

    public Tile(int rotation, Slot slot) {
        Rotation = rotation;
        mySlot = slot;
    }
    public int getRotation() {
        return Rotation;
    }

    public void rotate(int addRotation) {
        Rotation += addRotation;
        Rotation %= 360;
    }
}
