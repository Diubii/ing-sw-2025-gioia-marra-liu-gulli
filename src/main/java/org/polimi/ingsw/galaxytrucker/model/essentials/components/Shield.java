package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

import java.util.ArrayList;

public class Shield extends Component {

    private ArrayList<ProjectileDirection> protectedSides;
    private Boolean charged;

    public Shield(Boolean charged){
        super(false);
        this.charged = charged;
    }

    public Shield( ArrayList<ProjectileDirection> protectedSides, Boolean charged) {
        super(false);
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



    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

}
