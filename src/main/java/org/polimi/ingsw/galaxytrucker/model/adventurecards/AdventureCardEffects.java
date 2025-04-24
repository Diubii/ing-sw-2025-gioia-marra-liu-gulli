package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateDoubleEnginesRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateDoubleEnginesResponse;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AdventureCardEffects implements AdventureCardVisitorsInterface {
    @Override
    public void visitAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){
//        player.addCredits(abandonedShip.getCredits());
//        flightBoard.moveBoard(player.getNickName(), abandonedShip.getDaysLost());
    }

    @Override
    public void visitAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){
//        flightBoard.moveBoard(player.getNickName(), abandonedStation.getDaysLost());
    }

    @Override
    public void visitCombatZone(CombatZone combatZone, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){

    }

    @Override
    public void visitEpidemic(Epidemic epidemic, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){

    }

    @Override
    public void visitMeteorSwarm(MeteorSwarm meteorSwarm, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){

    }

    @Override
    public void visitOpenSpace(OpenSpace openSpace, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        for(Player player : rankedPlayers){
            ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Prendo il ClientHandler associato al player
            ActivateDoubleEnginesRequest activateDoubleEnginesRequest = new ActivateDoubleEnginesRequest();
            clientHandler.sendMessage(activateDoubleEnginesRequest); //Mando la richiesta di attivare eventuali motori doppi
            lobbyManager.addPendingResponse(future, activateDoubleEnginesRequest.getID()); //Notifico che sono in attesa di una risposta //IMPORTANTE: Non dovrei mettere l'id di una ActivateDoubleEnginesResponse?
            future.get(); //Aspetto che il player mandi la risposta

            lobbyManager.getRealGame().getFlightBoard().movePlayer(lobbyManager.getPlayerColors().get(player.getNickName()), player.getShip().calculateEnginePower()); //Muovo il player
        }
    }

    @Override
    public void visitPlanets(Planets planets, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){
    }

    @Override
    public void visitStardust(Stardust stardust, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){
        //player.getShip().calcExposedConnectors();
    }

    @Override
    public void visitPirates(Pirates pirates, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){

    }

    @Override
    public void visitSlavers(Slavers slavers, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){

    }

    @Override
    public void visitSmugglers(Smugglers smugglers, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){

    }
}