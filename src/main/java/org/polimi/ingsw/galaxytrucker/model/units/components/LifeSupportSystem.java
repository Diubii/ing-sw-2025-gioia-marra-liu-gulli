package org.polimi.ingsw.galaxytrucker.model.units.components;

import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.units.Component;
import org.polimi.ingsw.galaxytrucker.model.units.Position;
import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.model.units.Tile;

import java.util.ArrayList;

public class LifeSupportSystem extends Component {

    private Color color;
    private ArrayList<Pair<Position, AlienColor>> nearMHU;

    public LifeSupportSystem(String name, Color color) {
        super(name);
        this.color = color;
        this.nearMHU = new ArrayList<>();
    }

    public Color getColor() {
        return color;
    }

    public void setNearMHU() {
        Tile tempTile = getMyTile();

    }
}
