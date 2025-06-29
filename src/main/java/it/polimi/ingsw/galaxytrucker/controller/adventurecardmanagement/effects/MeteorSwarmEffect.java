package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Projectile;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.MeteorSwarm;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.AskTrunkRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskTrunkResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;


/**
 * The {@code MeteorSwarmEffect} class manages the multiphase logic for resolving the "Meteor Swarm"
 * adventure card. This effect simulates a series of projectile attacks directed at players' ships.
 *
 * <p>
 * The effect includes:
 * <ul>
 *   <li>Generating and resolving meteor projectiles of different sizes and directions.</li>
 *   <li>Allowing players to defend themselves with shields or cannons.</li>
 *   <li>Handling component damage and possible ship fragmentation (into "trunks").</li>
 *   <li>Prompting players to select which ship segment to retain if their ship splits.</li>
 * </ul>
 *
 */

public abstract class MeteorSwarmEffect {

    @NeedsToBeCompleted
    //È necessario aggiungere la logica per verificare se il ship si dividerà in tronconi e permettere al giocatore di scegliere.

    private final static HashMap<LobbyManager, Integer> projectileCounters = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> diceRolls = new HashMap<>();
    private final static Random rand = new Random();
    private final static HashMap<LobbyManager, ArrayList<Player>> needToAskTrunkReq = new HashMap<>();
    private static final HashMap<LobbyManager, HashMap<Player, ArrayList<Ship>>> trunkOptions = new HashMap<>();
    private static final HashMap<LobbyManager,Integer> askTrunkReceived = new HashMap<>();

    /**
     * Sends a request to eligible players to activate shield or cannon components
     * in response to an incoming meteor. The decision depends on meteor size and ship layout.
     *
     * @param context the current {@link CardContext} of the game.
     */

    public static void sendActivateComponentRequests(CardContext context) {

//        System.out.println("Sending ActivateComponentRequests");
        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();

        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);
        Projectile projectile = meteorSwarm.getMeteors().get(projectileIndex);

        int viewDiceRoll = rand.nextInt(2, 13);
        int diceRoll = getCorrectedDiceRoll(viewDiceRoll,projectile.getDirection());
        broadcastGameMessage(context, "Stai per essere colpito da un " + projectile.getType().name() + "  "+ projectile.getSize() +" " +projectile.getDirection().name() + ", indice " + viewDiceRoll + "!");
//        System.out.println("Stai per essere colpito da un " + projectile.getType().name() + "  "+ projectile.getSize() +" " +projectile.getDirection().name() + ", indice " + viewDiceRoll + "!");
        sleepSafe(600);

        diceRolls.put(game,diceRoll); //Inserisco nei diceRolls l'actual index

        ActivateComponentRequest activateShieldsRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
        ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);

        if (projectile.getSize() == ProjectileSize.Little) {
            for (Player player : context.getCurrentRankedPlayers()) {
//                System.out.println(projectileCounters.get(game) +"  Little  " + player.getNickName());
                if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null && playerCanDefendThemselvesWithAShield(player, projectile)) {
                    sendGameMessage(context, player, "Puoi difenderti con uno scudo!");
                    sleepSafe(600);
                    context.nextPhase();
                    sendMessage(context, player, activateShieldsRequest);
                    return;
                }
            }
        } else if (projectile.getSize() == ProjectileSize.Big) {
            for (Player player : context.getCurrentRankedPlayers()) {
//                System.out.println( projectileCounters.get(game) +"  Big  " + player.getNickName());

                if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null) {
                    if (playerCanDefendThemselvesWithASingleCannon(player, projectile, diceRoll)) { //Prima controllo se si può difendere con un cannone singolo
                        sendGameMessage(context, player, "Ti proteggerà un cannone singolo!");
                        sleepSafe(600);

                    } else if (playerCanDefendThemselvesWithADoubleCannon(player, projectile, diceRoll)) { //Altrimenti faccio ricorso a un eventuale cannone doppio
                        sendGameMessage(context, player, "Puoi difenderti con un cannone doppio!");
                        sleepSafe(600);
                        context.nextPhase();
                        sendMessage(context, player, activateDoubleCannonsRequest);

                        return;
                    }
                }
            }
        }
