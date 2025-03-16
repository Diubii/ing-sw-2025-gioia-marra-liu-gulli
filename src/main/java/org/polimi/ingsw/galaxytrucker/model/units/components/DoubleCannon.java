package org.polimi.ingsw.galaxytrucker.model.units.components;


public class DoubleCannon extends Cannon {
    private  Boolean charged;

    public DoubleCannon(Boolean charged, String name, Float firePower) {
        super(name, firePower);
        this.charged = charged;
    }

    public Boolean getCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

}
