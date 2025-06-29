package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedStation;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code AbandonedStationEffect} class manages the effect logic for the {@link AbandonedStation} adventure card.
 * <p>
 *
 * This effect allows a player to choose whether to accept a reward from an abandoned station at the cost of
 * a certain number of crew members and backward movement. The class handles the validation, client interaction,
 * and game state progression.
 *
 * <p>
 * The effect flow includes:
 * <ol>
 *     <li>Checking if the player has enough crew members to activate the card.</li>
 *     <li>If eligible, prompting the player to accept or refuse the effect.</li>
 *     <li>Handling the player's decision and updating the game state accordingly.</li>
 * </ol>
 *
 */
public abstract class AbandonedStationEffect {
    /**
     * Begins the Abandoned Station card effect by checking if the player has enough crew.
     * If so, it prompts the player to accept or decline the reward.
     * If not, the player is skipped and the turn proceeds to the next player or phase.
     * @param context the {@link CardContext} representing the current card and player state.
     */
    public static void start(CardContext context) {
        Player player = context.getCurrentPlayer();
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();

        if (player.getShip().getnCrew() >= abandonedStation.getRequiredCrewMembers()) {

            broadcastGameMessage(context,"Il giocatore "+ player.getNickName() +"ha abbastanza membri dell'equipaggio e sta decidendo se raccogliere la ricompensa...");
            sleepSafe(600);

            context.nextPhase();
            sendMessage(context, player, new ActivateAdventureCardRequest());
        } else {
//            System.out.println( player.getNickName()  + " non ha abbastanza membri dell'equipaggio per attivare questa carta. Skippo");
            broadcastGameMessage(context,  player.getNickName() + " non ha abbastanza membri dell'equipaggio per attivare questa carta.");
            sleepSafe(600);
            if(context.currentPlayerIsLast()){
                context.goToEndPhase();
            }
            else{
                context.nextPlayer();
            }
            context.executePhase();
        }
    }

    /**
     * Handles the player's response to the activation request.
     * <p>
     * If the player accepts, a notification is broadcast and send a shipUpdate for add the necessary information in reply.
     * If the player refuses, the game proceeds to the next player or phase.
     *
     * @param context the {@link CardContext} containing the activation response.
     */
    public static void receivedCardActivationResponse(CardContext context) {
//        System.out.println("DEBUG: AbandonedStationEffect.receivedCardActivationResponse()");
        ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) context.getIncomingNetworkMessage();
        Player currentPlayer = context.getCurrentPlayer();

        if (activateAdventureCardResponse.isActivated()) {
            broadcastGameMessage(context,"Il giocatore " + currentPlayer.getNickName() + " ha accettato la ricompensa. In attesa che carichi la propria astronave...");
            context.nextPhase();
            sendMessage(context, context.getCurrentPlayer(), new ShipUpdate(context.getCurrentPlayer().getShip(), context.getCurrentPlayer().getNickName()));
            sleepSafe(600);
        }
        else {
//            System.out.println("Rifiuto attivazione carta. Skippo ");
            broadcastGameMessage(context,"Il giocatore " + currentPlayer.getNickName() + " rifiuta di attivare l'effetto della carta.");
            sleepSafe(600);
            if(context.currentPlayerIsLast()){
                context.goToEndPhase();
            }
            else{
                context.resetFSM();
                context.nextPlayer();
            }
            context.executePhase();
        }
    }

    /**
     * Executes the movement penalty for the player after activating the effect.
     * The player is moved backward by a number of days specified by the card.
     * Then the game proceeds to the next phase(end phase).
     *
     * @param context the {@link CardContext} containing current player and card data.
     */
    public static void moveCurrentPlayer(CardContext context) {

//        System.out.println("DEBUG: AbandonedStationEffect::moveCurrentPlayer");
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();
        broadcastGameMessage(context,"Il giocatore " + context.getCurrentPlayer().getNickName() + " deve muoversi indietro di " +context.getAdventureCard().getDaysLost() +" caselle.");
        sleepSafe(600);

        movePlayer(context, context.getCurrentPlayer(), -abandonedStation.getDaysLost());

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
