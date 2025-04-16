package org.polimi.ingsw.galaxytrucker.model.essentials;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

/**
 * Represents a component in the game, which could be part of a ship .
 * Each component has a name, a structural property, and a rotation value.
 */
public  class Component {
    @JsonIgnore
    /** The tile associated with the component. */
    private  Tile myTile;

    //private final String Name;

    private Boolean Structural;

    /** The rotation of the component (in degrees). */
    protected int rotation = 0;

    @JsonCreator
    public Component() {

    }

    /**
     * Constructs a Component with the specified name and structural property.
     *
     * @param structural .
     */
    public Component(Boolean structural) {
        Structural = structural;

    }

    /**
     * Gets the current rotation of the component.
     *
     * @return The rotation value in degrees.
     */
    public int getRotation() {
        return rotation;
    }
    /**
     * Sets the rotation of the component.
     *
     * @param r The rotation value in degrees.
     */
    public void setRotation(int r) {
        rotation = r;
    }

    /**
     * Accepts a visitor that can interact with this component.
     * The visitor pattern is used to process the component's name.
     *
     * @param visitor The visitor that interacts with the component.
     * @return A string result from the visitor's interaction with the component.
     */

    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }}
