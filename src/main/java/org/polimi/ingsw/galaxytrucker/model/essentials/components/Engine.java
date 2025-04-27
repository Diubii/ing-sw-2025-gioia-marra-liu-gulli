package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;

public class Engine extends Component {
    protected int enginePower;

    @NeedsToBeChecked("Non ha senso passare una potenza al costruttore se sono dettate dal regolamento.")
    @JsonCreator
    public Engine( @JsonProperty("enginePower") int enginePower) {
        super(false);
        this.enginePower = getRotation()==0 ? 1 : 0;
    }
    public int getEnginePower() {
        return enginePower;
    }

    public void calculatePower() {
        if (getRotation() != 0) {
            enginePower = 0;
        }
    }


    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }


    public String[] accept(ComponentPrintVisitorInterface visitor) {
        return visitor.visit(this);
    }


}