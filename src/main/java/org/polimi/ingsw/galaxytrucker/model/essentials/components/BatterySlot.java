package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

public class BatterySlot extends Component {

    private int batteriesLeft;

    @JsonCreator
    public BatterySlot(@JsonProperty("batteriesLeft") int batteriesLeft) {
        super(false);
        this.batteriesLeft = batteriesLeft;

    }

    public int getBatteriesLeft() {
        return batteriesLeft;
    }

    public boolean removeBattery() {
        if(batteriesLeft>0){
            batteriesLeft--;
            return true;
        }
        else return false;
    }

    @Override

    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
