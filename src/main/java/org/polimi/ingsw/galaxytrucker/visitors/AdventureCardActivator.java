package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
//import org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies.*;

import java.util.ArrayList;

public interface AdventureCardActivator {
    void activateAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> player, FlightBoard flightBoard);

    void activateAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> player, FlightBoard flightBoard);

    void activateCombatZone(CombatZone combatZone, ArrayList<Player> player, FlightBoard flightBoard);

    void activateEpidemic(Epidemic epidemic, ArrayList<Player> player, FlightBoard flightBoard);

    void activateMeteorSwarm(MeteorSwarm meteorSwarm, ArrayList<Player> player, FlightBoard flightBoard);

    void activateOpenSpace(OpenSpace openSpace, ArrayList<Player> player, FlightBoard flightBoard);

    void activatePlanets(Planets planets, ArrayList<Player> player, FlightBoard flightBoard);

    void activateStardust(Stardust stardust, ArrayList<Player> player, FlightBoard flightBoard);

    void activatePirates(Pirates pirates, ArrayList<Player> player, FlightBoard flightBoard);

    void activateSlavers(Slavers slavers, ArrayList<Player> player, FlightBoard flightBoard);

    void activateSmugglers(Smugglers smugglers, ArrayList<Player> player, FlightBoard flightBoard);
}
