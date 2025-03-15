package org.polimi.ingsw.galaxytruckers.model.units;

public class Tile {
    private int id;
    private int Rotation;
    private boolean isFaceUp;

    public Tile(int id,int rotation) {
        this.id = id;
        Rotation = rotation;
        this.isFaceUp = false;
    }

    public int getId() {
        return id;
    }
    public int getRotation() {
        return Rotation;
    }

    public void rotate(int addRotation) {
        Rotation += addRotation;
        Rotation %= 360;
    }

    public boolean IsFaceUp(){
        return isFaceUp;
    }

    public void flip(){
        this.isFaceUp = !this.isFaceUp;
    }
}