//      System.out.println("end");
        context.nextPhase();
        context.executePhase();
    }

    /**
     * Executes the impact of the meteor swarm, applying damage to player ships,
     * broadcasting results, and checking for potential ship fragmentation.
     * Triggers trunk selection if needed.
     *
     * @param context the current {@link CardContext}.
     */
    public static void unleashTheMeteorSwarm(CardContext context) {
//        System.out.println("UnleashTheMeteorSwarm");


        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();

        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);
        Projectile projectile = meteorSwarm.getMeteors().get(projectileIndex);



        int diceRoll = diceRolls.get(game);
        needToAskTrunkReq.putIfAbsent(game,new ArrayList<>());

        for (Player player : context.getCurrentRankedPlayers()) {
            //
//            System.out.println("Before attack " + player.getNickName());
//            ShipPrintUtils.printShip(player.getShip());

            broadcastShipUpdate(context,player);
            sleepSafe(600);


             Tile removedTile= game.getGameController().reactToProjectile(player, projectile, diceRoll);
            if (removedTile != null) {
                ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();
                broadcastGameMessage(context, "Purtroppo, " + player.getNickName() + " è stato colpito e ha perso un " + removedTile.getMyComponent().accept(componentNameVisitor));
            } else {
                broadcastGameMessage(context, "Congratulazioni, " + player.getNickName() + " è riuscito a schivare l'attacco!");
            }
            sleepSafe(600);
            resetShield(player);
            resetDoubleCannon(player);

          broadcastShipUpdate(context,player);


            ArrayList<Ship> troncs = player.getShip().getTronc();
            if(troncs.size()>1){

//                System.out.println();
//                System.out.println("asktroc need");

                needToAskTrunkReq.get(game).add(player);
                trunkOptions
                        .computeIfAbsent(game, g -> new HashMap<>())
                        .put(player, troncs);
            }

            //
//            System.out.println("After attack" + player.getNickName());
//            ShipPrintUtils.printShip(player.getShip());


            ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
            broadcast(context, shipUpdate);
        }

        if(!needToAskTrunkReq.get(game).isEmpty()){
            context.nextPhase();
            context.executePhase();
            return;
        }

        broadcastGameMessage(context, "Tutti gli attacchi meteorici di questo turno sono stati risolti.");
        sleepSafe(300);
        goToEndPhaseOrReset(context);
    }

    /**
     * Sends a request to all affected players to choose one of the surviving
     * ship trunks, in case their ship was split by meteor damage.
     *
     * @param context the current {@link CardContext}.
     */
    public static void askTrunkReq(CardContext context) {
//        System.out.println("AskTrunkReq");
        LobbyManager game = context.getCurrentGame();
        ArrayList<Player> players = needToAskTrunkReq.get(game);
        context.nextPhase();
        for (Player player : players) {
            ArrayList<Ship> troncs = trunkOptions.get(game).get(player);
            AskTrunkRequest askTrunkRequest = new AskTrunkRequest(troncs);

//            System.out.println("Player: "+player.getNickName()+"  askTrunkRequest ");
            sendMessage(context, player, askTrunkRequest);
        }

    }

    /**
     * Processes a player's response to the trunk selection prompt, replacing
     * their current ship with the selected trunk and updating the game state.
     * Ends the trunk selection phase once all players have responded.
     *
     * @param context the current {@link CardContext}.
     */

    public static void receivedTrunkRepo(CardContext context) {
//        System.out.println("ReceivedTrunkRepo");
        LobbyManager game = context.getCurrentGame();
        AskTrunkResponse askTrunkResponse = (AskTrunkResponse) context.getIncomingNetworkMessage();

        int indexTrunk = askTrunkResponse.getTrunkIndex();
        ArrayList<Player> rankedPlayers =context.getCurrentRankedPlayers();

        Player currentPlayer = rankedPlayers.stream()
                .filter(p -> p.getNickName().equals(askTrunkResponse.getPlayerNickname()))
                .findFirst()
                .orElse(null);

        if(currentPlayer == null){
//            System.out.println("Player " + askTrunkResponse.getPlayerNickname() + " not found");
            resetState(game);
            return;
        }

        Ship newShip;
        ArrayList<Ship> trunks = trunkOptions.get(game).get(currentPlayer);
        if (trunks != null && indexTrunk >= 0 && indexTrunk < trunks.size()) {
             newShip = trunks.remove(indexTrunk);
            currentPlayer.replaceShip(newShip);
            broadcast(context, new ShipUpdate(newShip, currentPlayer.getNickName() ));
            addDestroyedTilesInTrunc(currentPlayer, trunks);
        }


        //invio a tutti la nuova nave

        askTrunkReceived.merge(game, 1, Integer::sum);

        if(askTrunkReceived.get(game)==needToAskTrunkReq.get(game).size()){
            needToAskTrunkReq.remove(game);
            trunkOptions.remove(game);
            askTrunkReceived.remove(game);
            goToEndPhaseOrReset(context);
        }
        //FSM management



    }
    /**
     * Advances to the next meteor if any remain, or ends the effect phase entirely.
     * Also handles resetting all internal state related to the meteor swarm for the game session.
     *
     * @param context the current {@link CardContext}.
     */
    private static void goToEndPhaseOrReset(CardContext context) {
//        System.out.println("GoToEndPhase Or Reset");
        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();
        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);

        if (projectileIndex < meteorSwarm.getMeteors().size() - 1) {
            projectileCounters.put(game, projectileIndex + 1);
            context.resetFSM();
            context.executePhase();
        } else { //Finito
            resetState(context.getCurrentGame());
            //Execute CommonEffects::end
            context.goToEndPhase();
            context.executePhase();
        }
    }
    /**
     * Clears all internal tracking data (e.g., projectile counters, trunk maps)
     * for a given game session.
     *
     * @param game the {@link LobbyManager} representing the current game.
     */

    private static void resetState(LobbyManager game) {
        projectileCounters.remove(game);
        diceRolls.remove(game);
        needToAskTrunkReq.remove(game);
        trunkOptions.remove(game);
        askTrunkReceived.remove(game);
    }
}
