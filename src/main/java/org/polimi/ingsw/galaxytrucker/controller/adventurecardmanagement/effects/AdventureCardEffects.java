package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
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
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AdventureCardEffects implements AdventureCardVisitorsInterface<Void> {
    private final LobbyManager lobbyManager;
    private final ArrayList<Player> rankedPlayers;
    private final CardPhase cardPhase;

    //STATIC HELPER ATTRIBUTES
    //CombatZone

    //OpenSpace
    private final static HashMap<LobbyManager, HashMap<String, Integer>> playerToPowerMapPerGame = new HashMap<>();

    public AdventureCardEffects(LobbyManager lobbyManager, ArrayList<Player> rankedPlayers, CardPhase cardPhase) {
        this.lobbyManager = lobbyManager;
        this.rankedPlayers = rankedPlayers;
        this.cardPhase = cardPhase;
    }

    @Override
    public Void visit(AbandonedShip abandonedShip) {
//        ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
//        DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(abandonedShip.getRequiredCrewMembers()); //Il client deve scartare un tot di equipaggio
//        for (Player player : rankedPlayers) {
//            if (player.getShip().getnCrew() < abandonedShip.getRequiredCrewMembers()) {
//                GameMessage gameMessage = new GameMessage("Non hai abbastanza membri dell'equipaggio per attivare questa carta.");
//                lobbyManager.getPlayerHandlers().get(player.getNickName()).sendMessage(gameMessage);
//                continue; //Se il player non ha abbastanza equipaggio passo al prossimo
//            }
//
//            //Chiedo al player se vuole attivare la carta
//            ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) sendMessage(lobbyManager, player, activateAdventureCardRequest);
//            if (activateAdventureCardResponse.isActivated()) {
//                DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) sendMessage(lobbyManager, player, discardCrewMembersRequest); //Aspetto la risposta
//
//                discardCrewMembers(player, discardCrewMembersResponse);
//
//                //Broadcasto nuova nave
//                ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
//                lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(shipUpdate));
//
//                player.addCredits(abandonedShip.getCredits()); //Accredito crediti
//
//                movePlayer(lobbyManager, player, -abandonedShip.getDaysLost()); //Sposto il player
//                break; //Solo un player può attivare la carta
//            }
//        }

        Player player = lobbyManager.getGameController().getCurrentCardContext().getCurrentPlayer();

        switch (cardPhase){
            case Start -> {
                if (player.getShip().getnCrew() >= abandonedShip.getRequiredCrewMembers()) {
                    ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
                    sendMessage(lobbyManager, player, activateAdventureCardRequest);
                }
                else{
                    GameMessage gameMessage = new GameMessage("Non hai abbastanza membri dell'equipaggio per attivare questa carta.");
                    lobbyManager.getPlayerHandlers().get(player.getNickName()).sendMessage(gameMessage);

                    //Passiamo al prossimo giocatore
                    //lobbyManager.getGameController().incrementCurrentPlayerIndex();
                    visit(abandonedShip);
                }
            }
            case CardActivated -> {
                DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(abandonedShip.getRequiredCrewMembers());
                sendMessage(lobbyManager, player, discardCrewMembersRequest);
            }
            case CrewDiscarded -> {

            }
        }

        return null;
    }

    @NeedsToBeChecked("Sempre il problema di riuscire ad identificare lo ship update")
    @Override
    public Void visit(AbandonedStation abandonedStation) {
        ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
        for (Player player : rankedPlayers) {
            if (player.getShip().getnCrew() < abandonedStation.getRequiredCrewMembers())
                continue; //Se il player non ha abbastanza equipaggio passo al prossimo

            //Chiedo al player se vuole attivare la carta
            ActivateAdventureCardResponse activateAdventureCardResponse = new ActivateAdventureCardResponse(true);
            //(ActivateAdventureCardResponse) sendMessage(lobbyManager, player, activateAdventureCardRequest);

            if (activateAdventureCardResponse.isActivated()) {
                ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName()); //TODO: Trovare un metodo per identificare lo ship update
                CompletableFuture<NetworkMessage> shipUpdateFuture = new CompletableFuture<>();
                lobbyManager.addPendingResponse(shipUpdateFuture, shipUpdate.getID()); //Mi deve arrivare uno ShipUpdate
                //shipUpdateFuture.get(); //Aspetto che arrivi lo ship update
                movePlayer(lobbyManager, player, -abandonedStation.getDaysLost());
            }
        }

        return null;
    }

    @Override
    public Void visit(CombatZone combatZone) {
        //Parte 2: minor potenza motrice = rimozione equipaggio
        int minEnginePower = 0;
        Player minEnginePowerPlayer = null;
        for (Player player : rankedPlayers) {
            //Richiedo l'attivazione dei motori doppi, se esistono
            ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
            sendMessage(lobbyManager, player, activateDoubleEnginesRequest);

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
        broadcastGameMessage(lobbyManager, minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi!");

        //Richiedo al player le posizioni delle housing unit da cui rimuovere equipaggio
        DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(combatZone.getCrewMembersLost());
        DiscardCrewMembersResponse discardCrewMembersResponse = new DiscardCrewMembersResponse(null);
                //(DiscardCrewMembersResponse) sendMessage(lobbyManager, minEnginePowerPlayer, discardCrewMembersRequest);

        discardCrewMembers(minEnginePowerPlayer, discardCrewMembersResponse);

        //Parte 3: minor potenza di fuoco = cannonate
        float minFirePower = 0;
        Player minFirePowerPlayer = null;
        for (Player player : rankedPlayers) {
            //Chiedo l'attivazione di cannoni doppi, se esistono
            if (!player.getShip().getComponentPositionsFromName("DoubleCannon").isEmpty()) {
                ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
                sendMessage(lobbyManager, player, activateDoubleEnginesRequest);
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
        broadcastGameMessage(lobbyManager, minFirePowerPlayer.getNickName() + " ha la minor potenza di fuoco, quindi subisce delle cannonate!");

        Random rand = new Random();
        final Player targetPlayer = minFirePowerPlayer;
        int diceRoll;
        String message;
        for (Projectile projectile : combatZone.getProjectiles()) {
            diceRoll = rand.nextInt(2, 13);

            message = "Stai per essere colpito da un " + projectile.getType().name() + " da " + projectile.getDirection().name() + ", indice " + diceRoll + "!";
            sendGameMessage(lobbyManager, minFirePowerPlayer, message);

            if (targetPlayer.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null) { //Se il proiettile va a colpire un componente, vediamo se il player può proteggersi
                if (projectile.getSize() == ProjectileSize.Little && playerCanDefendThemselvesWithAShield(targetPlayer, projectile)) { //Se il proiettile è piccolo si possono attivare gli scudi, se ne esistono orientati correttamente
                    message = "Però puoi proteggerti con uno scudo!";
                    sendGameMessage(lobbyManager, minFirePowerPlayer, message);
                    sendMessage(lobbyManager, targetPlayer, new ActivateComponentRequest(ActivatableComponent.Shield));
                } else if (projectile.getSize() == ProjectileSize.Big && projectile.getType() == ProjectileType.Meteor) { //Se il proiettile è una meteora grande si possono attivare cannoni doppi
                    //Se nessun cannone punta verso il meteorite chiedo l'attivazione di un CannoneDoppio, se esiste
                    if (playerCanDefendThemselvesWithASingleCannon(targetPlayer, projectile, diceRoll)) {
                        message = "Ti proteggerà un cannone singolo!";
                        sendGameMessage(lobbyManager, minFirePowerPlayer, message);
                    } else if (playerCanDefendThemselvesWithADoubleCannon(targetPlayer, projectile, diceRoll)) {
                        message = "Però puoi proteggerti con un cannone doppio!";
                        sendGameMessage(lobbyManager, minFirePowerPlayer, message);
                        sendMessage(lobbyManager, minFirePowerPlayer, new ActivateComponentRequest(ActivatableComponent.DoubleCannon));
                    }
                }
            }

            lobbyManager.getGameController().reactToProjectile(minFirePowerPlayer, projectile, diceRoll);
            ShipUpdate shipUpdate = new ShipUpdate(minFirePowerPlayer.getShip(), minFirePowerPlayer.getNickName());
            broadcast(lobbyManager, shipUpdate);
        }

        Player currentPlayer = lobbyManager.getGameController().getCurrentCardContext().getCurrentPlayer();

        switch (lobbyManager.getGameController().getCurrentCardContext().getCurrentPhaseIndex()){
            case 0 -> {
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
                broadcastGameMessage(lobbyManager, minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");

                movePlayer(lobbyManager, minCrewMembersPlayer, -combatZone.getDaysLost());
            }
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
            case 4 -> {}
        }

        return null;
    }

    @NeedsToBeCompleted
    public Void visit(Epidemic epidemic) {
        ArrayList<Position> housings = new ArrayList<>();
        Ship ship;
        ModularHousingUnit modularHousingUnit;
        for (Player player : rankedPlayers) {
            ship = player.getShip();

            //Prendo tutte le housing units
            housings.addAll(ship.getComponentPositionsFromName("CentralHousingUnit"));
            housings.addAll(ship.getComponentPositionsFromName("ModularHousingUnit"));

//            CentralHousingUnit centralHousingUnit = (CentralHousingUnit) ship.getComponentFromPosition(housings.getFirst());
//            ArrayList<Pair<Position, Tile>> connectedModularHousingUnitsToCentralHousingUnitWithPositions = ship.getConnectedHousingUnitTiles(housings.getFirst());
//            housings.removeFirst();
//
//            for(Pair<Position, Tile> connectedModularHousingUnitToCentralHousingUnitWithPosition : connectedModularHousingUnitsToCentralHousingUnitWithPositions) {
//                modularHousingUnit = (ModularHousingUnit) connectedModularHousingUnitToCentralHousingUnitWithPosition.getValue().getMyComponent();
//
//                if(centralHousingUnit.getNCrewMembers() != 0 && modularHousingUnit.getNCrewMembers() != 0) {
//                    centralHousingUnit.removeCrewMember();
//                    modularHousingUnit.removeCrewMember();
//                }
//
//                housings.remove(connectedModularHousingUnitToCentralHousingUnitWithPosition.getKey());
//            }
//
//            for(Position modularHousingUnitPosition : housings) {
//                modularHousingUnit = (ModularHousingUnit) ship.getComponentFromPosition(modularHousingUnitPosition);
//
//                ArrayList<Pair<Position, Tile>> connectedModularHousingUnitsWithPosition = ship.getConnectedHousingUnitTiles(modularHousingUnitPosition);
//
//                for(Pair<Position, Tile> connectedModularHousingUnitWithPosition : connectedModularHousingUnitsWithPosition) {
//                    ModularHousingUnit connectedModularHousingUnit = (ModularHousingUnit) connectedModularHousingUnitWithPosition.getValue().getMyComponent();
//
//                    if(modularHousingUnit.getNCrewMembers() != 0 && connectedModularHousingUnit.getNCrewMembers() != 0) {
//                        modularHousingUnit.removeCrewMember();
//                        connectedModularHousingUnit.removeCrewMember();
//                    }
//
//                    housings.remove(connectedModularHousingUnitWithPosition.getKey());
//                }
//            }

            for (Position housingUnitPosition : housings) {
                //Dynamic binding, anche se faccio il cast a CentralHousingUnit il tipo dinamico potrebbe essere ModularHousingUnit
                CentralHousingUnit housingUnit = (CentralHousingUnit) ship.getComponentFromPosition(housingUnitPosition);

                ArrayList<Pair<Position, Tile>> connectedHousingUnitTilesWithPositions = ship.getConnectedHousingUnitTiles(housingUnitPosition);
                for (Pair<Position, Tile> connectedHousingUnitTileWithPosition : connectedHousingUnitTilesWithPositions) {
                    //Dynamic binding, anche se faccio il cast a CentralHousingUnit il tipo dinamico potrebbe essere ModularHousingUnit
                    CentralHousingUnit connectedHousingUnit = (CentralHousingUnit) connectedHousingUnitTileWithPosition.getValue().getMyComponent();

                    //Se entrambe le housingUnits hanno equipaggio, lo rimuovo da entrambe
                    if (housingUnit.getNCrewMembers() != 0 && connectedHousingUnit.getNCrewMembers() != 0) {
                        housingUnit.removeCrewMember();
                        connectedHousingUnit.removeCrewMember();
                    }

                    //Se nella housing unit connessa non ci sono più membri la rimuovo dalle housing units da controllare
                    if (connectedHousingUnit.getNCrewMembers() == 0)
                        housings.remove(connectedHousingUnitTileWithPosition.getKey());

                    //Se nella housing unit che stiamo guardando non ci sono più membri, esco dal foreach
                    if (housingUnit.getNCrewMembers() == 0)
                        break;
                }
            }
        }

        return null;
    }

    @Override
    public Void visit(MeteorSwarm meteorSwarm) {
        Random rand = new Random();
        int diceRoll;
        ActivateComponentRequest activateShieldRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
        ActivateComponentRequest activateDoubleCannonRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
        for (Projectile projectile : meteorSwarm.getMeteors()) {
            diceRoll = rand.nextInt(2, 13);
            broadcastGameMessage(lobbyManager, "Stai per essere colpito da un " + projectile.getType().name() + " da " + projectile.getDirection().name() + ", indice " + diceRoll + "!");

            ArrayList<CompletableFuture<NetworkMessage>> futures = new ArrayList<>();

            //Per ogni giocatore vedo se deve difendersi e se ne ha la possibilità
            if (projectile.getSize() == ProjectileSize.Little) {
                for (Player player : rankedPlayers) {
                    if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null && playerCanDefendThemselvesWithAShield(player, projectile)) {
                        sendGameMessage(lobbyManager, player, "Puoi difenderti con uno scudo!");
                        sendMessageAndDeferGetResponse(lobbyManager, player, activateShieldRequest, futures);
                    }
                }
            } else if (projectile.getSize() == ProjectileSize.Big) {
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

            //Se c'è gente che deve difendersi, notifico i player
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

        return null;
    }

    @Override
    public Void visit(OpenSpace openSpace) {

        Player player = lobbyManager.getGameController().getCurrentCardContext().getCurrentPlayer();

        switch (cardPhase){
            case Start -> {
                ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);


                lobbyManager.getGameController().getCurrentCardContext().setExpectedNetworkMessageType(NetworkMessageType.ActivateComponentResponse);
            }

            case ComponentActivated -> {

            }

            case End -> {
                HashMap<String, Integer> playerToPowerMap = playerToPowerMapPerGame.get(lobbyManager);

                playerToPowerMap.forEach((nickname, power) -> {
                if (power == 0) {
                 try {
                     lobbyManager.getGameController().removePlayerFromGame(nickname, false);
                  } catch (PlayerNotFoundException e) {
                    throw new RuntimeException(e);
                 }
                }
                });

                //Cleanup
                playerToPowerMapPerGame.get(lobbyManager).forEach((nickname, ignoredPower) -> playerToPowerMap.remove(nickname));
                playerToPowerMapPerGame.remove(lobbyManager);
            }
            default -> throw new IllegalStateException();
        }

        return null;
    }

    @NeedsToBeChecked("Non è giusto inserire un selectedPlanetUpdate nell'addPendingResponse di shipUpdates")
    @Override
    public Void visit(Planets planets) {
        ArrayList<CompletableFuture<NetworkMessage>> shipUpdates = new ArrayList<>();
        ArrayList<Player> landedPlayers = new ArrayList<>();

        for (Player player : rankedPlayers) {
            if (!planets.getPlanets().stream().allMatch(Planet::isOccupied)) { //Se non tutti i pianeti sono occupati
                ArrayList<Planet> notOccupiedPlanets = new ArrayList<>(planets.getPlanets().stream().filter(planet -> !planet.isOccupied()).toList());
                SelectPlanetRequest selectPlanetRequest = new SelectPlanetRequest(notOccupiedPlanets);
                SelectPlanetResponse selectPlanetResponse = new SelectPlanetResponse(null, 0);
                        //(SelectPlanetResponse) sendMessage(lobbyManager, player, selectPlanetRequest); //Aspetto che il player mandi la risposta


                Planet selectedPlanet = selectPlanetResponse.getSelectedPlanet();
                if (selectedPlanet != null) { //Se il player ha scelto
                    selectedPlanet.setOccupied(true);
                    SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(player.getNickName(), selectedPlanet, selectPlanetResponse.getPlanetIndex());
                    broadcast(lobbyManager, selectedPlanetUpdate); //Broadcasto un SelectedPlanetUpdate
                    CompletableFuture<NetworkMessage> shipUpdateFuture = new CompletableFuture<>();
                    lobbyManager.addPendingResponse(shipUpdateFuture, selectedPlanetUpdate.getID()); //Mi deve arrivare uno ShipUpdate
                    shipUpdates.add(shipUpdateFuture);
                    landedPlayers.add(player);

                    //TEST
                    //System.out.println("Simulando delay scelta player");
                    //Thread.sleep(2000);
                    //shipUpdateFuture.complete(new ShipUpdate(null, null));
                    //End TEST
                }
            } else break; //Se tutti i pianeti sono occupati usciamo dal ciclo
        }

        //Aspetto che arrivino tutti gli ShipUpdate del caso
        for (CompletableFuture<NetworkMessage> future : shipUpdates) {
            //future.get();
            //System.out.println("Ottenuto ship update");
        }

        for (Player player : landedPlayers.reversed()) { //I landed players sono in ordine di rotta
            movePlayer(lobbyManager, player, -planets.getDaysLost());
        }

        return null;
    }

    @Override
    //porca troia SI
    public Void visit(Stardust stardust) {
        //player.getShip().calcExposedConnectors();
        for (Player player : rankedPlayers.reversed()) { //Si parte dall'ultimo
            movePlayer(lobbyManager, player, -player.getShip().getnExposedConnector());
        }

        return null;
    }

    @Override
    public Void visit(Pirates pirates) {
        return null;
    }

    @Override
    public Void visit(Slavers slavers) {
        return null;
    }

    @Override
    public Void visit(Smugglers smugglers) {
        return null;
    }
}