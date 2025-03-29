package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class BatterySlot extends Component {

    private int capacity;
    public BatterySlot( int initialCapacity) {
        super(false);
        capacity = initialCapacity;

    }

    public int getBatteriesLeft(){
        return capacity;
    }

    public void removeBattery(){
        capacity--;
    }
    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }


}
