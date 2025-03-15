package org.polimi.ingsw.galaxytruckers.model.units.components;

import org.polimi.ingsw.galaxytruckers.enums.Color;
import org.polimi.ingsw.galaxytruckers.model.units.Component;

public class CentralHousingUnit extends Component {

    private Color color;
    private int HumanCrewNumber;

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
