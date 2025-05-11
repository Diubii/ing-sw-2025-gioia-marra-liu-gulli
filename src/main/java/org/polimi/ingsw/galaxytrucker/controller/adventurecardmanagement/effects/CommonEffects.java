package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;

public abstract class CommonEffects {
    public static void end(CardContext context){
        context.getCurrentGame().getGameController().handleEndTurn();
//        context.getCurrentGame().getGameController().handleTurnBeforeDrawnCard();
    }
}
