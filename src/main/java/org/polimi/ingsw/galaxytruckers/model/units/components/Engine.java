package org.polimi.ingsw.galaxytruckers.model.units.components;

import org.polimi.ingsw.galaxytruckers.model.units.Component;
import org.polimi.ingsw.galaxytruckers.model.units.Tile;

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
