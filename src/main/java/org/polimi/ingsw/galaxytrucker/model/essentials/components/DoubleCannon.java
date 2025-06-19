package org.polimi.ingsw.galaxytrucker.model.essentials.components;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

public class DoubleCannon extends Cannon {
    private Boolean charged;

    @JsonCreator
    public DoubleCannon(@JsonProperty("charged") Boolean charged, @JsonProperty("firePower") Float firePower) {
        super(firePower);
        this.charged = charged;
    }

    @Override
    public Float getFirePower() {
        calculateFP();
        return FirePower;
    }

    private void calculateFP() {
        if (getRotation() == 0) {
            //FirePower =2
            if (charged == false) {
                FirePower = 1.0F;
            }
            else{
                FirePower = 2.0F;
                charged = false;
            }
            //Altrimenti rimane uguale
        } else {
            if (charged==false) {
                FirePower = 0.5F;
            } else { //Altrimenti rimane uguale
                charged = false;
                FirePower = 1.0F;
            }
        }
    }

    public Boolean isCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

    @Override
    public DoubleCannon clone() {
        DoubleCannon copy = (DoubleCannon) super.clone();
        copy.charged = this.charged;
        return copy;
    }
    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }
}
