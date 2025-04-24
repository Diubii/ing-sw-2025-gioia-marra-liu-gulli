package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;

public class CentralHousingUnit extends Component {

    private final Color color;
    private int humanCrewNumber = 0;
    private final Boolean isColored = Boolean.TRUE;

    @JsonCreator
    public CentralHousingUnit( @JsonProperty("color") Color color ,@JsonProperty("humanCrewNumber") int humanCrewNumber) {
        super(false);
        this.color = color;
        if (!color.equals(Color.EMPTY)) setHumanCrewNumber(2);

    }

    public CentralHousingUnit( Color color) {
        super(false);
        this.color = color;
        if (!color.equals(Color.EMPTY)) setHumanCrewNumber(2);
    }

    public Color getColor() {
        return color;
    }
    public int getHumanCrewNumber() {
        return humanCrewNumber;
    }
    public void setHumanCrewNumber(int humanCrewNumber) {
         if (humanCrewNumber > 0 && humanCrewNumber <= 2) {
           this.humanCrewNumber = humanCrewNumber;

         } else throw new IllegalArgumentException("humanCrewNumber must be between 0 and 2");
    }
    public Boolean getIsColored() {
        return isColored;
    }
    public void removehumanCrewNumber() {

        if (humanCrewNumber > 0) humanCrewNumber--;
    }
    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

    @Override
    public String[] accept(ComponentPrintVisitorInterface visitor) {
        return visitor.visit(this);
    }

}
