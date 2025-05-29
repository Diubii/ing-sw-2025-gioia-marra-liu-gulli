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

        System.out.println(player.getNickName() +"  entered firePowerCheck");

        Float playerFirePower = player.getShip().calculateFirePower();
        float slaversFirePower = (float) slavers.getFirePower();

        if (slaversFirePower > playerFirePower){
            GameMessage personalMessage = new GameMessage("The Slavers are going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has less FirePower than the Slavers!");
            broadcastExcept(context, info, player);
            //gestisco la sua sconfitta


            int playerCrewMembersNumber = player.getShip().getnCrew();
            int nCrewToBeDiscarded = Integer.min(playerCrewMembersNumber, slavers.getPenalty());
            DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(nCrewToBeDiscarded);
            //Chiediamo l'attivazione di CommonEffects::sendDoubleEnginesActivationRequest al prossimo
            context.nextPhase();
            sendMessage(context, player, discardCrewMembersRequest);


        }
        else if (slaversFirePower< playerFirePower){
            GameMessage personalMessage = new GameMessage("You won against the Slavers!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has defeated the Slavers!");
            broadcastExcept(context, info, player);

            //chiedo se vuole accettare la ricompensa o no, in caso l'accettasse lo muovo indietro e aggiorno i crediti
            CollectRewardsRequest collectRewardsRequest = new CollectRewardsRequest();

            //Double nextPhase per passare a receivedRewardsCollectionResponse
            context.nextPhase(2);


            sendMessage(context, player, collectRewardsRequest);

            return;
        }
        else if (slaversFirePower == playerFirePower){
            System.out.println("Debug" + player.getNickName() +" slaversFirePower == playerFirePower");
            GameMessage personalMessage = new GameMessage("The Slavers are not going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);

            context.previousPhase();
            context.nextPlayer();
            context.executePhase();
        }

    }

    public static void receivedDiscardCrewMembersResponse(CardContext context){
        Slavers slavers = (Slavers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
        System.out.println(player.getNickName() +"  entered receivedDiscardCrewMembersResponse");
        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();

        discardCrewMembers(player, discardCrewMembersResponse, discardCrewMembersResponse.getHousingPositions().size());

        //Broadcasto nuova nave
        broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));


        if(context.currentPlayerIsLast()){
            context.goToEndPhase();
            context.executePhase();

        }
        else {
            //per tornare alla phase di attivare Double Cannon
            context.previousPhase(2);
            //end
            context.nextPlayer();
            context.executePhase();

        }
    }

    public static void receivedRewardsCollectionResponse(CardContext context){
        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
        Slavers slavers = (Slavers) context.getAdventureCard();
        System.out.println(player.getNickName() +"  entered receivedRewardsCollectionResponse");
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
