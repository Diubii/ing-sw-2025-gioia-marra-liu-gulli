package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class ModularHousingUnit extends CentralHousingUnit{

    private Boolean isColored = Boolean.FALSE;
    private int nBrownAlien;
    private int nPurpleAlien;

    public ModularHousingUnit(String name, Color color, int HumanCrewNumber) {
        super(name, color, HumanCrewNumber);
    }

    public void updateBrownAlien(){
        nBrownAlien += 1;
    }

    public void updatePurpleAlien(){
        nPurpleAlien += 1;
    }

    public int getNBrownAlien() {
        return nBrownAlien;
    }

    public int getNPurpleAlien() {
        return nPurpleAlien;
    }

    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }


}
