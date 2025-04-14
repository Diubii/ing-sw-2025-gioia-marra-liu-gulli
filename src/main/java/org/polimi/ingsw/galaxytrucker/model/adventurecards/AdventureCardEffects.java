package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies.*;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class AdventureCardEffects implements AdventureCardActivator {
    @Override
    public void activateAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> player, FlightBoard flightBoard){
//        player.addCredits(abandonedShip.getCredits());
//        flightBoard.moveBoard(player.getNickName(), abandonedShip.getDaysLost());
    }

    @Override
    public void activateAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> player, FlightBoard flightBoard){
//        flightBoard.moveBoard(player.getNickName(), abandonedStation.getDaysLost());
    }

    @Override
    public void activateCombatZone(CombatZone combatZone, ArrayList<Player> player, FlightBoard flightBoard){

    }

    @Override
    public void activateEpidemic(Epidemic epidemic, ArrayList<Player> player, FlightBoard flightBoard){

    }

    @Override
    public void activateMeteorSwarm(MeteorSwarm meteorSwarm, ArrayList<Player> player, FlightBoard flightBoard){

    }

    @Override
    public void activateOpenSpace(OpenSpace openSpace, ArrayList<Player> player, FlightBoard flightBoard){
        //flightBoard.moveBoard(player.getNickName(), player.getShip().calcLiveEngineStrength());
    }

    @Override
    public void activatePlanets(Planets planets, ArrayList<Player> player, FlightBoard flightBoard){
    }

    @Override
    public void activateStardust(Stardust stardust, ArrayList<Player> player, FlightBoard flightBoard){
        //player.getShip().calcExposedConnectors();
    }

    @Override
    public void activatePirates(Pirates pirates, ArrayList<Player> player, FlightBoard flightBoard){

    }

    @Override
    public void activateSlavers(Slavers slavers, ArrayList<Player> player, FlightBoard flightBoard){

    }

    @Override
    public void activateSmugglers(Smugglers smugglers, ArrayList<Player> player, FlightBoard flightBoard){

    }
}