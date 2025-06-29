package it.polimi.ingsw.galaxytrucker.model.essentials;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.Connector;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * Represents a spaceship tile with connectors and a component.
 * <p>Tiles can be rotated or flipped and placed on the ship grid.</p>
 */
public class Tile implements Serializable,Cloneable {

    @Serial
    private static final long serialVersionUID = 535L;

    private int id;/**


    /**
     * Current rotation angle of the tile (0, 90, 180, or 270 degrees).
     */
    private int Rotation;

    /**
     * Indicates whether the tile has been flipped horizontally.
     */
    private boolean flipped;

    /**
     * List of connectors on each side of the tile.
     * The order is typically: North, East, South, West.
     */
    private ArrayList<Connector> sides;

    /**
     * Number of exposed connectors on this tile.
     */
    private int exposedConnectors;

    /**
     * Indicates whether this tile has been used in the current game.
     */
    private Boolean used;

    /**
     * Indicates whether this tile is fixed in place (cannot be moved).
     */
    private Boolean fixed;

    /**
     * The game component associated with this tile.
     */
    private Component myComponent;

    /**
     * Indicates whether this tile is well-connected to adjacent tiles.
     */
    private Boolean wellConnected;

    /**
     * Constructs a new Tile with specified properties.
     *
     * @param id         the unique identifier for this tile
     * @param rotation   the initial rotation angle (0, 90, 180, or 270)
     * @param s          list of connectors for each side
     * @param c          the component associated with this tile
     */
    public Tile(int id, int rotation, ArrayList<Connector> s, Component c) {
        this.id = id;
        Rotation = rotation;
        this.flipped = false;
        this.sides = new ArrayList<Connector>(s);
        this.exposedConnectors = 0;
        this.used = false;
        this.fixed = false;
        this.wellConnected = true;
        this.myComponent = c;
    }

    /**
     * Jackson JSON constructor for deserializing a Tile.
     *
     * @param id            the unique identifier for this tile
     * @param rotation      the initial rotation angle
     * @param flipped       whether the tile is flipped
     * @param s             list of connectors for each side
     * @param c             the component associated with this tile
     * @param fixed         whether the tile is fixed in place
     * @param wellConnected whether the tile is well-connected
     */
    @JsonCreator
    public Tile(
            @JsonProperty("id") int id,
            @JsonProperty("rotation") int rotation,
            @JsonProperty("flipped") boolean flipped,
            @JsonProperty("sides") ArrayList<Connector> s,
            @JsonProperty("myComponent") Component c,
            @JsonProperty("fixed") boolean fixed,
            @JsonProperty("wellConnected") boolean wellConnected
    ) {
        this.id = id;
        Rotation = rotation;
        this.flipped = flipped;
        this.sides = new ArrayList<>(s);
        this.exposedConnectors = 0;
        this.used = false;
        this.fixed = fixed;
        this.wellConnected = wellConnected;
        this.myComponent = c;
    }

    /**
     * Copy constructor for creating a Tile from another Tile.
     *
     * @param other the Tile to copy
     */
    public Tile(Tile other) {
        this.id = other.id;
        this.Rotation = other.Rotation;
        this.flipped = other.flipped;
        this.sides = new ArrayList<>(other.sides); // Cloniamo la lista di connettori
        this.exposedConnectors = other.exposedConnectors;
        this.used = other.used;
        this.fixed = other.fixed;
        this.wellConnected = other.wellConnected;
        this.myComponent = other.myComponent;
    }

    /**
     * Sets the unique identifier for this tile.
     *
     * @param id the new ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the component associated with this tile.
     *
     * @param myComponent1 the component to associate with this tile
     */
    public void setMyComponent(Component myComponent1) {
        myComponent = myComponent1;
    }

    /**
     * Gets the component associated with this tile.
     *
     * @return the component
     */
    public Component getMyComponent() {
        return myComponent;
    }

