package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateDoubleEnginesRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.SelectPlanetRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateDoubleEnginesResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.SelectPlanetResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.FlightBoardUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.SelectedPlanetUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
        FlightBoard flightBoard = lobbyManager.getRealGame().getFlightBoard();
        for(Player player : rankedPlayers){
            CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
            ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Prendo il ClientHandler associato al player
            ActivateDoubleEnginesRequest activateDoubleEnginesRequest = new ActivateDoubleEnginesRequest();
            lobbyManager.addPendingResponse(future, activateDoubleEnginesRequest.getID()); //Notifico che sono in attesa di una risposta //IMPORTANTE: Non dovrei mettere l'id di una ActivateDoubleEnginesResponse?
            clientHandler.sendMessage(activateDoubleEnginesRequest); //Mando la richiesta di attivare eventuali motori doppi
            future.get(); //Aspetto che il player mandi la risposta

            flightBoard.movePlayer(lobbyManager.getPlayerColors().get(player.getNickName()), player.getShip().calculateEnginePower()); //Muovo il player
            lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(new FlightBoardUpdate(flightBoard))); //Invio la FlightBoard aggiornata a tutti i players
        }
    }

    @NeedsToBeChecked("Non penso sia giusto inserire un selectedPlanetUpdate nell'addPendingResponse di shipUpdates")
    @Override
    public void visitPlanets(Planets planets, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        CompletableFuture<NetworkMessage> shipUpdates = new CompletableFuture<>();
        for(Player player : rankedPlayers){
            if(!planets.getPlanets().stream().allMatch(Planet::isOccupied)){ //Se non tutti i pianeti sono occupati
                ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Ottengo il ClientHandler del player
                CompletableFuture<NetworkMessage> selectedPlanetResponseFuture = new CompletableFuture<>();

                ArrayList<Planet> notOccupiedPlanets = (ArrayList<Planet>) planets.getPlanets().stream().filter(planet -> !planet.isOccupied()).toList();
                SelectPlanetRequest selectPlanetRequest = new SelectPlanetRequest(notOccupiedPlanets);
                lobbyManager.addPendingResponse(selectedPlanetResponseFuture, selectPlanetRequest.getID()); //Notifico che sono in attesa della risposta della selezione dei pianeti
                clientHandler.sendMessage(selectPlanetRequest); //Mando la richiesta di selezione dei pianeti
                SelectPlanetResponse selectPlanetResponse = (SelectPlanetResponse) selectedPlanetResponseFuture.get(); //Aspetto che il player mandi la risposta

                Planet selectedPlanet = selectPlanetResponse.getSelectedPlanet();
                if(selectedPlanet != null){ //Se il player ha scelto
                    SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(player.getNickName(), selectedPlanet);
                    lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(selectedPlanetUpdate)); //Broadcasto un SelectedPlanetUpdate
                    lobbyManager.addPendingResponse(shipUpdates, selectedPlanetUpdate.getID());
                }
            }
            else break; //Se tutti i pianeti sono occupati usciamo dal ciclo
        }

        while(lobbyManager.getPendingResponses().stream().anyMatch(pair -> pair.getValue().equals(shipUpdates))){ //Fino a quando attendiamo uno ShipUpdate
            shipUpdates.get();
        }
    }

    @Override
    public void visitStardust(Stardust stardust, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){
        //player.getShip().calcExposedConnectors();
        FlightBoard flightBoard = lobbyManager.getRealGame().getFlightBoard();
        for(Player player : rankedPlayers.reversed()){ //Si parte dall'ultimo
            flightBoard.movePlayer(lobbyManager.getPlayerColors().get(player.getNickName()), player.getShip().getnExposedConnector());
            lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(new FlightBoardUpdate(flightBoard))); //Invio la FlightBoard aggiornata a tutti i players
        }
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