package org.polimi.ingsw.galaxytrucker.model.essentials.components;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

public class DoubleCannon extends Cannon {
    private  Boolean charged;

    @JsonCreator
    public DoubleCannon(@JsonProperty("charged") Boolean charged, @JsonProperty("firePower") Float firePower) {
        super(firePower);
        this.charged = charged;
    }

    public Boolean getCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }
    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }


}
