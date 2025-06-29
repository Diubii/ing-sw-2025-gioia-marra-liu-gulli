package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedShip;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code AbandonedShipEffect} class handles the effect logic of the {@link AbandonedShip} adventure card.
 * <p>
 * This effect allows a player to board an abandoned ship by sacrificing a required number of crew members in exchange for credits,
 * with possible movement penalties (days lost). The class manages the multi-phase activation process and handles communication
 * with clients via network messages.
 *
 * <p>
 * The effect follows these main steps:
 * <ol>
 *     <li>Check if the player has enough crew to activate the card.</li>
 *     <li>If yes, send a request to the client to activate the card.</li>
 *     <li>Upon confirmation, request the client to discard the appropriate number of crew members.</li>
 *     <li>Handling the player's decision and updating the game state accordingly.</li>
 * </ol>
 *
 *
 */
public abstract class AbandonedShipEffect {
    /**
     * Initiates the abandoned ship card effect.
     * <p>
     * Checks if the player has the required number of crew members.
     * If they do,
     * sends a request to activate the card.
     * Otherwise,
     * notifies the player and proceeds to the next turn.
     *
     * @param context the current {@link CardContext}, containing the adventure card and game state.
     */
    public static void start(CardContext context) {
        AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

        if (player.getShip().getnCrew() >= abandonedShip.getRequiredCrewMembers()) {
            ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
            context.nextPhase();
            sleepSafe(600);
            sendMessage(context, player, activateAdventureCardRequest);

        } else {
            sendGameMessage(context, player, "Non hai abbastanza membri dell'equipaggio per attivare questa carta.");
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
     * Handles the response from the client after attempting to activate the abandoned ship card.
     * <p>
     * If activation is confirmed, sends a request to discard the necessary number of crew members.
     * If not confirmed, skips the current player and proceeds to the next turn or phase.
     *
     * @param context the current {@link CardContext}, including the response message.
     */
    public static void receivedCardActivationResponse(CardContext context) {
        ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) context.getIncomingNetworkMessage();

        if (activateAdventureCardResponse.isActivated()) {
            AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();
            context.nextPhase();
            Player player = context.getCurrentPlayer();

            int playerCrewMembersNumber = player.getShip().getnCrew();
            int nCrewToBeDiscarded = Integer.min(playerCrewMembersNumber, abandonedShip.getRequiredCrewMembers());
            DiscardCrewMembersRequest discardCrewMembersRequest = new DiscardCrewMembersRequest(nCrewToBeDiscarded);
            if(nCrewToBeDiscarded!=abandonedShip.getRequiredCrewMembers()){
                sendGameMessage(context,player,"Dato che non hai abbastanza membri dell'equipaggio, ti preghiamo di svuotare completamente il tuo equipaggio.");
            }
            sleepSafe(600);
            sendMessage(context, context.getCurrentPlayer(), discardCrewMembersRequest);
        }

        else {
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
     * Called when the client confirms the crew members have been discarded.
     * <p>
     * Applies the card effect: grants credits, updates the ship, and moves the player backward
     * by the specified number of days. Then proceeds to the next phase.
     *
     * @param context the current {@link CardContext}, including the discard response.
     */
    public static void crewDiscarded(CardContext context) {
        Player player = context.getCurrentPlayer();
        AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();

        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();
        Utils.discardCrewMembers(context,context.getCurrentPlayer(), discardCrewMembersResponse, discardCrewMembersResponse.getHousingPositions().size());

        player.addCredits(abandonedShip.getCredits());
        broadcastGameMessage(context,"Il giocatore " + player.getNickName()+ " ha guadagnato " + abandonedShip.getCredits()+ " punti");
        sleepSafe(600);

        movePlayer(context, player, -abandonedShip.getDaysLost());
        context.nextPhase();
        context.executePhase();
    }
}
