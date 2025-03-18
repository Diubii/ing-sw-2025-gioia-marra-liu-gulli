package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class Cannon extends Component {
    protected Float FirePower;

    public Cannon(String name, Float firePower) {
        super(name, false);
        this.FirePower = firePower;
    }
    public Float getFirePower() {
        return FirePower;
    }

    public void calculateFP(){

        if (getRotation() != 0) {
            FirePower = FirePower/2;
        }
    }

    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

}
