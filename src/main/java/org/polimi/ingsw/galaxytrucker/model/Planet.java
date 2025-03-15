package org.polimi.ingsw.galaxytrucker.model;

import org.polimi.ingsw.galaxytrucker.model.units.Good;

import java.util.ArrayList;

public class Planet {
    private boolean occupied;
    private final ArrayList<Good> goods;

    public boolean isOccupied() {
        return occupied;
    }
    public ArrayList<Good> getGoods() {
        return goods;
    }

    public Planet(boolean occupied, ArrayList<Good> goods) {
        this.occupied = occupied;
        this.goods = goods;
    }
}
