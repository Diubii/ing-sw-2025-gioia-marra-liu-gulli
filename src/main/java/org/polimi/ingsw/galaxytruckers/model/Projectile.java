package org.polimi.ingsw.galaxytruckers.model;

import org.polimi.ingsw.galaxytruckers.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytruckers.enums.ProjectileSize;
import org.polimi.ingsw.galaxytruckers.enums.ProjectileType;

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

    public Projectile(ProjectileType type, ProjectileDirection direction, ProjectileSize size) {
        this.type = type;
        this.direction = direction;
        this.size = size;
    }
}
