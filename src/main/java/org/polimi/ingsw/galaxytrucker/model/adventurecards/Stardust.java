package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.visitors.AdventureCardActivator;

public class Stardust extends AdventureCard {
    public Stardust(){}

    public void activateEffect(AdventureCardActivator aca){
        aca.activateStardust(this);
    }
}
