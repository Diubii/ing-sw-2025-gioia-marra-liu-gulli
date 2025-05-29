package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.Smugglers;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CollectRewardsRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CollectRewardsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;

import java.util.ArrayList;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public class SmugglersEffect {

    public static void firePowerCheck(CardContext context){

        LobbyManager game = context.getCurrentGame();
        Smugglers smugglers = (Smugglers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();


        System.out.println( player.getNickName() + "  Debug: Fire Power Check");
        if (smugglers.getFirePower() > player.getShip().calculateFirePower()){
            System.out.println( player.getNickName() + "  Debug: smugglers.getFirePower() > player.getShip().calculateFirePower())");
            GameMessage personalMessage = new GameMessage("The Smugglers are going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has less FirePower than the Smugglers!");
            broadcastExcept(context, info, player);
            //gestisco la sua sconfitta
            //rimuovo le 2 merci piu preziose


            GameMessage info2 = new GameMessage();
            //info2.setIsTurn(true);


            ArrayList<Good> mostValuableGoods = Util.getMostValuableGoods(player.getShip());
            //ho rimosso le merci piu importanti
            int numberOfGoods = mostValuableGoods.size();


            //in base alle merci che ho levato decido quante batterie rimuovere
            if (numberOfGoods == 2) info2.setMessage("[Smugglers] Ha ha! We'll steal your two most valuable goods!");
            if (numberOfGoods == 1) {
                Util.removeTwoBatteries(player.getShip(), true);
                info2.setMessage("[Smugglers] We'll steal your most valuable good and one battery, if you have it.");
            }
            if (numberOfGoods == 0) {
                Util.removeTwoBatteries(player.getShip(), false);
                info2.setMessage("[Smugglers] You don't have any goods, so we'll steal two of your batteries! Well, if you have any, poor fella.");
            }

            game.getPlayerHandlers().get(player.getNickName()).sendMessage(info2);

            context.previousPhase();
        }
        else if (smugglers.getFirePower() < player.getShip().calculateFirePower()){
            System.out.println(player.getNickName() + "DEBUG: smugglers.getFirePower() < player.getShip().calculateFirePower())");
            GameMessage personalMessage = new GameMessage("You won against the Smugglers!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has defeated the Smugglers!");
            broadcastExcept(context, info, player);

            //chiedo se vuole accettare la ricompensa o no, in caso l'accettasse lo muovo indietro e aggiorno i crediti
            CollectRewardsRequest collectRewardsRequest = new CollectRewardsRequest();
            sendMessage(context, player, collectRewardsRequest);

            context.nextPhase();
            return;
        }
        else if (smugglers.getFirePower() == player.getShip().calculateFirePower()){
            System.out.println(player.getNickName() + " DEBUG: smugglers.getFirePower() == player.getShip().calculateFirePower(");
            GameMessage personalMessage = new GameMessage("The Smugglers are not going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);

            context.previousPhase();
        }

        if(context.currentPlayerIsLast()){
            //Execute CommonEffects::end
            context.goToEndPhase();
        }

        context.nextPlayer();
        context.executePhase();
    }

    public static void receivedRewardsCollectionResponse(CardContext context){

        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();

        System.out.println(player.getNickName() + "  Debug: Received rewards collection response");
        if(collectRewardsResponse.doesWantToCollect()) {
            context.incrementExpectedNumberOfNetworkMessages(NetworkMessageType.ShipUpdate);
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose to collect the rewards!"), player);
            context.nextPhase();
        }
        else{
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose NOT to collect the rewards!"), player);

            //Execute CommonEffects::end
            context.goToEndPhase();
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
