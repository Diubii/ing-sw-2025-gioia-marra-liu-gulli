package org.polimi.ingsw.galaxytrucker.model.units.components;

import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.model.units.Component;
import org.polimi.ingsw.galaxytrucker.model.units.Tile;

import java.util.ArrayList;

public class Shield extends Component {

    private ArrayList<ProjectileDirection> protectedSides;
    private Boolean charged;

    public Shield(String name, ArrayList<ProjectileDirection> protectedSides, Boolean charged) {
        super(name);
        this.protectedSides = new ArrayList<>(protectedSides);
        this.charged = charged;
    }

    public ArrayList<ProjectileDirection> getProtectedSides() {
        return new ArrayList<>(protectedSides);
    }

    public Boolean getCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

    public void calculateProtectedSide(){
        Tile tempTile = getMyTile();
        ProjectileDirection newValue;

                if (tempTile.getRotation() != 0) {

                    for (int i = 0; i < tempTile.getRotation()/90; i++) {}
                    protectedSides.set(0, protectedSides.get(1));
                    switch (protectedSides.get(1)) {
                        case LEFT:
                            newValue = ProjectileDirection.BOTTOM;
                        case RIGHT:
                            newValue = ProjectileDirection.FRONT;

                        case BOTTOM:
                            newValue = ProjectileDirection.RIGHT;

                        case FRONT:
                            newValue = ProjectileDirection.LEFT;

                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + protectedSides.get(1));
                    }

                    protectedSides.set(1, newValue);


                }

    }
}
