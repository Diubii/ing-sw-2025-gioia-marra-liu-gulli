package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.sendMessage;

public abstract class CommonEffects {
    public static void sendDoubleCannonsActivationRequest(CardContext context) {
        System.out.println("DEBUG: CommonEffects.sendDoubleCannonsActivationRequest()");
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
