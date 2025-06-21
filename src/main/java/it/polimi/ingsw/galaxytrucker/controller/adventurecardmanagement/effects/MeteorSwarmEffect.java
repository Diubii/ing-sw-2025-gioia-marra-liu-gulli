package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Projectile;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.MeteorSwarm;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.AskTrunkRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskTrunkResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class MeteorSwarmEffect {

    @NeedsToBeCompleted
    //È necessario aggiungere la logica per verificare se il ship si dividerà in tronconi e permettere al giocatore di scegliere.

    private final static HashMap<LobbyManager, Integer> projectileCounters = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> diceRolls = new HashMap<>();
    private final static Random rand = new Random();
    private final static HashMap<LobbyManager, ArrayList<Player>> needToAskTrunkReq = new HashMap<>();
    private static final HashMap<LobbyManager, HashMap<Player, ArrayList<Ship>>> trunkOptions = new HashMap<>();
    private static final HashMap<LobbyManager,Integer> askTrunkReceived = new HashMap<>();


    public static void sendActivateComponentRequests(CardContext context) {

        System.out.println("Sending ActivateComponentRequests");
        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();

        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);
        Projectile projectile = meteorSwarm.getMeteors().get(projectileIndex);

        int viewDiceRoll = rand.nextInt(2, 13);
        int diceRoll = getCorrectedDiceRoll(viewDiceRoll,projectile.getDirection());
        broadcastGameMessage(context, "Stai per essere colpito da un " + projectile.getType().name() + "  "+ projectile.getSize() +" " +projectile.getDirection().name() + ", indice " + viewDiceRoll + "!");
        System.out.println("Stai per essere colpito da un " + projectile.getType().name() + "  "+ projectile.getSize() +" " +projectile.getDirection().name() + ", indice " + viewDiceRoll + "!");

        diceRolls.put(game,diceRoll); //Inserisco nei diceRolls l'actual index

        ActivateComponentRequest activateShieldsRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
        ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);

        if (projectile.getSize() == ProjectileSize.Little) {
            for (Player player : context.getCurrentRankedPlayers()) {
                System.out.println(projectileCounters.get(game) +"  Little  " + player.getNickName());
                if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null && playerCanDefendThemselvesWithAShield(player, projectile)) {
                    sendGameMessage(context, player, "Puoi difenderti con uno scudo!");
                    context.nextPhase();
                    sendMessage(context, player, activateShieldsRequest);
                    return;
                }
            }
        } else if (projectile.getSize() == ProjectileSize.Big) {
            for (Player player : context.getCurrentRankedPlayers()) {
                System.out.println( projectileCounters.get(game) +"  Big  " + player.getNickName());

                if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null) {
                    if (playerCanDefendThemselvesWithASingleCannon(player, projectile, diceRoll)) { //Prima controllo se si può difendere con un cannone singolo
                        sendGameMessage(context, player, "Ti proteggerà un cannone singolo!");
                    } else if (playerCanDefendThemselvesWithADoubleCannon(player, projectile, diceRoll)) { //Altrimenti faccio ricorso a un eventuale cannone doppio
                        sendGameMessage(context, player, "Puoi difenderti con un cannone doppio!");
                        context.nextPhase();
                        sendMessage(context, player, activateDoubleCannonsRequest);
                        return;
                    }
                }
            }
        }
      System.out.println("end");
        context.nextPhase();
        context.executePhase();
    }

    public static void unleashTheMeteorSwarm(CardContext context) {
        System.out.println("UnleashTheMeteorSwarm");
        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();

        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);
        Projectile projectile = meteorSwarm.getMeteors().get(projectileIndex);

        int diceRoll = diceRolls.get(game);
        needToAskTrunkReq.putIfAbsent(game,new ArrayList<>());
        for (Player player : context.getCurrentRankedPlayers()) {
            game.getGameController().reactToProjectile(player, projectile, diceRoll);
            resetShield(player);
            resetDoubleCannon(player);
            ArrayList<Ship> troncs = player.getShip().getTronc();
            if(troncs.size()!=1){

                needToAskTrunkReq.get(game).add(player);
                trunkOptions
                        .computeIfAbsent(game, g -> new HashMap<>())
                        .put(player, troncs);
            }

            ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
            broadcast(context, shipUpdate);
        }

        if(!needToAskTrunkReq.get(game).isEmpty()){
            context.nextPhase();
            context.executePhase();
            return;
        }

        goToEndPhaseOrReset(context);
    }
    public static void askTrunkReq(CardContext context) {
        System.out.println("AskTrunkReq");
        LobbyManager game = context.getCurrentGame();
        ArrayList<Player> players = needToAskTrunkReq.get(game);

        for (Player player : players) {
            ArrayList<Ship> troncs = trunkOptions.get(game).get(player);
            AskTrunkRequest askTrunkRequest = new AskTrunkRequest(troncs);

            System.out.println("Player: "+player.getNickName()+"askTrunkRequest ");
            sendMessage(context, player, askTrunkRequest);
        }
        context.nextPhase();
    }

    public static void receivedTrunkRepo(CardContext context) {
        System.out.println("ReceivedTrunkRepo");
        LobbyManager game = context.getCurrentGame();
        AskTrunkResponse askTrunkResponse = (AskTrunkResponse) context.getIncomingNetworkMessage();

        int indexTrunk = askTrunkResponse.getTrunkIndex();
        ArrayList<Player> rankedPlayers =context.getCurrentRankedPlayers();
        Player currentPlayer = null;

        for (Player player : rankedPlayers) {
            if(Objects.equals(askTrunkResponse.getPlayerNickname(), player.getNickName())){
                currentPlayer = player;
            }
        }

        if(currentPlayer == null){
            System.out.println("Player " + askTrunkResponse.getPlayerNickname() + " not found");
            return;
        }

        Ship newShip = trunkOptions.get(game).get(currentPlayer).get(indexTrunk);
        currentPlayer.replaceShip(newShip);

        //invio a tutti la nuova nave
        broadcast(context, new ShipUpdate(newShip, currentPlayer.getNickName() ));

        askTrunkReceived.putIfAbsent(game,0);
        askTrunkReceived.put(game,askTrunkReceived.get(game)+1);

        if(askTrunkReceived.get(game)==needToAskTrunkReq.get(game).size()){
            goToEndPhaseOrReset(context);
        }
        //FSM management



    }

    private static void goToEndPhaseOrReset(CardContext context) {
        System.out.println("GoToEndPhase Or Reset");
        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();
        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);

        if (projectileIndex < meteorSwarm.getMeteors().size() - 1) {
            projectileCounters.put(game, projectileIndex + 1);
            context.resetFSM();
            context.executePhase();
        } else { //Finito
            projectileCounters.remove(game);
            diceRolls.remove(game);

            //Execute CommonEffects::end
            context.goToEndPhase();
            context.executePhase();
        }
    }

}
