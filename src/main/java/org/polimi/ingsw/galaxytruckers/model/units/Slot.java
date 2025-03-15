package org.polimi.ingsw.galaxytruckers.model.units;

public class Slot {
    private Position position;
    private Ship myShip;
    private Tile TileContainer;
    public Slot(Position position, Ship myShip) {
        this.position = position;
        this.myShip = myShip;
    }



    public Position getPosition() {
        return position;
    }

    public void removeTile(){
        TileContainer = null;
    }

    public Tile getTile(){
        return TileContainer;
    }
    public Ship getMyShip() {
        return myShip;
    }


    public void putTile(Tile t){
        this.TileContainer = new Tile(t);
    }
}
