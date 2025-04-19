package org.polimi.ingsw.galaxytrucker.model.essentials;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BatterySlot.class, name = "BatterySlot"),
        @JsonSubTypes.Type(value = Cannon.class, name = "Cannon"),
        @JsonSubTypes.Type(value = CentralHousingUnit.class, name = "CentralHousingUnit"),
        @JsonSubTypes.Type(value = DoubleCannon.class, name = "DoubleCannon"),
        @JsonSubTypes.Type(value = DoubleEngine.class, name = "DoubleEngine"),
        @JsonSubTypes.Type(value = Engine.class, name = "Engine"),
        @JsonSubTypes.Type(value = GenericCargoHolds.class, name = "GenericCargoHolds"),
        @JsonSubTypes.Type(value = LifeSupportSystem.class, name = "LifeSupportSystem"),
        @JsonSubTypes.Type(value = ModularHousingUnit.class, name = "ModularHousingUnit"),
        @JsonSubTypes.Type(value = Shield.class, name = "Shield"),

})
/**
 * Represents a component in the game, which could be part of a ship .
 * Each component has a name, a structural property, and a rotation value.
 */
public  class Component implements Serializable {


//    private final String Name;

    private Boolean Structural;

    /** The rotation of the component (in degrees). */
    protected int rotation = 0;

    /**
     * Constructs a Component with the specified name and structural property.
     *
     * @param structural .
     */
    @JsonCreator
    public Component( @JsonProperty("structural") Boolean structural) {
//        this.Name = name;

        Structural = structural;

    }

//    public void getName(ComponentNameVisitor cnm){
//
//
//
//    }
//
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
