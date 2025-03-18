package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.visitors.AdventureCardActivator;

public class OpenSpace extends AdventureCard {
    public OpenSpace() {}

    public void activateEffect(AdventureCardActivator aca) {
        aca.activateOpenSpace(this);
    }
}
