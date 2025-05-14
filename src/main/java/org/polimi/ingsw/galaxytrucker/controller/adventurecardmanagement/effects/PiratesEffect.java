package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

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

    public static void firePowerCheck(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
        if (pirates.getFirePower() > player.getShip().calculateFirePower()) {
            losersPerGame.computeIfAbsent(game, _ -> new ArrayList<>());
            losersPerGame.get(game).add(player);

            //dopo averlo aggiunto si notifica
            GameMessage personalMessage = new GameMessage("The Pirates are going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage();
            info.setMessage(player.getNickName() + " has less FirePower than the Pirates!");
            broadcastExcept(context, info, player);

        } else if (pirates.getFirePower() < player.getShip().calculateFirePower()) {
            GameMessage personalMessage = new GameMessage("The Pirates are not going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage();
            info.setMessage(player.getNickName() + " has defeated the Pirates!");
            broadcastExcept(context, info, player);


            //chiedo se vuole accettare la ricompensa o no, in caso l'accettasse lo muovo indietro e aggiorno i crediti
            CollectRewardsRequest collectRewardsRequest = new CollectRewardsRequest();
            sendMessage(context, context.getCurrentPlayer(), collectRewardsRequest);

            //Andiamo a PiratesEffect::receivedRewardsCollectionResponse una volta ricevuta la risposta
            context.nextPhase();
            return;
        } else if (pirates.getFirePower() == player.getShip().calculateFirePower()) {
            GameMessage personalMessage = new GameMessage("The Pirates are not going to haunt you!"); //personalMessage.setIsTurn(true);
            sendMessage(context, player, personalMessage);
        }

        if (!context.currentPlayerIsLast()) {
            context.nextPlayer(); //Iteriamo
            context.previousPhase();
            context.executePhase();
        } else if (context.currentPlayerIsLast() && (losersPerGame.get(game) == null || losersPerGame.get(game).isEmpty())) {
            context.goToEndPhase();
            context.executePhase();
        } else { //Cannonate
            context.nextPhase(2);
            context.executePhase();
        }
    }

    public static void receivedRewardsCollectionResponse(CardContext context) {
        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
        Pirates pirates = (Pirates) context.getAdventureCard();

        if (collectRewardsResponse.doesWantToCollect()) {
            //aggiorno i crediti
            player.addCredits(pirates.getCredits());
            //lo muovo indietro
            movePlayer(context, player, -pirates.getDaysLost());
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose to collect the rewards!"), player);
        } else {
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose NOT to collect the rewards!"), player);
        }

        //Passiamo alle cannonate
        context.nextPhase();
        context.executePhase();
    }

    public static void cannonaitsStart(CardContext context) {
        //dopo aver finito chiedo di tirare i dadi al primo, ma per ora per la Tui facciamo sia automatico
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        ArrayList<Player> losers = losersPerGame.get(game);

        if (losers == null || losers.isEmpty()) { //Se abbiamo finito vado alla fase finale
            context.goToEndPhase();
            context.executePhase();
            return;
        }

        Player player = losersPerGame.get(game).getFirst();

        projectileIndexes.putIfAbsent(game, 0);

        int projectileIndex = projectileIndexes.get(game);
        Projectile projectile = pirates.getCannonFires().get(projectileIndex);

        //Fare player.sendMessage(new YourTurnStart());

        ActivateComponentRequest activateShieldRequest = new ActivateComponentRequest(ActivatableComponent.Shield);

        context.nextPhase();
        if (projectile.getSize().equals(ProjectileSize.Little) && projectile.getType().equals(ProjectileType.CannonFire)) {
            sendMessage(context, player, activateShieldRequest);
            return;
        }
        context.executePhase();
    }

    public static void cannonaitsFire(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = losersPerGame.get(game).getFirst();

        int projectileIndex = projectileIndexes.get(game);
        Projectile projectile = pirates.getCannonFires().get(projectileIndex);

        Random rand = new Random();
        int diceRoll = rand.nextInt(2, 13);

        switch (projectile.getDirection()) {
            case UP, DOWN -> diceRoll -= 4;
            case LEFT, RIGHT -> diceRoll -= 5;
        }

        //TEST
//        int diceRoll = 0;
//        switch (projectile.getDirection()) {
//            case UP, DOWN -> diceRoll = 3;
//            case LEFT, RIGHT -> diceRoll = 2;
//        }

        Tile destroyedTile = game.getGameController().reactToProjectile(player, projectile, diceRoll);

        if (destroyedTile != null) {
            ArrayList<Ship> tronconi;
            //se ho eliminato una tile vedo se ho creato dei tronconi
            tronconi = player.getShip().getTronc();
            trunksPerGame.put(game, tronconi);

            if (tronconi.size() > 1) {
                //se ho creato nuovi tronconi chiedo quale tenere
                AskTrunkRequest askTrunkRequest = new AskTrunkRequest(tronconi);
                sendMessage(context, player, askTrunkRequest);
                context.nextPhase();
            } else {
                losersPerGame.get(game).remove(player);
                context.previousPhase();
                context.executePhase();
            }

            broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));
        }
    }

    public static void cannonaitsTrunks(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        AskTrunkResponse askTrunkResponse = (AskTrunkResponse) context.getIncomingNetworkMessage();
        ArrayList<Ship> tronconi = trunksPerGame.get(game);
        Player player = losersPerGame.get(game).getFirst();

        Ship newShip = tronconi.get(askTrunkResponse.getTrunkIndex());
        player.replaceShip(newShip);
        //invio a tutti la nuova nave
        broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));

        context.previousPhase(2); //cannonaitsStart
        context.executePhase();
    }
    //fare  player.sendMessage(new YourTurnEnd());
}
