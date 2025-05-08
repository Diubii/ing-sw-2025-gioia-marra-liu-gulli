package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.enums.CardPhase;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public interface AdventureCardVisitorsInterface<T> {
    T visit(AbandonedShip abandonedShip);

    T visit(AbandonedStation abandonedStation);

    T visit(CombatZone combatZone);

    T visit(Epidemic epidemic);

    T visit(MeteorSwarm meteorSwarm);

    T visit(OpenSpace openSpace);

    T visit(Planets planets);

    T visit(Stardust stardust);

    T visit(Pirates pirates);

    T visit(Slavers slavers);

    T visit(Smugglers smugglers);
}
