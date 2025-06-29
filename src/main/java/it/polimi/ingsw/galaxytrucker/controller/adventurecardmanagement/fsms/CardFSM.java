package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CommonEffects;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Abstract base class representing a finite state machine (FSM) for handling the execution
 * of an adventure card. Each card has its own FSM that defines specific phases.
 * <p>
 * Phases are defined as a list of {@link Consumer}s operating on {@link CardContext}.
 */
public abstract class CardFSM {
    private final ArrayList<Consumer<CardContext>> phases;
    private Consumer<CardContext> currentPhase;
    private int currentPhaseIndex = 0;

    /**
     * Constructs the FSM by initializing its phases and setting the first phase as current.
     */
    protected CardFSM() {
        phases = initPhases();
        phases.add(CommonEffects::end);

        currentPhase = phases.get(currentPhaseIndex);
    }

    /**
     * Initializes and returns the list of card-specific phases.
     * This method must be implemented by subclasses to define the card logic.
     *
     * @return list of phase consumers
     */
    public abstract ArrayList<Consumer<CardContext>> initPhases();

    /**
     * Executes the current phase with the provided card context.
     *
     * @param cardContext the current context of the card being executed
     */
    public void execute(CardContext cardContext) {
        if (currentPhase != null) {
            currentPhase.accept(cardContext);
        }
        else {
            System.err.println("[CardFSM] No phase to execute.");
        }
    }


    /**
     * Moves the FSM to the previous phase, if not already at the first.
     */
    public void previous() {
        currentPhaseIndex = currentPhaseIndex == 0 ? 0 : currentPhaseIndex - 1;
        currentPhase = phases.get(currentPhaseIndex);
    }

    /**
     * Moves the FSM to the next phase, if within bounds.
     */
    public void next() {
        currentPhaseIndex++;
        if(currentPhaseIndex < phases.size()) {
            currentPhase = phases.get(currentPhaseIndex);
        }

    }

    /**
     * Skips directly to the final (end) phase of the FSM.
     */
    public void skipToEndState(){
        currentPhaseIndex = phases.size() - 1;
        currentPhase = phases.getLast();
    }

    /**
     * Resets the FSM to its initial state (first phase).
     */
    public void reset() {
        currentPhaseIndex = 0;
        currentPhase = phases.get(currentPhaseIndex);
    }

}
