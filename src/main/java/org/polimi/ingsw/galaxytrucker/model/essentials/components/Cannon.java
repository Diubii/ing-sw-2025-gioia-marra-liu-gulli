package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentVisitorInterface;

public class Cannon extends Component {
    protected Float FirePower;

    @JsonCreator
    public Cannon( @JsonProperty("firePower") Float firePower) {
        super(false);
        this.FirePower = firePower;
    }
    public Float getFirePower() {
        calculateFP();
        return FirePower;
    }

    private void calculateFP(){

        if (getRotation() != 0) {
            FirePower = FirePower/2;
        }
    }

    @Override

    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
