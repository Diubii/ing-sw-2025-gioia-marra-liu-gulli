package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentVisitorInterface;

public class ModularHousingUnit extends CentralHousingUnit{

    private int nBrownAlien = 0;
    private int nPurpleAlien = 0;
    private  AlienColor alienColor;

    @JsonCreator
    public ModularHousingUnit(@JsonProperty("color") Color color,@JsonProperty("nbrownAlien") int nBrownAlien, @JsonProperty("npurpleAlien") int nPurpleAlien,@JsonProperty("alienColor")  AlienColor alienColor, @JsonProperty("humanCrewNumber") int humanCrew) {
        super(color,humanCrew);
        this.nBrownAlien = nBrownAlien;
        this.nPurpleAlien = nPurpleAlien;
        this.alienColor = alienColor;
    }

    public ModularHousingUnit() {
        super(Color.EMPTY);
    }


    public void addBrownAlien(){

        if (alienColor == AlienColor.BROWN) {
            nBrownAlien += 1;
        }
        alienColor = AlienColor.BROWN;

    }

    public void addPurpleAlien(){
        if (alienColor == AlienColor.PURPLE) {
            nPurpleAlien += 1;
        }
        alienColor = AlienColor.PURPLE;
    }

    public int getNBrownAlien() {
        return nBrownAlien;
    }

    public int getNPurpleAlien() {
        return nPurpleAlien;
    }

    public void removeBrownAlien() {
        if (nBrownAlien > 0 && alienColor == AlienColor.BROWN) {
            nBrownAlien -= 1;
            this.alienColor = AlienColor.EMPTY;

        }

    }

    public void removeAlienCrew() {
        nPurpleAlien = 0;
        nBrownAlien = 0;
        this.alienColor = AlienColor.EMPTY;

    }

    public void removePurpleAlien() {
        if (nPurpleAlien > 0 && alienColor == AlienColor.PURPLE) {
            nPurpleAlien -= 1;
            this.alienColor = AlienColor.EMPTY;
        }
    }

    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

    public void addHumanCrew(){
        super.setHumanCrewNumber(2);
    }

    public int getHumanCrewNumber(){
        int result = 0;
        switch (alienColor){
            case PURPLE -> result = getNPurpleAlien();
            case BROWN -> result = getNBrownAlien();
            case EMPTY -> result = this.getHumanCrewNumber();
            default -> throw new IllegalStateException("Multiversal housing issue.");
        }
        return result;
    }

    public AlienColor getAlienColor(){
        return this.alienColor;
    }

}
