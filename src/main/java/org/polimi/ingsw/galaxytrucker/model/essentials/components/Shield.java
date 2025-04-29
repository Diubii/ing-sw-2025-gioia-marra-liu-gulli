package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentVisitorInterface;

import java.util.ArrayList;

public class Shield extends Component {

    private ArrayList<ProjectileDirection> protectedSides;
    private Boolean charged;

    public Shield(Boolean charged){
        super(false);
        this.charged = charged;
        protectedSides = new ArrayList<>();
    }

    @JsonCreator
    public Shield(@JsonProperty("protectedSides") ArrayList<ProjectileDirection> protectedSides,@JsonProperty("charged") Boolean charged) {
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
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
