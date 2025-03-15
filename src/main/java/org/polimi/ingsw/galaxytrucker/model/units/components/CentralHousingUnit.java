package org.polimi.ingsw.galaxytrucker.model.units.components;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.units.Component;

public class CentralHousingUnit extends Component {

    private Color color;
    private int HumanCrewNumber;
    private Boolean isColored = Boolean.TRUE;

    public CentralHousingUnit(String name, Color color, int HumanCrewNumber) {
        super(name);
        this.color = color;
        this.HumanCrewNumber = HumanCrewNumber;

    }

    public Color getColor() {
        return color;
    }
    public int getHumanCrewNumber() {
        return HumanCrewNumber;
    }
    public void removeHumanCrewNumber() {
        HumanCrewNumber--;
    }
}
