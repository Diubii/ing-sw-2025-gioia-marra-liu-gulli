package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.SelectPlanetRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.SelectPlanetResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.FlightBoardUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.SelectedPlanetUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AdventureCardEffects implements AdventureCardVisitorsInterface {
    @Override
    public void visitAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
        DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(abandonedShip.getRequiredCrewMembers()); //Il client deve scartare un tot di equipaggio
        for (Player player : rankedPlayers) {
            if (player.getShip().getnCrew() < abandonedShip.getRequiredCrewMembers()) {
                GameMessage gameMessage = new GameMessage("Non hai abbastanza membri dell'equipaggio per attivare questa carta.");
                lobbyManager.getPlayerHandlers().get(player.getNickName()).sendMessage(gameMessage);
                continue; //Se il player non ha abbastanza equipaggio passo al prossimo
            }

            //Chiedo al player se vuole attivare la carta
            ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) sendMessageAndGetResponse(lobbyManager, player, activateAdventureCardRequest);
            if (activateAdventureCardResponse.isActivated()) {
                DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) sendMessageAndGetResponse(lobbyManager, player, discardCrewMembersRequest); //Aspetto la risposta

                discardCrewMembers(player, discardCrewMembersResponse);

                //Broadcasto nuova nave
                ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
                lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(shipUpdate));

                player.addCredits(abandonedShip.getCredits()); //Accredito crediti

                movePlayer(lobbyManager, player, -abandonedShip.getDaysLost()); //Sposto il player
                break; //Solo un player può attivare la carta
            }
        }
    }

    @NeedsToBeCompleted
    @Override
    public void visitAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
        for (Player player : rankedPlayers) {
            if (player.getShip().getnCrew() < abandonedStation.getRequiredCrewMembers())
                continue; //Se il player non ha abbastanza equipaggio passo al prossimo

            //Chiedo al player se vuole attivare la carta
            ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) sendMessageAndGetResponse(lobbyManager, player, activateAdventureCardRequest);

            if (activateAdventureCardResponse.isActivated()) {

            }
        }
    }

    @Override
    public void visitCombatZone(CombatZone combatZone, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        GameMessage gameMessage = new GameMessage();

        //Parte 1: minor equipaggio = demozione
        int minCrewMembers = 0;
        Player minCrewMembersPlayer = null;
        for (Player player : rankedPlayers) {
            int playerCrewMembersNumber = player.getShip().getnCrew();
            if (playerCrewMembersNumber < minCrewMembers || minCrewMembers == 0) {
                minCrewMembers = player.getShip().getnCrew();
                minCrewMembersPlayer = player;
            } else if (playerCrewMembersNumber == minCrewMembers) { //Se c'è parità
                if (player.getPlacement() > minCrewMembersPlayer.getPlacement()) { //Il giocatore in vantaggio diventa il nuovo target
                    minCrewMembersPlayer = player;
                }
            }
        }

        assert minCrewMembersPlayer != null;
        gameMessage.setMessage(minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");
        broadcast(lobbyManager, gameMessage);

        movePlayer(lobbyManager, minCrewMembersPlayer, -combatZone.getDaysLost());

        //Parte 2: minor potenza motrice = rimozione equipaggio
        int minEnginePower = 0;
        Player minEnginePowerPlayer = null;
        for (Player player : rankedPlayers) {
            //Richiedo l'attivazione dei motori doppi, se esistono
            if (!player.getShip().getComponentPositionsFromName("DoubleEngine").isEmpty()) {
                ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
                sendMessageAndGetResponse(lobbyManager, player, activateDoubleEnginesRequest);
            }

            int playerEnginePower = player.getShip().calculateEnginePower();
            if (playerEnginePower < minEnginePower || minEnginePower == 0) {
                minEnginePower = playerEnginePower;
                minEnginePowerPlayer = player;
            } else if (playerEnginePower == minEnginePower) { //Se c'è parità
                if (player.getPlacement() > minEnginePowerPlayer.getPlacement()) { //Il giocatore in vantaggio diventa il nuovo target
                    minEnginePowerPlayer = player;
                }
            }
        }

        assert minEnginePowerPlayer != null;
        gameMessage.setMessage(minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi!");
        broadcast(lobbyManager, gameMessage);

        //Richiedo al player le posizioni delle housing unit da cui rimuovere equipaggio
        DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(combatZone.getCrewMembersLost());
        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) sendMessageAndGetResponse(lobbyManager, minEnginePowerPlayer, discardCrewMembersRequest);

        discardCrewMembers(minCrewMembersPlayer, discardCrewMembersResponse);

        //Parte 3: minor potenza di fuoco = cannonate
        float minFirePower = 0;
        Player minFirePowerPlayer = null;
        for (Player player : rankedPlayers) {
            //Chiedo l'attivazione di cannoni doppi, se esistono
            if (!player.getShip().getComponentPositionsFromName("DoubleCannon").isEmpty()) {
                ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
                sendMessageAndGetResponse(lobbyManager, player, activateDoubleEnginesRequest);
            }

            float playerFirePower = player.getShip().calculateFirePower();
            if (playerFirePower < minFirePower || minFirePower == 0) {
                minFirePower = playerFirePower;
                minFirePowerPlayer = player;
            } else if (playerFirePower == minFirePower) { //Se c'è parità
                if (player.getPlacement() > minFirePowerPlayer.getPlacement()) { //Il giocatore in vantaggio diventa il nuovo target
                    minFirePowerPlayer = player;
                }
            }
        }

        assert minFirePowerPlayer != null;
        gameMessage.setMessage(minFirePowerPlayer.getNickName() + " ha la minor potenza di fuoco, quindi subisce delle cannonate!");
        broadcast(lobbyManager, gameMessage);

        Random rand = new Random();
        final Player targetPlayer = minFirePowerPlayer;
        for (Projectile projectile : combatZone.getProjectiles()) {
            if (projectile.getSize() == ProjectileSize.LITTLE && !targetPlayer.getShip().getComponentPositionsFromName("Shield").isEmpty()) { //Se il proiettile è piccolo si possono attivare gli scudi, se esistono
                sendMessageAndGetResponse(lobbyManager, targetPlayer, new ActivateComponentRequest(ActivatableComponent.Shield));
            } else if (projectile.getSize() == ProjectileSize.BIG && projectile.getType() == ProjectileType.Meteor) {
                //Se nessun cannone punta verso il meteorite chiedo l'attivazione di un CannoneDoppio, se esiste
                if (targetPlayer.getShip().getComponentPositionsFromName("Cannon").stream().noneMatch(p -> targetPlayer.getShip().getComponentFromPosition(p).getRotation() == projectile.getDirection().ordinal())
                        && !targetPlayer.getShip().getComponentPositionsFromName("DoubleCannon").isEmpty()) {
                    sendMessageAndGetResponse(lobbyManager, minFirePowerPlayer, new ActivateComponentRequest(ActivatableComponent.DoubleCannon));
                }
            }

            int diceRoll = rand.nextInt(2, 13);
            gameMessage.setMessage("Stai per ricevere delle cannonate da " + projectile.getDirection().name() + ", indice " + diceRoll + "!");
            lobbyManager.getPlayerHandlers().get(minFirePowerPlayer.getNickName()).sendMessage(gameMessage);

            lobbyManager.getGameController().reactToProjectile(minFirePowerPlayer, projectile, diceRoll);
            ShipUpdate shipUpdate = new ShipUpdate(minFirePowerPlayer.getShip(), minFirePowerPlayer.getNickName());
            broadcast(lobbyManager, shipUpdate);
        }
    }

    @NeedsToBeCompleted
    @Override
    public void visitEpidemic(Epidemic epidemic, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {
        for (Player player : rankedPlayers) {

        }
    }

    @Override
    public void visitMeteorSwarm(MeteorSwarm meteorSwarm, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {

    }

    @NeedsToBeCompleted("Disattivare motori doppi")
    @Override
    public void visitOpenSpace(OpenSpace openSpace, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException, PlayerNotFoundException {
        for (Player player : rankedPlayers) {
            ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
            sendMessageAndGetResponse(lobbyManager, player, activateDoubleEnginesRequest);
            int playerEnginePower = player.getShip().calculateEnginePower();
            if(playerEnginePower == 0){
                lobbyManager.getGameController().removePlayerFromGame(player.getNickName());
                continue;
            }
            movePlayer(lobbyManager, player, playerEnginePower);
        }
    }

    @NeedsToBeChecked("Non è giusto inserire un selectedPlanetUpdate nell'addPendingResponse di shipUpdates")
    @Override
    public void visitPlanets(Planets planets, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        ArrayList<CompletableFuture<NetworkMessage>> shipUpdates = new ArrayList<>();
        ArrayList<Player> landedPlayers = new ArrayList<>();

        for (Player player : rankedPlayers) {
            if (!planets.getPlanets().stream().allMatch(Planet::isOccupied)) { //Se non tutti i pianeti sono occupati
                ArrayList<Planet> notOccupiedPlanets = new ArrayList<>(planets.getPlanets().stream().filter(planet -> !planet.isOccupied()).toList());
                SelectPlanetRequest selectPlanetRequest = new SelectPlanetRequest(notOccupiedPlanets);
                SelectPlanetResponse selectPlanetResponse = (SelectPlanetResponse) sendMessageAndGetResponse(lobbyManager, player, selectPlanetRequest); //Aspetto che il player mandi la risposta

                //TEST
                //SelectPlanetResponse selectPlanetResponse = new SelectPlanetResponse(new Planet(false, null));
                //selectedPlanetResponseFuture.complete(selectPlanetResponse);
                //END TEST

                Planet selectedPlanet = selectPlanetResponse.getSelectedPlanet();
                if (selectedPlanet != null) { //Se il player ha scelto
                    selectedPlanet.setOccupied(true);
                    SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(player.getNickName(), selectedPlanet);
                    broadcast(lobbyManager, selectedPlanetUpdate); //Broadcasto un SelectedPlanetUpdate
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
            } else break; //Se tutti i pianeti sono occupati usciamo dal ciclo
        }

        //Aspetto che arrivino tutti gli ShipUpdate del caso
        for (CompletableFuture<NetworkMessage> future : shipUpdates) {
            future.get();
            //System.out.println("Ottenuto ship update");
        }

        for (Player player : landedPlayers.reversed()) { //I landed players sono in ordine di rotta
            movePlayer(lobbyManager, player, -planets.getDaysLost());
        }
    }

    @Override
    public void visitStardust(Stardust stardust, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {
        //player.getShip().calcExposedConnectors();
        for (Player player : rankedPlayers.reversed()) { //Si parte dall'ultimo
            movePlayer(lobbyManager, player, -player.getShip().getnExposedConnector());
        }
    }

    @Override
    public void visitPirates(Pirates pirates, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {

    }

    @Override
    public void visitSlavers(Slavers slavers, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {

    }

    @Override
    public void visitSmugglers(Smugglers smugglers, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {

    }

    /**
     * Moves a player in the game's flight board and sends an update to all clients.
     *
     * @param lobbyManager
     * @param player
     * @param steps
     */
    private void movePlayer(LobbyManager lobbyManager, Player player, int steps) {
        FlightBoard flightBoard = lobbyManager.getRealGame().getFlightBoard();
        flightBoard.movePlayer(lobbyManager.getPlayerColors().get(player.getNickName()), steps);
        FlightBoardUpdate fbu = new FlightBoardUpdate(flightBoard);
        String message = "Player " + player.getNickName() + "moved " + steps + " steps!";
        broadcast(lobbyManager, new GameMessage(message));
        broadcast(lobbyManager, fbu);
    }

    private void discardCrewMembers(Player player, DiscardCrewMembersResponse discardCrewMembersResponse) {
        ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();
        for (Position position : discardCrewMembersResponse.getHousingPositions()) { //Per ogni posizione (assumo posizioni duplicate per scartare più volte dalla stessa housing unit)
            Component housingUnit = player.getShip().getComponentFromPosition(position); //Prendo la housingUnit dalla position data
            String componentName = componentNameVisitor.visit(housingUnit); //Visitor

            if (componentName.equals("CentralHousingUnit")) {
                ((CentralHousingUnit) housingUnit).removeHumanCrewMember();
            } else { //Altrimenti è una ModularHousingUnit
                ModularHousingUnit modularHousingUnit = (ModularHousingUnit) housingUnit;

                if (modularHousingUnit.getHumanCrewNumber() > 0) { //Ci sono solo umani
                    modularHousingUnit.removeHumanCrewMember();
                } else { //Ci sono solo alieni
                    modularHousingUnit.removeAlienCrew();
                }
            }
        }
    }

    private NetworkMessage sendMessageAndGetResponse(LobbyManager lobbyManager, Player player, NetworkMessage message) throws ExecutionException, InterruptedException {
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Prendo il ClientHandler associato al player
        lobbyManager.addPendingResponse(future, message.getID()); //Notifico che sono in attesa di una risposta
        clientHandler.sendMessage(message); //Mando la richiesta di attivare eventuali motori doppi
        return future.get(); //Aspetto che il player mandi la risposta
    }

    private void broadcast(LobbyManager lobbyManager, NetworkMessage message) {
        lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(message));
    }
}