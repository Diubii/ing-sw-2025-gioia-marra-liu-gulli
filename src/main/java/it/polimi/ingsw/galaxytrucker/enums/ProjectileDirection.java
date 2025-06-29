package it.polimi.ingsw.galaxytrucker.enums;

/**
 * Represents the direction of a projectile relative to the ship.
 */
public enum ProjectileDirection {
    UP,
    RIGHT,
    DOWN ,
    LEFT;


    /**
     * Rotates the current direction clockwise by the given number of 90° steps.
     *
     * @param steps number of 90-degree clockwise rotations
     * @return the resulting direction after rotation
     */
    public ProjectileDirection rotate(int steps) {
        int normalized = (this.ordinal() + steps) % 4;
        return values()[normalized];
    }
    /**
     * Converts a rotation in degrees into a ProjectileDirection.
     *
     * @param rotation angle in degrees (e.g., 0, 90, 180, 270)
     * @return the corresponding direction
     */
    public static ProjectileDirection fromRotation(int rotation) {
        int steps = ((rotation % 360) + 360) % 360 / 90;
        return values()[steps];
    }
}
