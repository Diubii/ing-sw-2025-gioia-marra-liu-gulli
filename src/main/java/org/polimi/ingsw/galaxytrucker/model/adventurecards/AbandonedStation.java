package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.util.ArrayList;

public class AbandonedStation extends AdventureCard {
    private final ArrayList<Good> goods;
    public ArrayList<Good> getGoods() { return goods; }

    public AbandonedStation(ArrayList<Good> goods) {
        this.goods=goods;
    }

    public void activateEffect(AdventureCardActivator aca) {
        aca.activateAbandonedStation(this);
    }
}
