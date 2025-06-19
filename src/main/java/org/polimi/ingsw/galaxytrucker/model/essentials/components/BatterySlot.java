package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * Represents a battery slot component within the game structure.
 * A BatterySlot component holds a certain number of batteries which can be used or removed.
 * Extends the Component class to inherit general properties and behaviors of a component.
 */
public class BatterySlot extends Component {

    private int batteriesLeft;

    @JsonCreator
    public BatterySlot(@JsonProperty("batteriesLeft") int batteriesLeft) {
        super(false);
        this.batteriesLeft = batteriesLeft;

    }

    /**
     * Retrieves the number of batteries currently available in the battery slot.
     *
     * @return the remaining number of batteries in the battery slot
     */
    public int getBatteriesLeft() {
        return batteriesLeft;
    }

    /**
     * Removes a battery from the battery slot if at least one battery is available.
     * Decrements the count of batteries by one when successful.
     *
     * @return {@code true} if a battery was successfully removed, {@code false} otherwise.
     */
    public boolean removeBattery() {
        if(batteriesLeft>0){
            batteriesLeft--;
            return true;
        }
        else return false;
    }

    @Override
    public BatterySlot clone() {
        BatterySlot copy = (BatterySlot) super.clone();
        copy.batteriesLeft = this.batteriesLeft;
        return copy;
    }

    @Override

    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
