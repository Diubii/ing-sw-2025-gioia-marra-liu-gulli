package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;

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
