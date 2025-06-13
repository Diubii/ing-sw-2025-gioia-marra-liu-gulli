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
    public static ProjectileDirection fromRotation(int rotation) {
        int steps = ((rotation % 360) + 360) % 360 / 90;
        return values()[steps];
    }
}
