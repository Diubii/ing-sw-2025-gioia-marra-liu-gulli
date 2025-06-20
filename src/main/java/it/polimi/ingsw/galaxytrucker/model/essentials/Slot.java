package it.polimi.ingsw.galaxytrucker.model.essentials;

import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;

import java.io.Serial;
import java.io.Serializable;

public class Slot implements Serializable {

    @Serial
    private static final long serialVersionUID = 12121L;

    private final Position position;
    private Tile TileContainer;
    private Boolean lastAction = false;

    public Slot(Position position) {
        this.position = position;
        this.TileContainer = null;
    }


    public Position getPosition() {
        return position;
    }

    public void removeTile() {
        TileContainer = null;
        setLastAction(Boolean.TRUE);
    }

    public Tile getTile() {
        return TileContainer;
    }


    public void putTile(Tile t) throws InvalidTilePosition {


        if (TileContainer == null) {
            this.TileContainer = new Tile(t);

        } else throw new InvalidTilePosition("INVALID_POSITION");

    }

    public boolean getLastAction() {
        return lastAction;
    }

    public void setLastAction(boolean lastAction) {
        this.lastAction = lastAction;
    }
}