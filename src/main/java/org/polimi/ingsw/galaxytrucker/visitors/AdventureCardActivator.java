package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies.*;

public interface AdventureCardActivator {
    void    activateAbandonedShip(      AbandonedShip abandonedShip, Player player, FlightBoard flightBoard);
    void activateAbandonedStation(AbandonedStation abandonedStation, Player player, FlightBoard flightBoard);
    void       activateCombatZone(            CombatZone combatZone, Player player, FlightBoard flightBoard);
    void         activateEpidemic(                Epidemic epidemic, Player player, FlightBoard flightBoard);
    void      activateMeteorSwarm(          MeteorSwarm meteorSwarm, Player player, FlightBoard flightBoard);
    void        activateOpenSpace(              OpenSpace openSpace, Player player, FlightBoard flightBoard);
    void          activatePlanets(                  Planets planets, Player player, FlightBoard flightBoard);
    void         activateStardust(                Stardust stardust, Player player, FlightBoard flightBoard);
    void          activatePirates(                  Pirates pirates, Player player, FlightBoard flightBoard);
    void          activateSlavers(                  Slavers slavers, Player player, FlightBoard flightBoard);
    void        activateSmugglers(              Smugglers smugglers, Player player, FlightBoard flightBoard);
}
