package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.Player;
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
        if (smugglers.getFirePower() > player.getShip().calculateFirePower()){
            GameMessage personalMessage = new GameMessage("The Smugglers are going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has less FirePower than the Smugglers!");
            broadcastExcept(context, info, player);
            //gestisco la sua sconfitta
            //rimuovo le 2 merci piu preziose


            GameMessage info2 = new GameMessage();
            //info2.setIsTurn(true);


            ArrayList<Good> twoGoods = Util.getMostValuableGoods(player.getShip());
            //ho rimosso le merci piu importanti
            int nullGoods = 0;
            for (Good good : twoGoods) {
                if (good == null) nullGoods++;
            }


            //in base alle merci che ho levato decido quante batterie rimuovere
            if (nullGoods == 1) {Util.removeTwoBatteries(player.getShip(), true); info2.setMessage("Removed your most valuable good and one battery (if it's present)");}
            if (nullGoods == 2) {Util.removeTwoBatteries(player.getShip(), false); info2.setMessage("Could not remove Goods, batteries were taken instead");}
            if (nullGoods == 0) info2.setMessage("Removed your two most valuable goods!");

            game.getPlayerHandlers().get(player.getNickName()).sendMessage(info2);

            context.previousPhase();
            context.nextPlayer();
            context.executePhase();
        }
        else if (smugglers.getFirePower() < player.getShip().calculateFirePower()){
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
            GameMessage personalMessage = new GameMessage("The Smugglers are not going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);

            context.previousPhase();
            context.nextPlayer();
            context.executePhase();
        }

        if(context.currentPlayerIsLast()){
            //Execute CommonEffects::end
            context.goToEndPhase();
            context.executePhase();
        }
    }

    public static void receivedRewardsCollectionResponse(CardContext context){
        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();

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
        Smugglers smugglers = (Smugglers) context.getAdventureCard();
        movePlayer(context, context.getCurrentPlayer(), -smugglers.getDaysLost());

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
