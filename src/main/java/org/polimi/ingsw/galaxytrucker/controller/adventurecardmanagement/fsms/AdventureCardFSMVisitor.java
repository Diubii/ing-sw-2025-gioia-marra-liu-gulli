package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

public class AdventureCardFSMVisitor implements AdventureCardVisitorsInterface<CardFSM> {
    @Override
    public CardFSM visit(AbandonedShip abandonedShip) {
        return new AbandonedShipFSM();
    }

    @Override
    public CardFSM visit(AbandonedStation abandonedStation) {
        return new AbandonedStationFSM();
    }

    @Override
    public CardFSM visit(CombatZone combatZone) {
        return new CombatZoneFSM();
    }

    @Override
    public CardFSM visit(Epidemic epidemic) {
        return new EpidemicFSM();
    }

    @Override
    public CardFSM visit(MeteorSwarm meteorSwarm) {
        return new MeteorSwarmFSM();
    }

    @Override
    public CardFSM visit(OpenSpace openSpace) {
        return new OpenSpaceFSM();
    }

    @Override
    public CardFSM visit(Planets planets) {
        return new PlanetsFSM();
    }

    @Override
    public CardFSM visit(Stardust stardust) {
        return new StardustFSM();
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
