package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;

public class Engine extends Component {
    protected int enginePower;

    public Engine(String Name, int enginePower) {

        super(Name);
        this.enginePower = enginePower;
    }
    public int getEnginePower() {
        return enginePower;
    }

    public void calculatePower() {
        Tile tempTile = getMyTile();
        if (tempTile.getRotation() != 0) {
            enginePower = 0;
        }
    }


}
