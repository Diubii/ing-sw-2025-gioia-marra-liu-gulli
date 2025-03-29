package org.polimi.ingsw.galaxytrucker.model.essentials.components;


import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class DoubleCannon extends Cannon {
    private  Boolean charged;

    public DoubleCannon(Boolean charged,  Float firePower) {
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
