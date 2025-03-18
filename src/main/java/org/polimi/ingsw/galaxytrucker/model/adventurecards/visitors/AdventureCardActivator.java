package org.polimi.ingsw.galaxytrucker.model.adventurecards.visitors;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies.*;

public interface AdventureCardActivator {
    void activateAbandonedShip(AbandonedShip as);
    void activateAbandonedStation(AbandonedStation as);
    void activateCombatZone(CombatZone cz);
    void activateEpidemic(Epidemic e);
    void activateMeteorSwarm(MeteorSwarm ms);
    void activateOpenSpace(OpenSpace os);
    void activatePlanets(Planets p);
    void activateStardust(Stardust s);
    void activatePirates(Pirates p);
    void activateSlavers(Slavers s);
    void activateSmugglers(Smugglers s);
}
