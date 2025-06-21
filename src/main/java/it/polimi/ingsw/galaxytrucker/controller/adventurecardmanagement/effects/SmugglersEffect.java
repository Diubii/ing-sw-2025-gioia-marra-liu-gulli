package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Smugglers;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CollectRewardsRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CollectRewardsResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public class SmugglersEffect {

    public static void firePowerCheck(CardContext context){

        LobbyManager game = context.getCurrentGame();
        Smugglers smugglers = (Smugglers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

        float smugglerFirePower = smugglers.getFirePower();
        float playerFirePower =  player.getShip().calculateFirePower();
        resetDoubleCannon(player);

        System.out.println( player.getNickName() + "  Debug: Fire Power Check");
        ShipUpdate shipUpdate = new ShipUpdate(player.getShip(),player.getNickName());
        broadcast(context, shipUpdate);

        if (smugglerFirePower> playerFirePower){
            handleSmugglersWin(context,game,player,smugglers);
            context.previousPhase();
        }
        else if (smugglerFirePower < playerFirePower){
            context.nextPhase();
            handlePlayerWin(context, game, player);

            return;
        }
        else  {
            context.previousPhase();
            handleTie( game, player);

        }
//(smugglerFirePower > playerFirePower) ||(smugglerFirePower == playerFirePower)
        if(context.currentPlayerIsLast()){
            //Execute CommonEffects::end
            context.goToEndPhase();
        }
        else {
            context.nextPlayer();
        }

        context.executePhase();
    }

    private static void handleSmugglersWin(CardContext context, LobbyManager game, Player player, Smugglers smugglers) {
        game.getPlayerHandlers().get(player.getNickName())
                .sendMessage(new GameMessage("The Smugglers are going to haunt you!"));

        GameMessage broadcast = new GameMessage(player.getNickName() + " has less FirePower than the Smugglers!");
        broadcastExcept(context, broadcast, player);

        ArrayList<Good> removedGoods = getAndRemoveMostValuableGoods(context, player, smugglers.getPenalty());
        int goodsCount = removedGoods.size();
        int batteryToDiscard = smugglers.getPenalty() - goodsCount;

        String message;
        if (goodsCount == smugglers.getPenalty()) {
            message = "[Smugglers] Ha ha! We'll steal your " + goodsCount + " most valuable goods!";
        } else if (goodsCount > 0) {
            removeBatteries(context, player, batteryToDiscard);
            message = "[Smugglers] We'll steal your " + goodsCount + " most valuable good(s) and " + batteryToDiscard + " battery(ies), if you have them.";
        } else {
            removeBatteries(context,player, batteryToDiscard);
            message = "[Smugglers] You don't have any goods, so we'll steal " + batteryToDiscard + " of your batteries! Well, if you have any, poor fella.";
        }

        GameMessage personalInfo = new GameMessage(message);
        game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalInfo);
    }

    private static void handlePlayerWin(CardContext context, LobbyManager game, Player player) {
        game.getPlayerHandlers().get(player.getNickName())
                .sendMessage(new GameMessage("You won against the Smugglers!"));

        GameMessage broadcast = new GameMessage(player.getNickName() + " has defeated the Smugglers!");
        broadcastExcept(context, broadcast, player);

        CollectRewardsRequest rewardRequest = new CollectRewardsRequest();
        sendMessage(context, player, rewardRequest);
    }

    private static void handleTie( LobbyManager game, Player player) {
        game.getPlayerHandlers().get(player.getNickName())
                .sendMessage(new GameMessage("The Smugglers are not going to haunt you!"));
    }
    public static void receivedRewardsCollectionResponse(CardContext context){

        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
        System.out.println(player.getNickName() + "  Debug: Received rewards collection response");

        if(collectRewardsResponse.doesWantToCollect()) {
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose to collect the rewards!"), player);
            context.nextPhase();
            sendMessage(context, player, new ShipUpdate(player.getShip(), player.getNickName()));

        }
        else{
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose NOT to collect the rewards!"), player);

            if(context.currentPlayerIsLast()){
                //Execute CommonEffects::end
                context.goToEndPhase();
            }
            else {
                context.nextPlayer();
                context.previousPhase(1);
            }
            //Execute CommonEffects::end
            context.executePhase();
        }
    }

    public static void receivedShipUpdate(CardContext context){
        Player currentPlayer = context.getCurrentPlayer();
        System.out.println(currentPlayer.getNickName() + "  Debug: Received ship update response");

        Smugglers smugglers = (Smugglers) context.getAdventureCard();
        movePlayer(context, context.getCurrentPlayer(), -smugglers.getDaysLost());

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
