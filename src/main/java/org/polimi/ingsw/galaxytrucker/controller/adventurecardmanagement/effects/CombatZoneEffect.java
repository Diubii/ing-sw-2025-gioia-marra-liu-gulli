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
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.HashMap;
import java.util.Random;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class CombatZoneEffect {
    //PHASE 1
    private final static HashMap<LobbyManager, Pair<Integer, Player>> minCrewMembersCheckPairs = new HashMap<>();

    public static void minCrewMembersCheck(CardContext context) {
        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

        int playerCrewMembersNumber = player.getShip().getnCrew();
        combatZoneCompare(context, playerCrewMembersNumber, minCrewMembersCheckPairs);

        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minCrewMembersCheckPairs);
        Player minCrewMembersPlayer = pair.getValue();

        if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
            broadcastGameMessage(context, minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");
            movePlayer(context, minCrewMembersPlayer, -combatZone.getDaysLost());

            //Passo subito alla prossima fase
            context.nextPhase();

            //Cleanup
            minCrewMembersCheckPairs.remove(context.getCurrentGame());
        }

        context.nextPlayer();
        context.executePhase();
    }


    //PHASE 2
    private final static HashMap<LobbyManager, Pair<Integer, Player>> minEnginePowerCheckPairs = new HashMap<>();

    public static void sendDoubleEnginesActivationRequest(CardContext context) {
        Player player = context.getCurrentPlayer();

        //Controllo se il player può attivare DoubleEngines
        if (player.getShip().getComponentPositionsFromName("DoubleEngine").stream().anyMatch(p -> player.getShip().getComponentFromPosition(p).isCharged())) {
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

        int playerEnginePower = player.getShip().calculateEnginePower();
        combatZoneCompare(context, playerEnginePower, minEnginePowerCheckPairs);

        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs);
        Player minEnginePowerPlayer = pair.getValue();

        if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
            broadcastGameMessage(context, minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi perde equipaggio!");

            //Passo alla prossima fase
            context.nextPhase();
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

        //Controllo se il player può attivare DoubleEngines
        if (player.getShip().getComponentPositionsFromName("DoubleCannon").stream().anyMatch(p -> player.getShip().getComponentFromPosition(p).isCharged())) {
            ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
            sendMessage(context, player, activateDoubleCannonsRequest);
            context.nextPhase();
        } else {
            context.nextPhase();
            context.executePhase();
        }
    }

    public static void minFirePowerCheck(CardContext context) {
        Player player = context.getCurrentPlayer();

        float playerFirePower = player.getShip().calculateFirePower();
        combatZoneCompare(context, playerFirePower, minFirePowerCheckPairs);

        Pair<Float, Player> pair = getNumberPlayerPairFromHashMap(context, minFirePowerCheckPairs);
        Player minFirePowerPlayer = pair.getValue();

        if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
            broadcastGameMessage(context, minFirePowerPlayer.getNickName() + " ha la minor potenza di fuoco, quindi subisce delle cannonate!");

            //Passo alla prossima fase
            context.nextPhase();
        } else {
            //Torno indietro per inviare la ActivateDoubleEnginesRequest al prossimo player
            context.previousPhase();
        }
    }

    public static void cannonaits(CardContext context) {
        LobbyManager lobbyManager = context.getCurrentGame();
        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        Random rand = new Random();
        final Player targetPlayer = getNumberPlayerPairFromHashMap(context, minFirePowerCheckPairs).getValue();
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
        minFirePowerCheckPairs.remove(context.getCurrentGame());

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
