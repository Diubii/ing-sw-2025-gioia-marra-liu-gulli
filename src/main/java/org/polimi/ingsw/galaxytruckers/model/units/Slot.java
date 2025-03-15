package org.polimi.ingsw.galaxytruckers.model.units;

public class Slot {
    private Position position;
    private Ship myShip;
    public Slot(Position position) {
        this.position = position;
    }
    public Position getPosition() {
        return position;
    }

}
