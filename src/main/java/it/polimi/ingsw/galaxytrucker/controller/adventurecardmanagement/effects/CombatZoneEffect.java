package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.model.FlightBoard;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Projectile;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CombatZone;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.AskTrunkRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskTrunkResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class CombatZoneEffect {
    private final static HashMap<LobbyManager, Integer> projectileIndexes = new HashMap<>();
    private final static Random rand = new Random();
    private final static HashMap<LobbyManager, ArrayList<Ship>> trunksPerGame = new HashMap<>();

    //PHASE 1
    private final static HashMap<LobbyManager, Pair<Integer, Player>> minCrewMembersCheckPairs = new HashMap<>();
    private final static HashMap<LobbyManager,Integer> levelCombatZone = new HashMap<>();


    public static void checkLevel(CardContext context) {

        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        LobbyManager game = context.getCurrentGame();
        int level = combatZone.getLevel();
        System.out.println("Checking level " + level);
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
        System.out.println(player.getNickName() + "  minCrewMembersCheck");
        int playerCrewMembersNumber = player.getShip().getnCrew();
        combatZoneCompare(context, playerCrewMembersNumber, minCrewMembersCheckPairs);

        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minCrewMembersCheckPairs);
        Player minCrewMembersPlayer = pair.getValue();

        if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
            if(combatZone.getLevel() ==1) {

                broadcastGameMessage(context, minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");
                System.out.println(minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");
                movePlayer(context, minCrewMembersPlayer, -combatZone.getDaysLost());

                //Passo subito alla prossima fase
                context.setCurrentPlayer(context.getCurrentRankedPlayers().getFirst());
                context.nextPhase();
                context.executePhase();

                //Cleanup
                minCrewMembersCheckPairs.remove(context.getCurrentGame());
                return;
            }
            else{

                broadcastGameMessage(context, minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");
                System.out.println(minCrewMembersPlayer.getNickName() + " ha il minor numero di membri dell'equipaggio!");

                context.setCurrentPlayer(context.getCurrentRankedPlayers().getFirst());
                context.nextPhase(7);
                context.executePhase();
                minCrewMembersCheckPairs.remove(context.getCurrentGame());

                return;

            }
        }

        context.nextPlayer();
        context.executePhase();
    }


    //PHASE 2
    private final static HashMap<LobbyManager, Pair<Integer, Player>> minEnginePowerCheckPairs = new HashMap<>();

    public static void sendDoubleEnginesActivationRequest(CardContext context) {

        Player player = context.getCurrentPlayer();
        System.out.println("Player " + player.getNickName()+" sendDoubleEnginesActivationRequest");
        GameMessage gameInfo = new GameMessage(player.getNickName());
        gameInfo.setMessage("Il giocatore con la minore potenza del motore deve ricevere una penalità" );
        broadcast(context,gameInfo);

        //Controllo se il player può attivare DoubleEngines  charged==false
        if (player.getShip().getComponentPositionsFromName("DoubleEngine").stream().anyMatch(p -> !player.getShip().getComponentFromPosition(p).isCharged())) {
            ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
            context.nextPhase();
            sendMessage(context, player, activateDoubleEnginesRequest);

        } else {
            context.nextPhase();
            context.executePhase();
        }
    }

    public static void minEnginePowerCheck(CardContext context) {

        Player player = context.getCurrentPlayer();
        System.out.println(player.getNickName() + " minEnginePowerCheck");

        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        LobbyManager game = context.getCurrentGame();

        int playerEnginePower = player.getShip().calculateEnginePower();
        combatZoneCompare(context, playerEnginePower, minEnginePowerCheckPairs);

        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs);
        Player minEnginePowerPlayer = pair.getValue();

        if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
            if(levelCombatZone.get(game)==1){

            broadcastGameMessage(context, minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi perde equipaggio!");
            System.out.println(minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi perde equipaggio!");
            //Passo alla prossima fase
            context.nextPhase();}

            else{
                broadcastGameMessage(context, minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi perde merci!");
                System.out.println(minEnginePowerPlayer.getNickName() + " ha la minor potenza motrice, quindi perde merci!");
                //to do discard merci
                System.out.println("numero degli Goods nel ship  =  " +  minEnginePowerPlayer.getShip().getnGoods() );



                ArrayList<Good> removedGoods = getAndRemoveMostValuableGoods(context,player, combatZone.getGoodsLost());

                int goodsCount = removedGoods.size();
                int batteryToDiscard = combatZone.getGoodsLost() - goodsCount;

                System.out.println("removedGoods size =  " +  removedGoods.size() );

                String message;
                if (goodsCount == combatZone.getGoodsLost()) {
                    message = "[CombatZone] Ha ha! We'll steal your " + goodsCount + " most valuable goods!";
                } else if (goodsCount > 0) {
                    removeBatteries(context,player, batteryToDiscard);
                    message = "[CombatZone] We'll steal your " + goodsCount + " most valuable good(s) and " + batteryToDiscard + " battery(ies), if you have them.";
                } else {
                    removeBatteries(context, player, batteryToDiscard);
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
        System.out.println("sendDiscardCrewMembersRequest");



        CombatZone combatZone = (CombatZone) context.getAdventureCard();
        int playerCrewMembersNumber = context.getCurrentPlayer().getShip().getnCrew();
        int nCrewToBeDiscarded = Integer.min(playerCrewMembersNumber, combatZone.getCrewMembersLost());
        context.nextPhase();
        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs);
        Player minEnginePowerPlayer = pair.getValue();

        GameMessage minCrewMemberMessage = new GameMessage(" Il giocatore "+ minEnginePowerPlayer.getNickName() + " sta scegliendo di scartare gli equipaggi, attendere che completi la selezione!");
        broadcast(context, minCrewMemberMessage);

        System.out.println(" Il giocatore "+ minEnginePowerPlayer.getNickName() + " sta scegliendo di scartare gli equipaggi, attendere che completi la selezione!");

        sendMessage(context,minEnginePowerPlayer, new DiscardCrewMembersRequest(nCrewToBeDiscarded));


    }

    public static void receivedDiscardCrewMembersRequest(CardContext context) {
        System.out.println("receivedDiscardCrewMembersRequest");
        Pair<Integer, Player> pair = getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs);
        Player minEnginePowerPlayer = pair.getValue();

        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();
        discardCrewMembers(getNumberPlayerPairFromHashMap(context, minEnginePowerCheckPairs).getValue(), discardCrewMembersResponse, discardCrewMembersResponse.getHousingPositions().size());

        GameMessage minCrewMemberMessage = new GameMessage(minEnginePowerPlayer.getNickName() + " ha finito di scartare equipaggio, il gioco può continuare!");
        broadcast(context, minCrewMemberMessage);
        System.out.println(minEnginePowerPlayer.getNickName() + " ha finito di scartare equipaggio, il gioco può continuare!");

        //Cleanup
        minEnginePowerCheckPairs.remove(context.getCurrentGame());

        context.setCurrentPlayer(context.getCurrentRankedPlayers().getFirst());
        context.nextPhase();
        context.executePhase();
    }

    //PHASE 3
    private final static HashMap<LobbyManager, Pair<Float, Player>> minFirePowerCheckPairs = new HashMap<>();

    public static void sendDoubleCannonsActivationRequest(CardContext context) {

        Player player = context.getCurrentPlayer();
        System.out.println( player.getNickName() + " sendDoubleCannonsActivationRequest");
        GameMessage gameInfo = new GameMessage(player.getNickName());
        gameInfo.setMessage("Il giocatore con la minore potenza del cannon deve ricevere una penalità" );
        broadcast(context,gameInfo);

        //Controllo se il player può attivare DoubleEngines  no charged
        if (player.getShip().getComponentPositionsFromName("DoubleCannon").stream().anyMatch(p -> !player.getShip().getComponentFromPosition(p).isCharged())) {
            ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
            context.nextPhase();
            sendMessage(context, player, activateDoubleCannonsRequest);

        } else {
            context.nextPhase();
            context.executePhase();
        }
    }

    public static void minFirePowerCheck(CardContext context) {

        Player player = context.getCurrentPlayer();
        System.out.println(player.getNickName() + " minFirePowerCheck");
        CombatZone combatZone = (CombatZone) context.getAdventureCard();

        LobbyManager game = context.getCurrentGame();
        float playerFirePower = player.getShip().calculateFirePower();
        combatZoneCompare(context, playerFirePower, minFirePowerCheckPairs);

        Pair<Float, Player> pair = getNumberPlayerPairFromHashMap(context, minFirePowerCheckPairs);
        Player minFirePowerPlayer = pair.getValue();
        if(levelCombatZone.get(game) == 1) {

            if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
                broadcastGameMessage(context, minFirePowerPlayer.getNickName() + " ha la minor potenza di fuoco, quindi subisce delle cannonate!");
                System.out.println(minFirePowerPlayer.getNickName() + " ha la minor potenza di fuoco, quindi subisce delle cannonate!");

                //Passo alla prossima fase
                context.nextPhase();
                context.executePhase();
            } else {
                //Torno indietro per inviare la ActivateDoubleEnginesRequest al prossimo player
                context.nextPlayer();
                context.previousPhase();
                context.executePhase();
            }
        }
        else{
            if (context.getCurrentPlayer() == context.getCurrentRankedPlayers().getLast()) {
                // solo per test
                FlightBoard flightBoard = game.getRealGame().getFlightBoard();
                int playerStep  = flightBoard.getPlayerPosition(minFirePowerPlayer.getColor());
                 System.out.println( minFirePowerPlayer.getNickName() + " PlayerStep Before Move: " + playerStep);
                //
                movePlayer(context, minFirePowerPlayer, -combatZone.getDaysLost());
                //solo per test
                playerStep  = flightBoard.getPlayerPosition(minFirePowerPlayer.getColor());
                System.out.println("PlayerStep After Move: " + playerStep);
                //

                broadcastGameMessage(context,"il giocatore"+ minFirePowerPlayer.getNickName()+" ha la potenza di fuoco minima, è retrocesso di "+ combatZone.getDaysLost()+" passi.");
                context.setCurrentPlayer(context.getCurrentRankedPlayers().getFirst());
                context.previousPhase(5); //sendDoubleEngine
                context.executePhase();
            }
            else {
                context.previousPhase();
                context.nextPlayer();
                context.executePhase();
            }
        }
    }

    public static void cannonaitsStart(CardContext context) {
        System.out.println("cannonaitsStart");

        LobbyManager lobbyManager = context.getCurrentGame();
        CombatZone combatZone = (CombatZone) context.getAdventureCard();

//        Random rand = new Random();
        final Player targetPlayer;

        if(combatZone.getLevel() ==1) {
            targetPlayer  = getNumberPlayerPairFromHashMap(context, minFirePowerCheckPairs).getValue();
        }else {
            targetPlayer = getNumberPlayerPairFromHashMap(context, minCrewMembersCheckPairs).getValue();
        }

        context.setCurrentPlayer(targetPlayer);


        projectileIndexes.putIfAbsent(lobbyManager, 0);
        int projectileIndex = projectileIndexes.get(lobbyManager);

        if (projectileIndex == combatZone.getProjectiles().size()) {

            projectileIndexes.remove(lobbyManager);
            context.goToEndPhase();
            context.executePhase();
            return;
        }
        Projectile projectile = combatZone.getProjectiles().get(projectileIndex);

        if (projectileIndex < combatZone.getProjectiles().size()) {
            //Fare player.sendMessage(new YourTurnStart());

            ActivateComponentRequest activateShieldRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
            context.nextPhase();

            if (projectile.getSize().equals(ProjectileSize.Little) && playerCanDefendThemselvesWithAShield(targetPlayer, projectile)) {
                sendMessage(context, targetPlayer, activateShieldRequest);
            } else {
                context.executePhase();
            }
        }
    }

    public static void cannonaitsFire(CardContext context) {

        LobbyManager game = context.getCurrentGame();

        CombatZone combatZone = (CombatZone) context.getAdventureCard();

        Player player = context.getCurrentPlayer();
        Ship playerShip = player.getShip();
        System.out.println();
        ShipPrintUtils.printShip(playerShip);
        System.out.println();

        System.out.println(player.getNickName() + " DEBUG: cannonaitsFire");
        int projectileIndex = projectileIndexes.getOrDefault(game,0);


        Projectile projectile = combatZone.getProjectiles().get(projectileIndex);


        int viewDiceRoll = rand.nextInt(2, 13);

        int diceRoll = getCorrectedDiceRoll(viewDiceRoll, projectile.getDirection());

        broadcastGameMessage(context,player.getNickName() + "  sta per essere colpito da un " + projectile.getType().name() +" "+ projectile.getSize() +" da " + projectile.getDirection().name() + ", indice " + viewDiceRoll + "!");

        System.out.println("Stai per essere colpito da un " + projectile.getType().name()  +" "+ projectile.getSize() +" da " + projectile.getDirection().name() + ", indice " + viewDiceRoll + "!");

        projectileIndexes.put(game, projectileIndex + 1);

        Tile destroyedTile = game.getGameController().reactToProjectile(player, projectile, diceRoll);
        ShipPrintUtils.printShip(playerShip);
        resetShield(player);
        broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));

        if (destroyedTile != null) {
            ArrayList<Ship> tronconi;

//            se ho eliminato una tile vedo se ho creato dei tronconi
            tronconi = player.getShip().getTronc();
            trunksPerGame.put(game, tronconi);

            if (tronconi.size() > 1) {
                System.out.println("in tronconi size()>1");
                System.out.println(player.getNickName() + " size tronconi " + tronconi.size());
                //se ho creato nuovi tronconi chiedo quale tenere
                AskTrunkRequest askTrunkRequest = new AskTrunkRequest(tronconi);
                context.nextPhase();
                sendMessage(context, player, askTrunkRequest);

            } else {
                context.previousPhase();
                context.executePhase();
            }

        }
        else{
            context.previousPhase();
            context.executePhase();
        }

    }

    public static void cannonaitsTrunks(CardContext context) {
        System.out.println("DEBUG: cannonaitsTrunks");
        LobbyManager game = context.getCurrentGame();
        AskTrunkResponse askTrunkResponse = (AskTrunkResponse) context.getIncomingNetworkMessage();
        int indexTrunk = askTrunkResponse.getTrunkIndex();
        Player player = context.getCurrentPlayer();

        Ship newShip = trunksPerGame.get(game).get(indexTrunk);
        player.replaceShip(newShip);

        //invio a tutti la nuova nave
        broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));

        context.previousPhase(2); //cannonaitsStart
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

        if (min == null || current.floatValue() < min.floatValue() || min.floatValue() == 0) {
            map.put(context.getCurrentGame(), new Pair<>(current, player));
        } else if (current.floatValue() == min.floatValue()) {
            if (minPlayer == null || player.getPlacement() > minPlayer.getPlacement()) {
                map.put(context.getCurrentGame(), new Pair<>(current, player));
            }
        }
    }
}
