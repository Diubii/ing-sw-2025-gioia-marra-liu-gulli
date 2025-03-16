package org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts;

import org.polimi.ingsw.galaxytrucker.model.units.Player;

public abstract class AdventureCard {
    protected int id;
    protected int level;
    protected int daysLost;

    protected String name;

    protected boolean learningFlight;
    protected boolean affectsAll;

    protected abstract void activateEffect(Player player);

    public int getId() {
        return id;
    }
    public int getLevel() {
        return level;
    }
    public int getDaysLost() {
        return daysLost;
    }
    public String getName() {
        return name;
    }
    public boolean isLearningFlight() {
        return learningFlight;
    }
    public boolean isAffectsAll() {
        return affectsAll;
    }
}
