package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsm;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

public class AdventureCardFSMVisitor implements AdventureCardVisitorsInterface<CardFSM> {
    @Override
    public CardFSM visit(AbandonedShip abandonedShip) {
        return new AbandonedShipFSM();
    }

    @Override
    public CardFSM visit(AbandonedStation abandonedStation) {
        return null;
    }

    @Override
    public CardFSM visit(CombatZone combatZone) {
        return null;
    }

    @Override
    public CardFSM visit(Epidemic epidemic) {
        return null;
    }

    @Override
    public CardFSM visit(MeteorSwarm meteorSwarm) {
        return null;
    }

    @Override
    public CardFSM visit(OpenSpace openSpace) {
        return new OpenSpaceFSM();
    }

    @Override
    public CardFSM visit(Planets planets) {
        return null;
    }

    @Override
    public CardFSM visit(Stardust stardust) {
        return null;
    }

    @Override
    public CardFSM visit(Pirates pirates) {
        return null;
    }

    @Override
    public CardFSM visit(Slavers slavers) {
        return null;
    }

    @Override
    public CardFSM visit(Smugglers smugglers) {
        return null;
    }
}
