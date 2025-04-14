package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

public class CentralHousingUnit extends Component {

    private final Color color;
    private int HumanCrewNumber = 0;
    private final Boolean isColored = Boolean.TRUE;

    public CentralHousingUnit(Color color) {
        super(false);
        this.color = color;
        if (!color.equals(Color.EMPTY)) setHumanCrewNumber(2);

    }

    public Color getColor() {
        return color;
    }
    public int getHumanCrewNumber() {
        return HumanCrewNumber;
    }
    private void setHumanCrewNumber(int humanCrewNumber) {
         if (humanCrewNumber > 0 && humanCrewNumber <= 2) {
             HumanCrewNumber = humanCrewNumber;

         } else throw new IllegalArgumentException("humanCrewNumber must be between 0 and 2");
    }
    public Boolean getIsColored() {
        return isColored;
    }
    public void removeHumanCrewNumber() {

        if (HumanCrewNumber > 0) HumanCrewNumber--;
    }
    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

}
