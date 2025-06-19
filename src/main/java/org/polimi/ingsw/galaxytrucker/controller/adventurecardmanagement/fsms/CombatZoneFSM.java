package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CombatZoneEffect;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CombatZone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class CombatZoneFSM extends CardFSM {
    public CombatZoneFSM() {
        super();
    }

    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                Arrays.asList(
                        CombatZoneEffect::checkLevel,
                        CombatZoneEffect::minCrewMembersCheck,
                        CombatZoneEffect::sendDoubleEnginesActivationRequest,
                        CombatZoneEffect::minEnginePowerCheck,
                        CombatZoneEffect::sendDiscardCrewMembersRequest,
                        CombatZoneEffect::receivedDiscardCrewMembersRequest,
                        CombatZoneEffect::sendDoubleCannonsActivationRequest,
                        CombatZoneEffect::minFirePowerCheck,
                        CombatZoneEffect::cannonaitsStart,
                        CombatZoneEffect::cannonaitsFire,
                        CombatZoneEffect::cannonaitsTrunks
                )
        );
    }
}
