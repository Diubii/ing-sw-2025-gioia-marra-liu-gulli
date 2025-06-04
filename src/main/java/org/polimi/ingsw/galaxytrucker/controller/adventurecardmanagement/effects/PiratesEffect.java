package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.Pirates;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.AskTrunkRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CollectRewardsRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskTrunkResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CollectRewardsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public class PiratesEffect {
    private final static HashMap<LobbyManager, ArrayList<Player>> losersPerGame = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> projectileIndexes = new HashMap<>();
    private final static HashMap<LobbyManager, ArrayList<Ship>> trunksPerGame = new HashMap<>();
    private final static HashMap<LobbyManager, Boolean> rewardTaken  = new HashMap<>();
    private final static HashMap <LobbyManager, Integer> projectileVictimIndexes = new HashMap<>();

    private final static Random rand = new Random();

    public static void firePowerCheck(CardContext context) {

        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

        System.out.println(player.getNickName()+ " DEBUG: firePowerCheck");
        float piratesFirePower = pirates.getFirePower();
        float playerFirePower = player.getShip().calculateFirePower();

        if (piratesFirePower > playerFirePower) {
            handlePiratesWin(context);

        } else if (piratesFirePower < playerFirePower) {
            handlePlayerWin(context);

            rewardTaken.computeIfAbsent(game, _ -> false);
            //Controllo prima se la ricompensa è già stata presa da un altro giocatore.

            if(!rewardTaken.get(game)) {
                //Andiamo a PiratesEffect::receivedRewardsCollectionResponse una volta ricevuta la risposta
                context.nextPhase();
                CollectRewardsRequest collectRewardsRequest = new CollectRewardsRequest();
                sendMessage(context, context.getCurrentPlayer(), collectRewardsRequest);
                return;
            }
            //Se la ricompensa è già stata riscossa, salto il blocco if-else
            // e passo direttamente al controllo se il giocatore è l’ultimo

        } else if (piratesFirePower == playerFirePower) {
            handleTie(context);
        }

        if(context.currentPlayerIsLast()) {
            context.nextPhase(2); //cannonaitsStart
        }
        else{
            context.nextPlayer();
            context.previousPhase();
        }
        context.executePhase();
    }

    private static void handlePlayerWin(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

        System.out.println("DEBUG : firePower pirates.getFirePower() < player.getShip().calculateFirePower()");
        GameMessage personalMessage = new GameMessage("The Pirates are not going to haunt you!"); //personalMessage.setIsTurn(true);
        game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
        GameMessage info = new GameMessage();
        info.setMessage(player.getNickName() + " has defeated the Pirates!");
        broadcastExcept(context, info, player);

    }
    private static void handlePiratesWin(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

        System.out.println("DEBUG: firePower pirates.getFirePower() > player.getShip().calculateFirePower()");
        losersPerGame.computeIfAbsent(game, _ -> new ArrayList<>());
        losersPerGame.get(game).add(player);

        //dopo averlo aggiunto si notifica
        GameMessage personalMessage = new GameMessage("The Pirates are going to haunt you!"); //personalMessage.setIsTurn(true);
        game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
        GameMessage info = new GameMessage();
        info.setMessage(player.getNickName() + " has less FirePower than the Pirates!");
        broadcastExcept(context, info, player);

    }
    private static void handleTie(CardContext context){
        Player player = context.getCurrentPlayer();
        System.out.println("DEBUG: firePower pirates.getFirePower() == player.getShip().calculateFirePower()");
        GameMessage personalMessage = new GameMessage("The Pirates are not going to haunt you!"); //personalMessage.setIsTurn(true);
        sendMessage(context, player, personalMessage);

    }

    public static void receivedRewardsCollectionResponse(CardContext context) {
        System.out.println("DEBUG: receivedRewardsCollectionResponse");


        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
        Pirates pirates = (Pirates) context.getAdventureCard();
        LobbyManager game = context.getCurrentGame();


        if (collectRewardsResponse.doesWantToCollect() && !rewardTaken.get(game)) {
            //aggiorno i crediti
            player.addCredits(pirates.getCredits());
            //lo muovo indietro
            movePlayer(context, player, -pirates.getDaysLost());
            rewardTaken.put(game, true);
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose to collect the rewards!"), player);
        }
        else {
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose NOT to collect the rewards!"), player);
        }

        if(context.currentPlayerIsLast()) {
                context.nextPhase(1); //cannonaitsStart
        }
        else{
            context.nextPlayer();
            context.previousPhase(2);
        }
        context.executePhase();
    }



    public static void cannonaitsStart(CardContext context) {

        //dopo aver finito chiedo di tirare i dadi al primo, ma per ora per la Tui facciamo sia automatico
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();


        ArrayList<Player> losers = losersPerGame.getOrDefault(game,null);

        if (losers == null || losers.isEmpty()) { //Se abbiamo finito vado alla fase finale
            losersPerGame.remove(game);
            projectileIndexes.remove(game);
            trunksPerGame.remove(game);
            rewardTaken.remove(game);
            projectileVictimIndexes.remove(game);
            context.goToEndPhase();
            context.executePhase();
            return;
        }

        projectileIndexes.putIfAbsent(game, 0);

        int projectileIndex = projectileIndexes.get(game);

        if (projectileIndex == pirates.getCannonFires().size()) {
            losersPerGame.remove(game);
            projectileIndexes.remove(game);
            trunksPerGame.remove(game);
            rewardTaken.remove(game);
            projectileVictimIndexes.remove(game);

            context.goToEndPhase();
            context.executePhase();
                return;
        }
        projectileVictimIndexes.putIfAbsent(game, 0);

        int projectileVictimIndex = projectileVictimIndexes.get(game);
        Player player = losers.get(projectileVictimIndex);
        context.setCurrentPlayer(player);
        System.out.println(player.getNickName() + " DEBUG: cannonaitsStart");

        Projectile projectile = pirates.getCannonFires().get(projectileIndex);

        if (projectileIndex < pirates.getCannonFires().size()) {
            //Fare player.sendMessage(new YourTurnStart());
            ActivateComponentRequest activateShieldRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
            context.nextPhase();
            if (projectile.getSize().equals(ProjectileSize.Little) && projectile.getType().equals(ProjectileType.CannonFire)) {
                sendMessage(context, player, activateShieldRequest);
            } else {
                context.executePhase();
            }
        }

    }

    @NeedsToBeCompleted
    //funzione get tronconi
    public static void cannonaitsFire(CardContext context) {

        LobbyManager game = context.getCurrentGame();



        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
        System.out.println(player.getNickName() + " DEBUG: cannonaitsFire");
        int projectileIndex = projectileIndexes.getOrDefault(game,0);
        int victimIdx = projectileVictimIndexes.getOrDefault(game, 0);

        Projectile projectile = pirates.getCannonFires().get(projectileIndex);

        int shiftDiceRoll = rand.nextInt(2, 13);
        int diceRoll = getCorrectedDiceRoll( shiftDiceRoll,projectile.getDirection());


        if (losersPerGame.get(game).size() == victimIdx +1 ) {
            projectileIndexes.put(game, projectileIndex + 1);
            projectileVictimIndexes.put(game, 0);
        }
        else {
            projectileVictimIndexes.put(game, victimIdx + 1);
        }


        //TEST
//        int diceRoll = 0;
//        switch (projectile.getDirection()) {
//            case UP, DOWN -> diceRoll = 3;
//            case LEFT, RIGHT -> diceRoll = 2;
//        }


        broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));
        Tile destroyedTile = game.getGameController().reactToProjectile(player, projectile, diceRoll);
        if (destroyedTile != null) {
            ArrayList<Ship> tronconi;

//            se ho eliminato una tile vedo se ho creato dei tronconi
//            tronconi = player.getShip().getTronc();

            tronconi = new ArrayList<Ship>();
            tronconi.add(player.getShip());
            trunksPerGame.put(game, tronconi);

            if (tronconi.size() > 1) {
                System.out.println("in tronconi.size()>1");
                //se ho creato nuovi tronconi chiedo quale tenere
                AskTrunkRequest askTrunkRequest = new AskTrunkRequest(tronconi);
                context.nextPhase();
                sendMessage(context, player, askTrunkRequest);

            } else {
                System.out.println("Player " + player.getNickName() + "  in else!");
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
    //fare  player.sendMessage(new YourTurnEnd());
}
