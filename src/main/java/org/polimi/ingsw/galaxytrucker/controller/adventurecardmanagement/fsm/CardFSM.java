package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsm;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.CardPhase;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

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

    protected CardFSM() {
        phases = initPhases();
        phaseIterator = phases.listIterator();
        currentPhase = phaseIterator.next();
    }

    public abstract ArrayList<Consumer<CardContext>> initPhases();

    public void execute(CardContext cardContext) {
        currentPhase.accept(cardContext);
    }

    public void next(){
        currentPhase = phaseIterator.next();
    }

    public void reset(){
        phaseIterator = phases.listIterator();
        currentPhase = phaseIterator.next();
    }
}
