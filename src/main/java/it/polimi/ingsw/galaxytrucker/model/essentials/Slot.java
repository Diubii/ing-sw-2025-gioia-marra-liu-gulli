package it.polimi.ingsw.galaxytrucker.model.essentials;

import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a single grid cell (slot) on the ship board.
 * <p>
 * A slot holds one {@link Tile} at a fixed {@link Position}.
 */
public class Slot implements Serializable {

    @Serial
    private static final long serialVersionUID = 12121L;

    private final Position position;
    private Tile TileContainer;
    private Boolean lastAction = false;

    /**
     * Creates a slot at a given position.
     *
     * @param position The grid position of the slot.
     */
    public Slot(Position position) {
        this.position = position;
        this.TileContainer = null;
    }

    /**
     * Returns the slot's position on the board.
     *
     * @return The position of this slot.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Removes the tile from the slot.
     */
    public void removeTile() {
        TileContainer = null;
        setLastAction(Boolean.TRUE);
    }
    /**
     * Returns the tile currently placed in the slot.
     *
     * @return The tile in the slot, or null if empty.
     */
    public Tile getTile() {
        return TileContainer;
    }


    /**
     * Places a tile in the slot if it is currently empty.
     *
     * @param t The tile to place.
     * @throws InvalidTilePosition If the slot is already occupied.
     */
    public void putTile(Tile t) throws InvalidTilePosition {


        if (TileContainer == null) {
            this.TileContainer = new Tile(t);

        } else throw new InvalidTilePosition("INVALID_POSITION");

    }

    /**
     * Indicates whether the slot was involved in the last player action.
     *
     * @return {@code true} if it was recently modified; otherwise {@code false}.
     */
    public boolean getLastAction() {
        return lastAction;
    }
    /**
     * Marks whether the slot was involved in the last action.
     *
     * @param lastAction The flag value.
     */
    public void setLastAction(boolean lastAction) {
        this.lastAction = lastAction;
    }
}