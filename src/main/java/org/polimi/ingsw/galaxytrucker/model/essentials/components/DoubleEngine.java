package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;

public class DoubleEngine extends Engine{

    private Boolean charged;

    @JsonCreator
    public DoubleEngine(@JsonProperty("charged") Boolean charged, @JsonProperty("enginePower") int enginePower) {

        super(enginePower);
        this.charged = charged;
    }

    public Boolean isCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

    @Override
    public int getEnginePower(){
        if(charged){
            if(getRotation() == 0) return 2;
            else return 0;
        }
        else{
            return 0;
        }
    }

    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

    @Override
    public String[] accept(ComponentPrintVisitorInterface visitor) {
        return visitor.visit(this);
    }

}