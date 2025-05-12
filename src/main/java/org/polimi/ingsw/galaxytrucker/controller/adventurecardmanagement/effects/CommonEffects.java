package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.sendMessage;

public abstract class CommonEffects {
    public static void sendDoubleCannonsActivationRequest(CardContext context){
        ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);
        sendMessage(context, context.getCurrentPlayer(), activateDoubleCannonsRequest);

        context.nextPhase();
    }

    public static void end(CardContext context){
        context.getCurrentGame().getGameController().handleEndTurn();
//        context.getCurrentGame().getGameController().handleTurnBeforeDrawnCard();
    }
}
