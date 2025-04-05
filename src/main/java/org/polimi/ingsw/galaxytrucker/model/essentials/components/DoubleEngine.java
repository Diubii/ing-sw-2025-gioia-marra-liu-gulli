package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

public class DoubleEngine extends Engine{

    private Boolean charged;
    public DoubleEngine(int EP, Boolean c) {

        super(EP);
        this.charged = c;
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
