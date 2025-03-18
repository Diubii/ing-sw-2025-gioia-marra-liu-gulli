package org.polimi.ingsw.galaxytrucker.model.essentials.components;

public class DoubleEngine extends Engine{

    private Boolean charged;
    public DoubleEngine(String Name, int EP, Boolean c) {

        super(Name, EP);
        this.charged = c;
    }

    public Boolean getCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }
}
