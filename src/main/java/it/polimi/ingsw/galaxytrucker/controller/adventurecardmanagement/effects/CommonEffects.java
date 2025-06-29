package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code CommonEffects} class contains utility methods for handling common effects
 * that may be triggered by multiple types of adventure cards, such as component activation
 * and end-of-effect procedures.
 *
 * <p>
 * Specifically, this includes logic for requesting the activation of special ship components
 * (e.g., double cannons, double engines) and for gracefully finalizing the current turn once
 * a card effect has concluded.
 *
 * <p>
 *
 */
public abstract class CommonEffects {

    /**
     * Sends a request to the current player asking whether they want to activate the Double Cannon component.
     * @param context the {@link CardContext} representing the current state of the game and active player.
     */
    public static void sendDoubleCannonsActivationRequest(CardContext context) {
        Player player = context.getCurrentPlayer();

//        System.out.println(player.getNickName()+" DEBUG: CommonEffects.sendDoubleCannonsActivationRequest()");

        broadcastGameMessage(context,"In attesa che il giocatore "+player.getNickName()+"  scelga se attivare il componente.");
        sleepSafe(600);

        ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
        context.nextPhase();
        sendMessage(context, context.getCurrentPlayer(), activateDoubleCannonsRequest);
    }

    /**
     * Sends a request to the current player asking whether they want to activate the Double Engine component.
     * @param context the {@link CardContext} representing the current state of the game and active player.
     */
    public static void sendDoubleEnginesActivationRequest(CardContext context) {
//        System.out.println("DEBUG: CommonEffects.sendDoubleEnginesActivationRequest()");
        Player player = context.getCurrentPlayer();
        broadcastGameMessage(context,"In attesa che il giocatore "+player.getNickName()+"  scelga se attivare il componente.");
        sleepSafe(600);

        ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
        context.nextPhase();
        sendMessage(context, context.getCurrentPlayer(), activateDoubleEnginesRequest);
    }

    /**
     * Finalizes the effect of the current adventure card.
     * @param context the {@link CardContext} that includes the current game controller.
     */
    public static void end(CardContext context) {

        broadcastGameMessage(context,"Effetto della carta concluso");
        sleepSafe(600);

        context.getCurrentGame().getGameController().handleEndTurn();
    }
}
