package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.Slavers;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CollectRewardsRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CollectRewardsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public class SlaversEffect {

    public static void firePowerCheck(CardContext context){
        LobbyManager game = context.getCurrentGame();
        Slavers slavers = (Slavers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
        if (slavers.getFirePower() > player.getShip().calculateFirePower()){
            GameMessage personalMessage = new GameMessage("The Slavers are going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has less FirePower than the Slavers!");
            broadcastExcept(context, info, player);
            //gestisco la sua sconfitta

            DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(slavers.getPenalty());
            sendMessage(context, player, discardCrewMembersRequest);


            //Chiediamo l'attivazione di CommonEffects::sendDoubleEnginesActivationRequest al prossimo
            context.nextPhase();
        }
        else if (slavers.getFirePower() < player.getShip().calculateFirePower()){
            GameMessage personalMessage = new GameMessage("You won against the Slavers!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has defeated the Slavers!");
            broadcastExcept(context, info, player);

            //chiedo se vuole accettare la ricompensa o no, in caso l'accettasse lo muovo indietro e aggiorno i crediti
            CollectRewardsRequest collectRewardsRequest = new CollectRewardsRequest();
            sendMessage(context, player, collectRewardsRequest);

            context.nextPhase();
            return;
        }
        else if (slavers.getFirePower() == player.getShip().calculateFirePower()){
            GameMessage personalMessage = new GameMessage("The Slavers are not going to haunt you!"); //personalMessage.setIsTurn(true);
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

    public static void receivedDiscardCrewMembersResponse(CardContext context){
        Slavers slavers = (Slavers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();

        discardCrewMembers(player, discardCrewMembersResponse, slavers.getPenalty());

        //Broadcasto nuova nave
        broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));

        context.previousPhase();
        context.nextPlayer();
        context.executePhase();
    }

    public static void receivedRewardsCollectionResponse(CardContext context){
        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
        Slavers slavers = (Slavers) context.getAdventureCard();

        if(collectRewardsResponse.doesWantToCollect()) {
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose to collect the rewards!"), player);

            player.addCredits(slavers.getCredits());
            movePlayer(context, player, -slavers.getDaysLost());
        }
        else{
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose NOT to collect the rewards!"), player);
        }

        context.goToEndPhase();
        context.executePhase();
    }
}
