package org.polimi.ingsw.galaxytrucker.model.essentials.components;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentVisitorInterface;

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
        if(getRotation()!=0){
            if(charged){
               FirePower = FirePower*2;
            }
            //altrimenti rimane uguale
        }
        else{
            if(!charged){
                FirePower = FirePower/2;
            }
            //altrimenti rimane uguale
        }
    }

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
