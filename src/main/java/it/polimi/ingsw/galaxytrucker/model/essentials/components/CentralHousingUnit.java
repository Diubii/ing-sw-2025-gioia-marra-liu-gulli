package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * Represents the central housing unit of a structure or ship.
 * This component is associated with a specific color and has the capacity
 * to host a limited number of human crew members.
 * It provides functionality to manage and query the crew members as well as
 * the state of being colored.
 * Extends the {@link Component} class to inherit base component characteristics.
 */
public class CentralHousingUnit extends Component {

    private final Color color;
    private int humanCrewNumber = 0;
    private Boolean isColored = Boolean.TRUE;

    @JsonCreator
    public CentralHousingUnit(@JsonProperty("color") Color color, @JsonProperty("humanCrewNumber") int humanCrewNumber) {
        super(false);
        this.color = color;
        if (!color.equals(Color.EMPTY)) setHumanCrewNumber(2);
        if (color.equals(Color.EMPTY)) isColored = false;

    }

    public CentralHousingUnit(Color color) {
        super(false);
        this.color = color;
        if (!color.equals(Color.EMPTY)) setHumanCrewNumber(2);
        if (color.equals(Color.EMPTY)) isColored = false;

    }

    public Color getColor() {
        return color;
    }

    public int getNCrewMembers() {
        return humanCrewNumber;
    }

    /**
     * Sets the number of human crew members assigned to the housing unit.
     * The number of crew members must be within the valid range, inclusive.
     *
     * @param humanCrewNumber the new number of human crew members. Must be between 0 and 2.
     * @throws IllegalArgumentException if the provided humanCrewNumber is outside the range [0, 2].
     */
    public void setHumanCrewNumber(int humanCrewNumber) {
        if (humanCrewNumber >= 0 && humanCrewNumber <= 2) {
            this.humanCrewNumber = humanCrewNumber;

        } else throw new IllegalArgumentException("humanCrewNumber must be between 0 and 2");
    }

    /**
     * Determines whether the component is associated with a color.
     *
     * @return {@code true} if the component is colored, {@code false} otherwise.
     */
    public Boolean getIsColored() {
        return isColored;
    }

    public void removeCrewMember() {
        if (humanCrewNumber > 0) humanCrewNumber--;
    }

    @Override
    public CentralHousingUnit clone() {
        CentralHousingUnit copy = (CentralHousingUnit) super.clone();
        copy.humanCrewNumber = this.humanCrewNumber;
        copy.isColored = this.isColored;
        copy.setRotation(this.getRotation());
        return copy;
    }
    @Override

    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }


}
