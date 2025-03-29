package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class Engine extends Component {
    protected int enginePower;

    public Engine(int enginePower) {

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



}