    /**
     * Marks this tile as used or unused.
     *
     * @param used true to mark as used, false otherwise
     */
    public void setUsed(Boolean used) {
        this.used = used;
    }

    /**
     * Checks if this tile has been used.
     *
     * @return true if used, false otherwise
     */
    public Boolean getUsed() {
        return used;
    }

    /**
     * Checks if this tile is fixed in place.
     *
     * @return true if fixed, false otherwise
     */
    public Boolean getFixed() {
        return fixed;
    }

    /**
     * Gets the number of exposed connectors on this tile.
     *
     * @return the number of exposed connectors
     */
    public int getExposedConnectors() {
        return exposedConnectors;
    }

    /**
     * Gets the list of connectors on this tile's sides.
     *
     * @return a new list containing this tile's connectors
     */
    public ArrayList<Connector> getSides() {
        return new ArrayList<Connector>(sides);
    }

    /**
     * Modifies the count of exposed connectors on this tile.
     *
     * @param i the amount to adjust by (positive or negative)
     */
    public void modifyExposedConnector(int i) {
        exposedConnectors += i;
    }

    /**
     * Gets the unique identifier of this tile.
     *
     * @return the tile ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the current rotation angle of this tile.
     *
     * @return the rotation angle (0, 90, 180, or 270)
     */
    public int getRotation() {
        return Rotation;
    }

    /**
     * Sets the rotation angle of this tile.
     *
     * @param rotation the new rotation angle (0, 90, 180, or 270)
     */
    public void setRotation(int rotation){
        Rotation = rotation;
    }

    /**
     * Rotates the tile by the specified angle increment.
     * Also rotates the associated component and adjusts connector positions.
     *
     * @param addRotation the angle to rotate (typically 90, 180, or 270)
     */
    public void rotate(int addRotation) {
        Rotation = (Rotation + addRotation) % 360;
        myComponent.setRotation(Rotation);
        setRotation(Rotation);
        int numRotation = ((addRotation % 360) + 360) % 360 / 90;
        for (int i = 0; i < numRotation; i++) {
            rotateSides90();
        }
    }

    /**
     * Rotates the connectors on the tile's sides by 90 degrees clockwise.
     */
    private void rotateSides90() {
        if (sides == null || sides.size() != 4) return;

        Connector temp = sides.get(3);
        sides.set(3, sides.get(2));
        sides.set(2, sides.get(1));
        sides.set(1, sides.get(0));
        sides.set(0, temp);
    }

    /**
     * Checks if the tile has been flipped.
     *
     * @return true if flipped, false otherwise
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     * Toggles the flip state of this tile.
     */
    public void flip() {
        this.flipped = !this.flipped;
    }

    /**
     * Checks if this tile is well-connected to adjacent tiles.
     *
     * @return true if well-connected, false otherwise
     */
    public Boolean getWellConnected() {
        return wellConnected;
    }

    /**
     * Sets the connection status of this tile.
     *
     * @param wellConnected true if well-connected, false otherwise
     */
    public void setWellConnected(Boolean wellConnected) {
        this.wellConnected = wellConnected;
    }

    /**
     * Prints debug information about this tile to the console.
     */
    public void testPrint() {
        System.out.print(this.id);
        System.out.print(this.Rotation);
        System.out.print(this.flipped);
        System.out.print(this.sides);
        System.out.print(this.exposedConnectors);
        System.out.print(this.used);
        System.out.println(this.fixed);
    }

    /**
     * Sets the fixed status of this tile.
     *
     * @param fixed true to fix the tile in place, false otherwise
     */
    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    /**
     * Creates a deep copy of this Tile object.
     *
     * @return a new Tile instance with the same state as this tile
     */
    @Override
    public Tile clone() {
        try {
            Tile copy = new Tile(this);
            if (this.myComponent != null) {
                copy.myComponent = this.myComponent.clone();
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone Tile", e);
        }
    }
}

