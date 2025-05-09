package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * Represents a Finite State Machine (FSM) for a card. It is extended by the FSM class of the specific card.
 */
public abstract class CardFSM {
    private final ArrayList<Consumer<CardContext>> phases;
    private Consumer<CardContext> currentPhase;
    private ListIterator<Consumer<CardContext>> phaseIterator;
    private boolean done = false;

    protected CardFSM() {
        phases = initPhases();
        phaseIterator = phases.listIterator();
        currentPhase = phaseIterator.next();
    }

    public abstract ArrayList<Consumer<CardContext>> initPhases();

    public void execute(CardContext cardContext) {
        if (currentPhase != null) {
            currentPhase.accept(cardContext);
        } else {
            System.err.println("[CardFSM] No phase to execute.");
        }
    }

    public void previous() {
        if (phaseIterator.hasPrevious()) currentPhase = phaseIterator.previous();
    }

    public void next() {
        if (phaseIterator.hasNext()) {
            currentPhase = phaseIterator.next();
        } else {
            done = true;
        }
    }

    public boolean isDone() {
        return done;
    }

    public void reset() {
        phaseIterator = phases.listIterator();
        currentPhase = phaseIterator.next();
        done = false;
    }
}
