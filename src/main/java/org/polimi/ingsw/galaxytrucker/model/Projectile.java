package org.polimi.ingsw.galaxytrucker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileType;

public class Projectile {
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
