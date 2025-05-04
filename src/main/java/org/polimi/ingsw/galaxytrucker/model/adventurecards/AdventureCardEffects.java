package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
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

    @NeedsToBeChecked("Sempre il problema di riuscire ad identificare lo ship update")
    @Override
    public void visitAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
        for (Player player : rankedPlayers) {
            if (player.getShip().getnCrew() < abandonedStation.getRequiredCrewMembers())
                continue; //Se il player non ha abbastanza equipaggio passo al prossimo

            //Chiedo al player se vuole attivare la carta
            ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) sendMessageAndGetResponse(lobbyManager, player, activateAdventureCardRequest);

            if (activateAdventureCardResponse.isActivated()) {
                ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName()); //TODO: Trovare un metodo per identificare lo ship update
                CompletableFuture<NetworkMessage> shipUpdateFuture = new CompletableFuture<>();
                lobbyManager.addPendingResponse(shipUpdateFuture, shipUpdate.getID()); //Mi deve arrivare uno ShipUpdate
                shipUpdateFuture.get(); //Aspetto che arrivi lo ship update
                movePlayer(lobbyManager, player, -abandonedStation.getDaysLost());
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
        int diceRoll;
        String message;
        for (Projectile projectile : combatZone.getProjectiles()) {
            diceRoll = rand.nextInt(2, 13);

            message = "Stai per essere colpito da un " + projectile.getType().name() + " da " + projectile.getDirection().name() + ", indice " + diceRoll + "!";
            sendGameMessage(lobbyManager, minFirePowerPlayer, message);

            if (targetPlayer.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null) { //Se il proiettile va a colpire un componente, vediamo se il player può proteggersi
                if (projectile.getSize() == ProjectileSize.LITTLE && playerCanDefendThemselvesWithAShield(targetPlayer, projectile)) { //Se il proiettile è piccolo si possono attivare gli scudi, se ne esistono orientati correttamente
                    message = "Però puoi proteggerti con uno scudo!";
                    sendGameMessage(lobbyManager, minFirePowerPlayer, message);
                    sendMessageAndGetResponse(lobbyManager, targetPlayer, new ActivateComponentRequest(ActivatableComponent.Shield));
                } else if (projectile.getSize() == ProjectileSize.BIG && projectile.getType() == ProjectileType.Meteor) { //Se il proiettile è una meteora grande si possono attivare cannoni doppi
                    //Se nessun cannone punta verso il meteorite chiedo l'attivazione di un CannoneDoppio, se esiste
                    if (playerCanDefendThemselvesWithASingleCannon(targetPlayer, projectile, diceRoll)) {
                        message = "Ti proteggerà un cannone singolo!";
                        sendGameMessage(lobbyManager, minFirePowerPlayer, message);
                    } else if (playerCanDefendThemselvesWithADoubleCannon(targetPlayer, projectile, diceRoll)) {
                        message = "Però puoi proteggerti con un cannone doppio!";
                        sendGameMessage(lobbyManager, minFirePowerPlayer, message);
                        sendMessageAndGetResponse(lobbyManager, minFirePowerPlayer, new ActivateComponentRequest(ActivatableComponent.DoubleCannon));
                    }
                }
            }

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
        Random rand = new Random();
        int diceRoll;
        ActivateComponentRequest activateShieldRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
        ActivateComponentRequest activateDoubleCannonRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
        for (Projectile projectile : meteorSwarm.getMeteors()) {
            diceRoll = rand.nextInt(2, 13);
            broadcast(lobbyManager, new GameMessage("Stai per essere colpito da un " + projectile.getType().name() + " da " + projectile.getDirection().name() + ", indice " + diceRoll + "!"));

            ArrayList<CompletableFuture<NetworkMessage>> futures = new ArrayList<>();

            //Per ogni giocatore vedo se deve difendersi e se ne ha la possibilità
            if (projectile.getSize() == ProjectileSize.LITTLE) {
                for (Player player : rankedPlayers) {
                    if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null && playerCanDefendThemselvesWithAShield(player, projectile)) {
                        sendGameMessage(lobbyManager, player, "Puoi difenderti con uno scudo!");
                        sendMessageAndDeferGetResponse(lobbyManager, player, activateShieldRequest, futures);
                    }
                }
            } else if (projectile.getSize() == ProjectileSize.BIG) {
                for (Player player : rankedPlayers) {
                    if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null) {
                        if (playerCanDefendThemselvesWithASingleCannon(player, projectile, diceRoll)) { //Prima controllo se si può difendere con un cannone singolo
                            sendGameMessage(lobbyManager, player, "Ti proteggerà un cannone singolo!");
                        } else if (playerCanDefendThemselvesWithADoubleCannon(player, projectile, diceRoll)) { //Altrimenti faccio ricorso a un eventuale cannone doppio
                            sendGameMessage(lobbyManager, player, "Puoi difenderti con un cannone doppio!");
                            sendMessageAndDeferGetResponse(lobbyManager, player, activateDoubleCannonRequest, futures); //Metto la risposta nella lista delle future che devo aspettare
                        }
                    }
                }
            }

            //Se c'è gente che deve difendersi, notifico i player.
            if (!futures.isEmpty())
                broadcast(lobbyManager, new GameMessage("Aspettando che gli tutti i giocatori scelgano se difendersi o meno...")); //TODO: Mandarlo solo ai giocatori in attesa

            //Aspetto che tutti scelgano
            for (CompletableFuture<NetworkMessage> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            //Dò il via al proiettile e mando ship update
            for (Player player : rankedPlayers) {
                lobbyManager.getGameController().reactToProjectile(player, projectile, diceRoll);

                ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
                broadcast(lobbyManager, shipUpdate);
            }
        }
    }

    @NeedsToBeCompleted("Disattivare motori doppi")
    @Override
    public void visitOpenSpace(OpenSpace openSpace, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException, PlayerNotFoundException {
        for (Player player : rankedPlayers) {
            ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
            sendMessageAndGetResponse(lobbyManager, player, activateDoubleEnginesRequest);
            int playerEnginePower = player.getShip().calculateEnginePower();
            if (playerEnginePower == 0) {
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
                    SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(player.getNickName(), selectedPlanet,selectPlanetResponse.getPlanetIndex());
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

    private void sendMessageAndDeferGetResponse(LobbyManager lobbyManager, Player player, NetworkMessage message, ArrayList<CompletableFuture<NetworkMessage>> futures) {
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Prendo il ClientHandler associato al player
        lobbyManager.addPendingResponse(future, message.getID()); //Notifico che sono in attesa di una risposta
        clientHandler.sendMessage(message); //Mando la richiesta di attivare eventuali motori doppi
        futures.add(future);
    }

    private void sendGameMessage(LobbyManager lobbyManager, Player player, String message) {
        GameMessage gameMessage = new GameMessage(message);
        gameMessage.setMessage(message);
        lobbyManager.getPlayerHandlers().get(player.getNickName()).sendMessage(gameMessage);
    }

    private void broadcast(LobbyManager lobbyManager, NetworkMessage message) {
        lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(message));
    }

    /**
     * Checks if a player has a shield orientated the same way of the incoming projectile. Does not check for same row/column.
     *
     * @param player
     * @param projectile
     * @author Alessandro Giuseppe Gioia
     */
    private boolean playerCanDefendThemselvesWithAShield(Player player, Projectile projectile) {
        if (projectile.getSize() == ProjectileSize.BIG) return false;
        else
            return player.getShip().getComponentPositionsFromName("Shield").stream().anyMatch(p -> ((Shield) player.getShip().getComponentFromPosition(p)).getProtectedSides().contains(projectile.getDirection()));
    }

    private boolean playerCanDefendThemselvesWithASingleCannon(Player player, Projectile projectile, int diceRoll) {
        if (projectile.getType() != ProjectileType.Meteor || projectile.getSize() != ProjectileSize.BIG) return false;
        else return player.getShip().getComponentPositionsFromName("Cannon").stream().anyMatch(p -> {
            Cannon c = (Cannon) player.getShip().getComponentFromPosition(p);
            if (c.getRotation() == projectile.getDirection().ordinal()) {
                return projectile.getDirection() != ProjectileDirection.UP || (projectile.getDirection() == ProjectileDirection.UP && p.getX() == diceRoll);
            } else return false;
        });
    }

    private boolean playerCanDefendThemselvesWithADoubleCannon(Player player, Projectile projectile, int diceRoll) {
        if (projectile.getType() != ProjectileType.Meteor || projectile.getSize() != ProjectileSize.BIG) return false;
        else
            return player.getShip().getComponentPositionsFromName("DoubleCannon").stream().anyMatch(p -> {
                DoubleCannon c = (DoubleCannon) player.getShip().getComponentFromPosition(p);
                if (c.getRotation() == projectile.getDirection().ordinal()) {
                    return projectile.getDirection() != ProjectileDirection.UP || (projectile.getDirection() == ProjectileDirection.UP && p.getX() == diceRoll);
                } else return false;
            });
    }
}