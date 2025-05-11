package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CommonEffects;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.SlaversEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class SlaversFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                Arrays.asList(
                        CommonEffects::sendDoubleCannonsActivationRequest,
                        SlaversEffect::firePowerCheck,
                        SlaversEffect::receivedDiscardCrewMembersResponse,
                        SlaversEffect::receivedRewardsCollectionResponse
                )
        );
    }
}
