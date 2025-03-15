package org.polimi.ingsw.galaxytruckers.model.adventurecards;

import org.polimi.ingsw.galaxytruckers.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytruckers.model.units.Good;
import org.polimi.ingsw.galaxytruckers.model.units.Player;

import java.util.ArrayList;

public class AbandonedStation extends AdventureCard {
    private final ArrayList<Good> goods;
    public ArrayList<Good> getGoods() { return goods; }

    public AbandonedStation(ArrayList<Good> goods) {
        this.goods=goods;
    }

    public void activateEffect(Player player){

    }
}
