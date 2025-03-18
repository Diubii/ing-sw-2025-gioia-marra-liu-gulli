package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.visitors.AdventureCardActivator;

public class CombatZone extends AdventureCard {
    public CombatZone() {}

    public void activateEffect(AdventureCardActivator aca) {
        aca.activateCombatZone(this);
    }
}
