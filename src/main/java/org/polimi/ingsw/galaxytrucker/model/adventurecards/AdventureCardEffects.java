package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies.*;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

public class AdventureCardEffects implements AdventureCardActivator {
    @Override
    public void activateAbandonedShip(AbandonedShip abandonedShip, Player player, FlightBoard flightBoard){
        player.addCredits(abandonedShip.getCredits());
        flightBoard.moveBoard(player, abandonedShip.getDaysLost());
    }

    @Override
    public void activateAbandonedStation(AbandonedStation abandonedStation, Player player, FlightBoard flightBoard){
        flightBoard.moveBoard(player, abandonedStation.getDaysLost());
    }

    @Override
    public void activateCombatZone(CombatZone combatZone, Player player, FlightBoard flightBoard){

    }

    @Override
    public void activateEpidemic(Epidemic epidemic, Player player, FlightBoard flightBoard){

    }

    @Override
    public void activateMeteorSwarm(MeteorSwarm meteorSwarm, Player player, FlightBoard flightBoard){

    }

    @Override
    public void activateOpenSpace(OpenSpace openSpace, Player player, FlightBoard flightBoard){
        //flightBoard.moveBoard(player.getNickName(), player.getShip().calcLiveEngineStrength());
    }

    @Override
    public void activatePlanets(Planets planets, Player player, FlightBoard flightBoard){
    }

    @Override
    public void activateStardust(Stardust stardust, Player player, FlightBoard flightBoard){
        //player.getShip().calcExposedConnectors();
    }

    @Override
    public void activatePirates(Pirates pirates, Player player, FlightBoard flightBoard){

    }

    @Override
    public void activateSlavers(Slavers slavers, Player player, FlightBoard flightBoard){

    }

    @Override
    public void activateSmugglers(Smugglers smugglers, Player player, FlightBoard flightBoard){

    }
}