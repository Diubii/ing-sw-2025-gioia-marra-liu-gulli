package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.model.Ship;

public class Slot {
    private Position position;
    private Tile TileContainer;
    private Boolean lastAction = false;
    public Slot(Position position) {
        this.position = position;
        this.TileContainer = null;
    }



    public Position getPosition() {
        return position;
    }

    public void removeTile(){
        TileContainer = null;
        setLastAction(Boolean.FALSE);
    }

    public Tile getTile(){
        return TileContainer;
    }


    public void putTile(Tile t){
     this.TileContainer = new Tile(t);
    }

    public boolean getLastAction() {
        return lastAction;
    }

    public void setLastAction(boolean lastAction) {
        this.lastAction = lastAction;
    }
}
