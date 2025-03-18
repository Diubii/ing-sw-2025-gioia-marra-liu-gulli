package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;

public class Cannon extends Component {
    protected Float FirePower;

    public Cannon(String name, Float firePower) {
        super(name);
        this.FirePower = firePower;
    }
    public Float getFirePower() {
        return FirePower;
    }

    public void calculateFP(){

        Tile tempTile = getMyTile();
        if (tempTile.getRotation() != 0) {
            FirePower = FirePower/2;
        }
    }
}
