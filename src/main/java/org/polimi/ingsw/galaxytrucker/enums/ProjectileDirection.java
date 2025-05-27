package org.polimi.ingsw.galaxytrucker.enums;

public enum ProjectileDirection {
    UP,
    RIGHT,
    DOWN ,
    LEFT;


    public ProjectileDirection rotate(int steps) {
        int normalized = (this.ordinal() + steps) % 4;
        return values()[normalized];
    }
}
