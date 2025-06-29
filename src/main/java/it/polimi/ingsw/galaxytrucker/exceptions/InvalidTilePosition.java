package it.polimi.ingsw.galaxytrucker.exceptions;

/**
 * Thrown when a tile is placed in an invalid position.
 */
public class InvalidTilePosition extends Exception {
    public InvalidTilePosition(String message) {
        super(message);
    }
}
