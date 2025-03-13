package org.polimi.ingsw.galaxytruckers.model.units.components;


import org.polimi.ingsw.galaxytruckers.model.units.Tile;

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
