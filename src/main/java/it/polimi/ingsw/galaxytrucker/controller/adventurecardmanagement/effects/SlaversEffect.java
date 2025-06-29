package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Slavers;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CollectRewardsRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CollectRewardsResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code SlaversEffect} class manages the resolution of the "Slavers" adventure card effect.
 *
 * <p>Each player compares their ship's firepower against that of the slavers:
 * <ul>
 *   <li>If the player loses, they must discard a number of crew members as a penalty.</li>
 *   <li>If the player wins, they are offered a reward that can be accepted at the cost of losing days.</li>
 *   <li>In case of a tie, no penalties or rewards apply.</li>
 * </ul>
 *
 */
public class SlaversEffect {

    /**
     * Compares the current player's firepower with that of the slavers and handles the outcome:
     * loss (discard crew), win (optional reward), or tie (no effect).
     *
     * @param context the {@link CardContext} representing current game and player state.
     */
    public static void firePowerCheck(CardContext context){

        LobbyManager game = context.getCurrentGame();
        Slavers slavers = (Slavers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();


//        System.out.println(player.getNickName() +"  entered firePowerCheck");

        Float playerFirePower = player.getShip().calculateFirePower();
        float slaversFirePower = (float) slavers.getFirePower();
        resetDoubleCannon(player);
        ShipUpdate shipUpdate = new ShipUpdate(player.getShip(),player.getNickName());
        broadcast(context, shipUpdate);


        if (slaversFirePower > playerFirePower){
            GameMessage personalMessage = new GameMessage("The Slavers are going to haunt you!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has less FirePower than the Slavers!");
            broadcastExcept(context, info, player);

            sleepSafe(600);
            //gestisco la sua sconfitta


            int playerCrewMembersNumber = player.getShip().getnCrew();
            int nCrewToBeDiscarded = Integer.min(playerCrewMembersNumber, slavers.getPenalty());
            DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(nCrewToBeDiscarded);

            if(nCrewToBeDiscarded!=slavers.getPenalty()){
                sendGameMessage(context,player,"Dato che non hai abbastanza membri dell'equipaggio, ti preghiamo di svuotare completamente il tuo equipaggio.");
            }

            sleepSafe(600);

            //Chiediamo l'attivazione di CommonEffects::sendDoubleEnginesActivationRequest al prossimo
            context.nextPhase();
            sendMessage(context, player, discardCrewMembersRequest);


        }
        else if (slaversFirePower< playerFirePower){
            GameMessage personalMessage = new GameMessage("You won against the Slavers!"); //personalMessage.setIsTurn(true);
            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
            GameMessage info = new GameMessage(); info.setMessage(player.getNickName() + " has defeated the Slavers!");
            broadcastExcept(context, info, player);

            sleepSafe(600);

            //chiedo se vuole accettare la ricompensa o no, in caso l'accettasse lo muovo indietro e aggiorno i crediti
            CollectRewardsRequest collectRewardsRequest = new CollectRewardsRequest();

            //Double nextPhase per passare a receivedRewardsCollectionResponse
            context.nextPhase(2);


            sendMessage(context, player, collectRewardsRequest);

            return;
        }
        else {
//            System.out.println("Debug" + player.getNickName() +" slaversFirePower == playerFirePower");
            GameMessage personalMessage = new GameMessage("The Slavers are not going to haunt you!"); //personalMessage.setIsTurn(true);

            GameMessage info = new GameMessage(); info.setMessage("Il giocatore" +player.getNickName() +" ha pareggiato con gli schiavisti");
            broadcastExcept(context, info, player);
             sleepSafe(600);

            game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);

            if(context.currentPlayerIsLast()) {
                context.goToEndPhase();
                context.executePhase();
            }
            else {
                context.previousPhase();
                context.nextPlayer();
                context.executePhase();
            }
        }

    }

    /**
     * Processes the player's response to a request for discarding crew members after losing to the slavers.
     * Updates ship state and proceeds to the next player or ends the phase if all have responded.
     *
     * @param context the {@link CardContext} containing the discard response and game state.
     */
    public static void receivedDiscardCrewMembersResponse(CardContext context){
        Slavers slavers = (Slavers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
//        System.out.println(player.getNickName() +"  entered receivedDiscardCrewMembersResponse");
        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();

        discardCrewMembers(context, player, discardCrewMembersResponse, discardCrewMembersResponse.getHousingPositions().size());



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

    /**
     * Processes the player's response to the optional reward offer after defeating the slavers.
     * If accepted, the player gains credits and loses days.
     * Afterward, the phase is ended.
     *
     * @param context the {@link CardContext} including the response and player state.
     */

    public static void receivedRewardsCollectionResponse(CardContext context){
        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
        Slavers slavers = (Slavers) context.getAdventureCard();
//        System.out.println(player.getNickName() +"  entered receivedRewardsCollectionResponse");
        if(collectRewardsResponse.doesWantToCollect()) {
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose to collect the rewards!"), player);
            sleepSafe(600);

            player.addCredits(slavers.getCredits());
            movePlayer(context, player, -slavers.getDaysLost());
        }
        else{
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose NOT to collect the rewards!"), player);
            sleepSafe(600);
        }

        context.goToEndPhase();
        context.executePhase();
    }
}
