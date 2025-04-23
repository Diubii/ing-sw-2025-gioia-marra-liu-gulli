package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;

public class Engine extends Component {
    protected int enginePower;

    @JsonCreator
    public Engine( @JsonProperty("enginePower") int enginePower) {

        super(false);
        this.enginePower = enginePower;
    }
    public int getEnginePower() {
        return enginePower;
    }

    public void calculatePower() {
        if (getRotation() != 0) {
            enginePower = 0;
        }
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
