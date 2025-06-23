package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CommonEffects;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Represents a Finite State Machine (FSM) for a card. It is extended by the FSM class of the specific card.
 */
public abstract class CardFSM {
    private final ArrayList<Consumer<CardContext>> phases;
    private Consumer<CardContext> currentPhase;
    private int currentPhaseIndex = 0;

    protected CardFSM() {
        phases = initPhases();
        phases.add(CommonEffects::end);

        currentPhase = phases.get(currentPhaseIndex);
    }

    public abstract ArrayList<Consumer<CardContext>> initPhases();

    public void execute(CardContext cardContext) {
        if (currentPhase != null) {
            currentPhase.accept(cardContext);
        }
        else {
            System.err.println("[CardFSM] No phase to execute.");
        }
    }

    public void previous() {
        currentPhaseIndex = currentPhaseIndex == 0 ? 0 : currentPhaseIndex - 1;
        currentPhase = phases.get(currentPhaseIndex);
    }

    public void next() {
        currentPhaseIndex++;
        if(currentPhaseIndex < phases.size()) {
            currentPhase = phases.get(currentPhaseIndex);
        }

    }

    public void skipToEndState(){
        currentPhaseIndex = phases.size() - 1;
        currentPhase = phases.getLast();
    }

    public void reset() {
        currentPhaseIndex = 0;
        currentPhase = phases.get(currentPhaseIndex);
    }

}
