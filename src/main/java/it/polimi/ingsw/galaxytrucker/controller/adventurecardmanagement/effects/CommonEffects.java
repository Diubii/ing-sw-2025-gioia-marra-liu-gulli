package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.sendMessage;

public abstract class CommonEffects {
    public static void sendDoubleCannonsActivationRequest(CardContext context) {
        Player player = context.getCurrentPlayer();

        System.out.println(player.getNickName()+" DEBUG: CommonEffects.sendDoubleCannonsActivationRequest()");
        ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
        context.nextPhase();
        sendMessage(context, context.getCurrentPlayer(), activateDoubleCannonsRequest);
    }

    public static void sendDoubleEnginesActivationRequest(CardContext context) {
        System.out.println("DEBUG: CommonEffects.sendDoubleEnginesActivationRequest()");
        ActivateComponentRequest activateDoubleEnginesRequest = new ActivateComponentRequest(ActivatableComponent.DoubleEngine);
        context.nextPhase();
        sendMessage(context, context.getCurrentPlayer(), activateDoubleEnginesRequest);
    }

    public static void end(CardContext context) {
        System.out.println("DEBUG: CommonEffects.end()");
        context.getCurrentGame().getGameController().handleEndTurn();
//        context.getCurrentGame().getGameController().handleTurnBeforeDrawnCard();
    }
}
