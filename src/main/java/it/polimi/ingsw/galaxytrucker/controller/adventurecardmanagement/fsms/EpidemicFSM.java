package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.EpidemicEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/**
 * FSM (Finite State Machine) for handling the "Epidemic" adventure card.
 * <p>
 * This FSM defines the execution phase sequence for the card,
 * delegating to the relevant method in {@link EpidemicEffect}.
 */
public class EpidemicFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                List.of(
                        EpidemicEffect::check
                )
        );
    }
}
