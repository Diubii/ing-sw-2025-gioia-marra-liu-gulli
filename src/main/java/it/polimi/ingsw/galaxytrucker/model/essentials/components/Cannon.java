package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * Represents a Cannon, a type of Component that has a specific firepower.
 * The firepower value can be dynamically recalculated based on the rotation of the component.
 */
public class Cannon extends Component {
    protected Float FirePower;

    @JsonCreator
    public Cannon(@JsonProperty("firePower") Float firePower) {
        super(false);
        this.FirePower = firePower;
    }

    /**
     * Returns the current firepower of the Cannon. The firepower value may be dynamically
     * recalculated based on the component's rotation before being returned.
     *
     * @return the current firepower of the Cannon as a Float
     */
    public Float getFirePower() {
        calculateFP();
        return FirePower;
    }

    /**
     * Dynamically adjusts the firepower of the Cannon based on its current rotation.
     * If the rotation of the Cannon is non-zero, the firepower is halved. This method
     * ensures the firepower value reflects the Cannon's state at the time of invocation.
     */
    private void calculateFP() {
        if (getRotation() != 0) {
            FirePower = FirePower / 2;
        }
    }
    @Override
    public Cannon clone() {
        Cannon copy = (Cannon) super.clone();
        copy.FirePower = this.FirePower;
        return copy;
    }
    @Override

    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
