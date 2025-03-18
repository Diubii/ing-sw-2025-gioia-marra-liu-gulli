package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class CentralHousingUnit extends Component {

    private Color color;
    private int HumanCrewNumber;
    private Boolean isColored = Boolean.TRUE;

    public CentralHousingUnit(String name, Color color, int HumanCrewNumber) {
        super(name, false);
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
    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

}
