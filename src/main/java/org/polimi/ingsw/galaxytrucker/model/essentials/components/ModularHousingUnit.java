package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class ModularHousingUnit extends CentralHousingUnit{

    private Boolean isColored = Boolean.FALSE;
    private int nBrownAlien;
    private int nPurpleAlien;
    private AlienColor alienColor;

    public ModularHousingUnit( AlienColor color) {

        super(Color.EMPTY);
        this.alienColor = color;

    }


    public void addBrownAlien(){

        if (alienColor == AlienColor.BROWN) {
            nBrownAlien += 1;
        }

    }

    public void addPurpleAlien(){
        if (alienColor == AlienColor.PURPLE) {
            nPurpleAlien += 1;
        }
    }

    public int getNBrownAlien() {
        return nBrownAlien;
    }

    public int getNPurpleAlien() {
        return nPurpleAlien;
    }

    public void removeBrownAlien(){
        if (nBrownAlien > 0 && alienColor == AlienColor.BROWN) nBrownAlien -= 1;

    }

    public void removePurpleAlien(){
        if (nPurpleAlien > 0 && alienColor == AlienColor.PURPLE) nPurpleAlien -= 1;
    }

    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); 
    }


}
