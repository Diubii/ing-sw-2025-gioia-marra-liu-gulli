package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.StardustEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * FSM (Finite State Machine) for handling the "Stardust" adventure card effect.
 * <p>
 * The card applies a penalty to each player based on the number of exposed connectors on their ship.
 * The phase executes this logic in a single step.
 */
public class StardustFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                List.of(StardustEffect::effect)
        );
    }
}
