package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.Player;

public class AbandonedShip extends AdventureCard {
    private final int humansLost;
    private final int aliensLost;
    private final int credits;
    public int getHumansLost() {
        return humansLost;
    }
    public int getAliensLost() {
        return aliensLost;
    }
    public int getCredits() {
        return credits;
    }

    public AbandonedShip(int humansLost, int aliensLost, int credits) {
        this.humansLost = humansLost;
        this.aliensLost = aliensLost;
        this.credits = credits;
    }

    public void activateEffect(Player player) {

    }
}
