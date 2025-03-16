package org.polimi.ingsw.galaxytrucker.model.units.components;

import org.polimi.ingsw.galaxytrucker.model.units.Component;

public class BatterySlot extends Component {

    private int capacity;
    public BatterySlot(String name, int initialCapacity) {
        super(name);
        capacity = initialCapacity;

    }

    public int getBatteriesLeft(){
        return capacity;
    }

    public void removeBattery(){
        capacity--;
    }


}
