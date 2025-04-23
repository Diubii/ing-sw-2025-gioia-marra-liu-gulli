package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
//import org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies.*;

import java.util.ArrayList;

public interface AdventureCardActivator {
    void activateAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);


    void activateAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activateCombatZone(CombatZone combatZone, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activateEpidemic(Epidemic epidemic, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activateMeteorSwarm(MeteorSwarm meteorSwarm, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activateOpenSpace(OpenSpace openSpace, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activatePlanets(Planets planets, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activateStardust(Stardust stardust, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activatePirates(Pirates pirates, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activateSlavers(Slavers slavers, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);

    void activateSmugglers(Smugglers smugglers, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController);
}
