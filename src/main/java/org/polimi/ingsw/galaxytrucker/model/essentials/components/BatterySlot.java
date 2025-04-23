package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;

public class BatterySlot extends Component {

    private int batteriesLeft;
    @JsonCreator
    public BatterySlot( @JsonProperty("batteriesLeft") int batteriesLeft) {
        super(false);
        this.batteriesLeft = batteriesLeft;

    }

    public int getBatteriesLeft() {
        return batteriesLeft;
    }

    public void removeBattery(){
        batteriesLeft--;
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
