package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * Represents a modular housing unit in the Galaxy Trucker game.
 * This class extends CentralHousingUnit and provides functionality for managing different types of crew members,
 * including human crew and alien crew (brown and purple).
 */
public class ModularHousingUnit extends CentralHousingUnit {

    /**
     * The number of brown aliens currently housed in this module.
     */
    private int nBrownAlien = 0;

    /**
     * The number of purple aliens currently housed in this module.
     */
    private int nPurpleAlien = 0;

    /**
     * Sets the color of the alien currently assigned to this housing unit.
     *
     * @param alienColor the AlienColor to set
     */
    public void setAlienColor(AlienColor alienColor) {
        this.alienColor = alienColor;
    }

    /**
     * The color of the alien currently assigned to this housing unit.
     */
    private AlienColor alienColor;

    /**
     * Constructs a ModularHousingUnit with specified properties.
     *
     * @param color         the base color of the housing unit
     * @param nBrownAlien   the initial number of brown aliens
     * @param nPurpleAlien  the initial number of purple aliens
     * @param alienColor    the color of the alien assigned to this unit
     * @param humanCrew     the number of human crew members
     */
    @JsonCreator
    public ModularHousingUnit(@JsonProperty("color") Color color,
                              @JsonProperty("nbrownAlien") int nBrownAlien,
                              @JsonProperty("npurpleAlien") int nPurpleAlien,
                              @JsonProperty("alienColor") AlienColor alienColor,
                              @JsonProperty("humanCrewNumber") int humanCrew) {
        super(color, humanCrew);
        this.nBrownAlien = nBrownAlien;
        this.nPurpleAlien = nPurpleAlien;
        this.alienColor = alienColor != null ? alienColor : AlienColor.EMPTY;
    }

    /**
     * Default constructor that initializes an empty modular housing unit.
     */
    public ModularHousingUnit() {
        super(Color.EMPTY);
        this.alienColor = AlienColor.EMPTY;
    }

    /**
     * Adds one brown alien to the module and sets the alien color to brown.
     */
    public void addBrownAlien() {
        alienColor = AlienColor.BROWN;
        nBrownAlien += 1;
    }

    /**
     * Adds one purple alien to the module and sets the alien color to purple.
     */
    public void addPurpleAlien() {
        alienColor = AlienColor.PURPLE;
        nPurpleAlien += 1;
    }

    /**
     * Adds two human crew members to the module.
     */
    public void addHumanCrew() {
        super.setHumanCrewNumber(2);
    }

    /**
     * Removes one brown alien from the module.
     * If no brown aliens remain, sets the alien color back to empty.
     */
    public void removeBrownAlien() {
        if (nBrownAlien > 0 && alienColor == AlienColor.BROWN) {
            nBrownAlien -= 1;
            this.alienColor = AlienColor.EMPTY;
        }
    }

    /**
     * Removes all crew members (aliens and humans) from the module.
     */
    public void removeAllCrew(){
        nPurpleAlien = 0;
        nBrownAlien = 0;
        super.setHumanCrewNumber(0);
        this.alienColor = AlienColor.EMPTY;
    }

    /**
     * Removes only the alien crew members from the module.
     */
    public void removeAlienCrew() {
        nPurpleAlien = 0;
        nBrownAlien = 0;
        this.alienColor = AlienColor.EMPTY;
    }

    /**
     * Removes one purple alien from the module.
     * If no purple aliens remain, sets the alien color back to empty.
     */
    public void removePurpleAlien() {
        if (nPurpleAlien > 0 && alienColor == AlienColor.PURPLE) {
            nPurpleAlien -= 1;
            this.alienColor = AlienColor.EMPTY;
        }
    }

    /**
     * Removes one crew member based on the current alien color.
     * If the alien color is empty, removes a human crew member.
     * If it's BROWN or PURPLE, removes all corresponding alien crew members.
     */
    @Override
    public void removeCrewMember() {
        switch (alienColor) {
            case EMPTY -> super.removeCrewMember();
            case BROWN -> removeAllCrew();
            case PURPLE -> removeAllCrew();
        }
    }

    /**
     * Gets the total number of crew members currently in the module.
     *
     * @return the number of crew members
     */
    @Override
    public int getNCrewMembers() {
        int result = 0;
        switch (alienColor) {
            case PURPLE -> result = getNPurpleAlien();
            case BROWN -> result = getNBrownAlien();
            case EMPTY -> result = super.getNCrewMembers();
            default -> throw new IllegalStateException("Multiversal housing issue.");
        }
        return result;
    }

    /**
     * Gets the number of brown aliens currently in the module.
     *
     * @return the number of brown aliens
     */
    public int getNBrownAlien() {
        return nBrownAlien;
    }

    /**
     * Gets the number of purple aliens currently in the module.
     *
     * @return the number of purple aliens
     */
    public int getNPurpleAlien() {
        return nPurpleAlien;
    }

    /**
     * Gets the color of the alien currently assigned to this module.
     *
     * @return the AlienColor
     */
    public AlienColor getAlienColor() {
        return this.alienColor;
    }

    /**
     * Creates a copy of this ModularHousingUnit.
     *
     * @return a new ModularHousingUnit instance with the same state
     */
    @Override
    public ModularHousingUnit clone() {
        ModularHousingUnit copy = (ModularHousingUnit) super.clone();
        copy.nBrownAlien = this.nBrownAlien;
        copy.nPurpleAlien = this.nPurpleAlien;
        copy.alienColor = this.alienColor;
        return copy;
    }

    /**
     * Accepts a ComponentVisitorInterface to perform operations on this component.
     *
     * @param visitor the visitor to accept
     * @return the result of visiting this component
     */
    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }
}
