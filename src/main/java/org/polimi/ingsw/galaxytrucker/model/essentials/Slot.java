package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.model.Ship;

public class Slot {
    private Position position;
    private Tile TileContainer;
    public Slot(Position position) {
        this.position = position;
        this.TileContainer = null;
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


    public void putTile(Tile t){
     this.TileContainer = new Tile(t);
    }
}
