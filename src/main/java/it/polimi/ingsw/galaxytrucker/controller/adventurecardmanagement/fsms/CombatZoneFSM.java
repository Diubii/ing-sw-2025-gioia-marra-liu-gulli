package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CombatZoneEffect;

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
