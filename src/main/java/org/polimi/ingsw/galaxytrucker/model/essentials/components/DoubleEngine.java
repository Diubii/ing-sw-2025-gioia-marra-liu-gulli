package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentVisitorInterface;

public class DoubleEngine extends Engine {

    private Boolean charged;

    @JsonCreator
    public DoubleEngine(@JsonProperty("charged") Boolean charged, @JsonProperty("enginePower") int enginePower) {

        super(enginePower);
        this.charged = charged;
    }

    @Override
    public int getEnginePower() {
        calculatePower();
        return enginePower;
    }

    private void calculatePower() {
        if (getRotation() != 0) {
            if (charged) {
                enginePower = 2;
                charged = false;
            } else enginePower = 1;
        } else {
            enginePower = 0;
        }
    }


    @Override
    public Boolean isCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }


}
