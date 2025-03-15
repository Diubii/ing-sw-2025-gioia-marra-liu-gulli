package org.polimi.ingsw.galaxytruckers.model;

import org.polimi.ingsw.galaxytruckers.model.units.Good;

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
