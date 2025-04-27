package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateDoubleEnginesRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.SelectPlanetRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.SelectPlanetResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.FlightBoardUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.SelectedPlanetUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AdventureCardEffects implements AdventureCardVisitorsInterface {
    @Override
    public void visitAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        for(Player player : rankedPlayers){

            //Chiedo al player se vuole attivare la carta
            CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
            ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
            lobbyManager.addPendingResponse(future, activateAdventureCardRequest.getID());
            lobbyManager.getPlayerHandlers().get(player.getNickName()).sendMessage(activateAdventureCardRequest);
            ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) future.get();

            if(activateAdventureCardResponse.isActivated()){
                DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(abandonedShip.getCrewMembersLost()); //Il client deve scartare un tot di equipaggio
                lobbyManager.addPendingResponse(future, discardCrewMembersRequest.getID());
                lobbyManager.getPlayerHandlers().get(player.getNickName()).sendMessage(discardCrewMembersRequest); //Mando al player la richiesta
                DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) future.get(); //Aspetto la risposta

                ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();
                for(Position position : discardCrewMembersResponse.getHousingPositions()){ //Per ogni posizione (assumo posizioni duplicate per scartare più volte dalla stessa housing unit)
                    Component housingUnit = player.getShip().getComponentFromPosition(position); //Prendo la housingUnit dalla position data
                    String componentName = componentNameVisitor.visit(housingUnit); //Visitor

                    if(componentName.equals("CentralHousingUnit")){
                        ((CentralHousingUnit) housingUnit).removeHumanCrewMember();
                    }
                    else{ //Altrimenti è una ModularHousingUnit
                        ModularHousingUnit modularHousingUnit = (ModularHousingUnit) housingUnit;

                        if(modularHousingUnit.getHumanCrewNumber() > 0){ //Ci sono solo umani
                            modularHousingUnit.removeHumanCrewMember();
                        }
                        else{ //Ci sono solo alieni
                            modularHousingUnit.removeAlienCrew();
                        }
                    }
                }

                //Broadcasto nuova nave
                ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
                lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(shipUpdate));

                player.addCredits(abandonedShip.getCredits()); //Accredito crediti

                movePlayer(lobbyManager, player, -abandonedShip.getDaysLost()); //Sposto il player
                break; //Solo un player può attivare la carta
            }
        }
    }

    @Override
    public void visitAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){

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
        for(Player player : rankedPlayers){
            CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
            ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Prendo il ClientHandler associato al player
            ActivateDoubleEnginesRequest activateDoubleEnginesRequest = new ActivateDoubleEnginesRequest();
            lobbyManager.addPendingResponse(future, activateDoubleEnginesRequest.getID()); //Notifico che sono in attesa di una risposta //IMPORTANTE: Non dovrei mettere l'id di una ActivateDoubleEnginesResponse?
            clientHandler.sendMessage(activateDoubleEnginesRequest); //Mando la richiesta di attivare eventuali motori doppi
            future.get(); //Aspetto che il player mandi la risposta

            movePlayer(lobbyManager, player, player.getShip().calculateEnginePower());
        }
    }

    @NeedsToBeChecked("Non penso sia giusto inserire un selectedPlanetUpdate nell'addPendingResponse di shipUpdates")
    @Override
    public void visitPlanets(Planets planets, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        ArrayList<CompletableFuture<NetworkMessage>> shipUpdates = new ArrayList<>();
        ArrayList<Player> landedPlayers = new ArrayList<>();

        for(Player player : rankedPlayers){
            if(!planets.getPlanets().stream().allMatch(Planet::isOccupied)){ //Se non tutti i pianeti sono occupati
                ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Ottengo il ClientHandler del player
                CompletableFuture<NetworkMessage> selectedPlanetResponseFuture = new CompletableFuture<>();

                ArrayList<Planet> notOccupiedPlanets = new ArrayList<>(planets.getPlanets().stream().filter(planet -> !planet.isOccupied()).toList());
                SelectPlanetRequest selectPlanetRequest = new SelectPlanetRequest(notOccupiedPlanets);
                lobbyManager.addPendingResponse(selectedPlanetResponseFuture, selectPlanetRequest.getID()); //Notifico che sono in attesa della risposta della selezione dei pianeti
                clientHandler.sendMessage(selectPlanetRequest); //Mando la richiesta di selezione dei pianeti
                SelectPlanetResponse selectPlanetResponse = (SelectPlanetResponse) selectedPlanetResponseFuture.get(); //Aspetto che il player mandi la risposta

                //TEST
                //SelectPlanetResponse selectPlanetResponse = new SelectPlanetResponse(new Planet(false, null));
                //selectedPlanetResponseFuture.complete(selectPlanetResponse);
                //END TEST

                Planet selectedPlanet = selectPlanetResponse.getSelectedPlanet();
                if(selectedPlanet != null){ //Se il player ha scelto
                    selectedPlanet.setOccupied(true);
                    SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(player.getNickName(), selectedPlanet);
                    lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(selectedPlanetUpdate)); //Broadcasto un SelectedPlanetUpdate
                    CompletableFuture<NetworkMessage> shipUpdateFuture = new CompletableFuture<>();
                    lobbyManager.addPendingResponse(shipUpdateFuture, selectedPlanetUpdate.getID()); //Mi deve arrivare uno ShipUpdate
                    shipUpdates.add(shipUpdateFuture);
                    landedPlayers.add(player);

                    //TEST
                    //System.out.println("Simulando delay scelta player");
                    //Thread.sleep(2000);
                    //shipUpdateFuture.complete(new ShipUpdate(null, null));
                    //END TEST
                }
            }
            else break; //Se tutti i pianeti sono occupati usciamo dal ciclo
        }

        //Aspetto che arrivino tutti gli ShipUpdate del caso
        for(CompletableFuture<NetworkMessage> future : shipUpdates){
            future.get();
            //System.out.println("Ottenuto ship update");
        }

        for(Player player : landedPlayers.reversed()){ //I landed players sono in ordine di rotta
            movePlayer(lobbyManager, player, -planets.getDaysLost());
        }
    }

    @Override
    public void visitStardust(Stardust stardust, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){
        //player.getShip().calcExposedConnectors();
        for(Player player : rankedPlayers.reversed()){ //Si parte dall'ultimo
            movePlayer(lobbyManager, player, -player.getShip().getnExposedConnector());
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

    /**
     * Moves a player in the game's flight board and sends an update to all clients.
     * @param lobbyManager
     * @param player
     * @param steps
     */
    private void movePlayer(LobbyManager lobbyManager, Player player, int steps){
        FlightBoard flightBoard = lobbyManager.getRealGame().getFlightBoard();
        flightBoard.movePlayer(lobbyManager.getPlayerColors().get(player.getNickName()), steps);
        lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(new FlightBoardUpdate(flightBoard)));
    }
}