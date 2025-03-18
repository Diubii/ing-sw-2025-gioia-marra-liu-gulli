package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.visitors.AdventureCardActivator;

public class Epidemic extends AdventureCard {
    public Epidemic(){}

    public void activateEffect(AdventureCardActivator aca) {
        aca.activateEpidemic(this);
    }
}
