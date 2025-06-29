package it.polimi.ingsw.galaxytrucker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileType;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a projectile in the game, such as a meteor or cannon fire.
 * Each projectile has a type, direction, and size.
 */

public class Projectile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1019019019L; // consigliato

    private final ProjectileType type;
    private final ProjectileDirection direction;
    private final ProjectileSize size;

    public ProjectileType getType() {
        return type;
    }

    public ProjectileDirection getDirection() {
        return direction;
    }

    public ProjectileSize getSize() {
        return size;
    }

    @JsonCreator
    public Projectile(
            @JsonProperty("type") ProjectileType type,
            @JsonProperty("direction") ProjectileDirection direction,
            @JsonProperty("size") ProjectileSize size) {
        this.type = type;
        this.direction = direction;
        this.size = size;
    }
}
