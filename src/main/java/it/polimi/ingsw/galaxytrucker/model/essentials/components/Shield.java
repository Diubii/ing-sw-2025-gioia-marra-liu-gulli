package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

import java.util.ArrayList;

/**
 * Represents a Shield component that can block incoming projectiles from specific directions
 * when charged.
 */
public class Shield extends Component {

    private ArrayList<ProjectileDirection> protectedSides;
    private Boolean charged;

    public Shield(Boolean charged) {
        super(false);
        this.charged = charged;
        protectedSides = new ArrayList<>();
    }

    @JsonCreator
    public Shield(@JsonProperty("protectedSides") ArrayList<ProjectileDirection> protectedSides,
                  @JsonProperty("charged") Boolean charged) {
        super(false);
        this.charged = charged;
        if (protectedSides == null || protectedSides.isEmpty()) {
            this.protectedSides = new ArrayList<>();
            this.protectedSides.add(ProjectileDirection.UP);
            this.protectedSides.add(ProjectileDirection.RIGHT);
        } else {
            this.protectedSides = new ArrayList<>(protectedSides);
        }
    }

    /**
     * Gets the list of directions this shield can currently protect, based on its rotation.
     *
     * @return rotated list of {@link ProjectileDirection}
     */
    public ArrayList<ProjectileDirection> getProtectedSides() {

            int steps = ((rotation % 360) + 360) % 360 / 90;
            ArrayList<ProjectileDirection> rotatedSides = new ArrayList<>();
            for (ProjectileDirection dir : protectedSides) {
                rotatedSides.add(dir.rotate(steps));
            }
            return rotatedSides;

    }

    @Override
    public Boolean isCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

    @Override
    public Shield clone() {
        Shield copy = (Shield) super.clone();
        copy.protectedSides = new ArrayList<>(this.protectedSides);
        copy.charged = this.charged;
        return copy;
    }


    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
