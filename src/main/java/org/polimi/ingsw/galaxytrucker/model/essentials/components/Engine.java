package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentVisitorInterface;

public class Engine extends Component {
    protected int enginePower;

    @JsonCreator
    public Engine(@JsonProperty("enginePower") int enginePower) {

        super(false);
        this.enginePower = enginePower;
    }

    public int getEnginePower() {
        calculatePower();
        return enginePower;
    }

    private void calculatePower() {
        if (getRotation() != 0) {
            enginePower = 0;
        }
    }

    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
