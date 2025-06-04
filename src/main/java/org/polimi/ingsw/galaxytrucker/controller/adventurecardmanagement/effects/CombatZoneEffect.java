package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CombatZone;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class CombatZoneEffect {
    //PHASE 1
    private final static HashMap<LobbyManager, Pair<Integer, Player>> minCrewMembersCheckPairs = new HashMap<>();
    private final static HashMap<LobbyManager,Integer> levelCombatZone = new HashMap<>();


    public static void checkLevel(CardContext context) {
        System.out.println("Checking level");
        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        LobbyManager game = context.getCurrentGame();
        int level = combatZone.getLevel();
        levelCombatZone.putIfAbsent(game,level);

        if(level == 1){
            context.nextPhase();
            context.executePhase();
        }
        else if(level == 2){
            context.nextPhase(6);
            context.executePhase();

        }

    }
    public static void minCrewMembersCheck(CardContext context) {
        CombatZone combatZone = (CombatZone) context.getAdventureCard();

        Player player = context.getCurrentPlayer();

        int playerCrewMembersNumber = player.getShip().getnCrew();
        combatZoneCompare(context, playerCrewMembersNumber, minCrewMembersCheckPairs);

        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minCrewMembersCheckPairs);
        Player minCrewMembersPlayer = pair.getValue();

        if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
            if(combatZone.getLevel() ==1) {

                broadcastGameMessage(context, minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");
                movePlayer(context, minCrewMembersPlayer, -combatZone.getDaysLost());

                //Passo subito alla prossima fase
                context.nextPhase();

                //Cleanup
                minCrewMembersCheckPairs.remove(context.getCurrentGame());
                return;
            }
            else{

            }
        }

        context.nextPlayer();
        context.executePhase();
    }


    //PHASE 2
    private final static HashMap<LobbyManager, Pair<Integer, Player>> minEnginePowerCheckPairs = new HashMap<>();

    public static void sendDoubleEnginesActivationRequest(CardContext context) {
        Player player = context.getCurrentPlayer();
        GameMessage gameInfo = new GameMessage(player.getNickName());
        gameInfo.setMessage("Il giocatore con la minore potenza del motore deve ricevere una penalità" );
        broadcast(context,gameInfo);

        //Controllo se il player può attivare DoubleEngines  charged==false
        if (player.getShip().getComponentPositionsFromName("DoubleEngine").stream().anyMatch(p -> !player.getShip().getComponentFromPosition(p).isCharged())) {
            ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
            sendMessage(context, player, activateDoubleEnginesRequest);
            context.nextPhase();
        } else {
            context.nextPhase();
            context.executePhase();
        }
    }

    public static void minEnginePowerCheck(CardContext context) {
        Player player = context.getCurrentPlayer();
        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        LobbyManager game = context.getCurrentGame();
        int playerEnginePower = player.getShip().calculateEnginePower();
        combatZoneCompare(context, playerEnginePower, minEnginePowerCheckPairs);

        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs);
        Player minEnginePowerPlayer = pair.getValue();

        if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
            if(levelCombatZone.get(game)==1){
            broadcastGameMessage(context, minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi perde equipaggio!");
            //Passo alla prossima fase
            context.nextPhase();}
            else{
                broadcastGameMessage(context, minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi perde merci!");
                //to do discard merci
                ArrayList<Good> removedGoods = Util.getAndRemoveMostValuableGoods(player.getShip(), combatZone.getGoodsLost());
                int goodsCount = removedGoods.size();
                int batteryToDiscard = combatZone.getGoodsLost() - goodsCount;

                String message;
                if (goodsCount == combatZone.getGoodsLost()) {
                    message = "[CombatZone] Ha ha! We'll steal your " + goodsCount + " most valuable goods!";
                } else if (goodsCount > 0) {
                    Util.removeBatteries(player.getShip(), batteryToDiscard);
                    message = "[CombatZone] We'll steal your " + goodsCount + " most valuable good(s) and " + batteryToDiscard + " battery(ies), if you have them.";
                } else {
                    Util.removeBatteries(player.getShip(), batteryToDiscard);
                    message = "[CombatZone] You don't have any goods, so we'll steal " + batteryToDiscard + " of your batteries! Well, if you have any, poor fella.";
                }

                GameMessage personalInfo = new GameMessage(message);
                game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalInfo);

                context.previousPhase(2);
                context.setCurrentPlayer(context.getCurrentRankedPlayers().getFirst());
                context.executePhase();
                return;
            }
        } else {
            //Torno indietro per inviare la ActivateComponentRequest al prossimo player
            context.previousPhase();
        }

        context.nextPlayer();
        context.executePhase();
    }

    public static void sendDiscardCrewMembersRequest(CardContext context) {
        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        int playerCrewMembersNumber = context.getCurrentPlayer().getShip().getnCrew();
        int nCrewToBeDiscarded = Integer.min(playerCrewMembersNumber, combatZone.getCrewMembersLost());
        sendMessage(context, getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs).getValue(), new DiscardCrewMembersRequest(nCrewToBeDiscarded));
        context.nextPhase();
    }

    public static void receivedDiscardCrewMembersRequest(CardContext context) {
        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();
        discardCrewMembers(getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs).getValue(), discardCrewMembersResponse, combatZone.getCrewMembersLost());

        //Cleanup
        minEnginePowerCheckPairs.remove(context.getCurrentGame());

        context.nextPhase();
        context.executePhase();
    }

    //PHASE 3
    private final static HashMap<LobbyManager, Pair<Float, Player>> minFirePowerCheckPairs = new HashMap<>();

    public static void sendDoubleCannonsActivationRequest(CardContext context) {
        Player player = context.getCurrentPlayer();
        GameMessage gameInfo = new GameMessage(player.getNickName());
        gameInfo.setMessage("Il giocatore con la minore potenza del cannon deve ricevere una penalità" );
        broadcast(context,gameInfo);

        //Controllo se il player può attivare DoubleEngines  no charged
        if (player.getShip().getComponentPositionsFromName("DoubleCannon").stream().anyMatch(p -> !player.getShip().getComponentFromPosition(p).isCharged())) {
            ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
            sendMessage(context, player, activateDoubleCannonsRequest);
            context.nextPhase();
        } else {
            context.nextPhase();
            context.executePhase();
        }
    }

    public static void minFirePowerCheck(CardContext context) {
        Player player = context.getCurrentPlayer();
        CombatZone combatZone = (CombatZone) context.getAdventureCard();

        LobbyManager game = context.getCurrentGame();
        float playerFirePower = player.getShip().calculateFirePower();
        combatZoneCompare(context, playerFirePower, minFirePowerCheckPairs);

        Pair<Float, Player> pair = getNumberPlayerPairFromHashMap(context, minFirePowerCheckPairs);
        Player minFirePowerPlayer = pair.getValue();
        if(levelCombatZone.get(game) == 1) {

            if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
                broadcastGameMessage(context, minFirePowerPlayer.getNickName() + " ha la minor potenza di fuoco, quindi subisce delle cannonate!");

                //Passo alla prossima fase
                context.nextPhase();
            } else {
                //Torno indietro per inviare la ActivateDoubleEnginesRequest al prossimo player
                context.previousPhase();
            }
        }
        else{
            movePlayer(context, minFirePowerPlayer, -combatZone.getDaysLost());
            context.previousPhase(5); //sendDoubleEngine
            context.executePhase();
        }
    }

    public static void cannonaits(CardContext context) {
        LobbyManager lobbyManager = context.getCurrentGame();
        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        Random rand = new Random();
        final Player targetPlayer;
        if(combatZone.getLevel() ==1) {
          targetPlayer  = getNumberPlayerPairFromHashMap(context, minFirePowerCheckPairs).getValue();
        }else {
            targetPlayer = getNumberPlayerPairFromHashMap(context, minCrewMembersCheckPairs).getValue();
        }
        int diceRoll;
        String message;
        for (Projectile projectile : combatZone.getProjectiles()) {
            diceRoll = rand.nextInt(2, 13);

            message = "Stai per essere colpito da un " + projectile.getType().name() + " da " + projectile.getDirection().name() + ", indice " + diceRoll + "!";
            sendGameMessage(context, targetPlayer, message);

            if (targetPlayer.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null) { //Se il proiettile va a colpire un componente, vediamo se il player può proteggersi
                if (projectile.getSize() == ProjectileSize.Little && playerCanDefendThemselvesWithAShield(targetPlayer, projectile)) { //Se il proiettile è piccolo si possono attivare gli scudi, se ne esistono orientati correttamente
                    message = "Però puoi proteggerti con uno scudo!";
                    sendGameMessage(context, targetPlayer, message);
                    sendMessage(context, targetPlayer, new ActivateComponentRequest(ActivatableComponent.Shield));
                } else if (projectile.getSize() == ProjectileSize.Big && projectile.getType() == ProjectileType.Meteor) { //Se il proiettile è una meteora grande si possono attivare cannoni doppi
                    //Se nessun cannone punta verso il meteorite chiedo l'attivazione di un CannoneDoppio, se esiste
                    if (playerCanDefendThemselvesWithASingleCannon(targetPlayer, projectile, diceRoll)) {
                        message = "Ti proteggerà un cannone singolo!";
                        sendGameMessage(context, targetPlayer, message);
                    } else if (playerCanDefendThemselvesWithADoubleCannon(targetPlayer, projectile, diceRoll)) {
                        message = "Però puoi proteggerti con un cannone doppio!";
                        sendGameMessage(context, targetPlayer, message);
                        sendMessage(context, targetPlayer, new ActivateComponentRequest(ActivatableComponent.DoubleCannon));
                    }
                }
            }

            lobbyManager.getGameController().reactToProjectile(targetPlayer, projectile, diceRoll);
            ShipUpdate shipUpdate = new ShipUpdate(targetPlayer.getShip(), targetPlayer.getNickName());
            broadcast(context, shipUpdate);
        }
        //Cleanup

        if(combatZone.getLevel() ==1) {
            minFirePowerCheckPairs.remove(context.getCurrentGame());
        }
        else{
            minCrewMembersCheckPairs.remove(context.getCurrentGame());
        }

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }

    //Shared methods
    private static <T extends Number> Pair<T, Player> getNumberPlayerPairFromHashMap(CardContext context, HashMap<LobbyManager, Pair<T, Player>> map) {
        LobbyManager game = context.getCurrentGame();

        Pair<T, Player> integerPlayerPair;
        if (map.get(game) != null) {
            integerPlayerPair = map.get(game);
        } else {
            integerPlayerPair = new Pair<>(null, null);
            map.put(game, integerPlayerPair);
        }

        return integerPlayerPair;
    }

    /**
     * Compares current with the value in the map. //TODO fix this description
     *
     * @param context
     * @param current
     * @param map
     * @param <T>     A subclass of {@link Number}.
     */
    private static <T extends Number> void combatZoneCompare(CardContext context, T current, HashMap<LobbyManager, Pair<T, Player>> map) {
        Pair<T, Player> pair = getNumberPlayerPairFromHashMap(context, map);

        T min = pair.getKey();
        Player minPlayer = pair.getValue();

        Player player = context.getCurrentPlayer();

        if (current.floatValue() < min.floatValue() || min.floatValue() == 0) {
            min = current;
            minPlayer = player;
            map.replace(context.getCurrentGame(), new Pair<>(min, minPlayer)); //Aggiorno pair nella hashmap
        } else if (current.floatValue() == min.floatValue()) { //Se c'è parità
            if (player.getPlacement() > minPlayer.getPlacement()) { //Il giocatore in vantaggio diventa il nuovo target
                minPlayer = player;
                map.replace(context.getCurrentGame(), new Pair<>(min, minPlayer)); //Aggiorno pair nella hashmap
            }
        }
    }
}
