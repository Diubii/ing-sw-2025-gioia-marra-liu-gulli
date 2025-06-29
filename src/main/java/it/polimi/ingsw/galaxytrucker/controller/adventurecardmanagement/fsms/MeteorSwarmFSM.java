package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.MeteorSwarmEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * FSM (Finite State Machine) for handling the "Meteor Swarm" adventure card.
 * <p>
 * This FSM defines the sequential phases used to simulate a meteor swarm event,
 * coordinating component activation, meteor impact, and post-impact damage collection.
 */
public class MeteorSwarmFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(Arrays.asList(
                MeteorSwarmEffect::sendActivateComponentRequests,
                MeteorSwarmEffect::unleashTheMeteorSwarm,
                MeteorSwarmEffect::askTrunkReq,
                MeteorSwarmEffect::receivedTrunkRepo

        ));
    }
}
