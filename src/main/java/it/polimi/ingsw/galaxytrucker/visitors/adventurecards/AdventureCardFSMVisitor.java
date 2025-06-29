package it.polimi.ingsw.galaxytrucker.visitors.adventurecards;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms.*;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.*;

/**
 * Maps each type of {@link AdventureCard} to its corresponding {@link CardFSM} handler.
 * <p>
 * Used to delegate adventure card logic to the correct finite state machine based on card type.
 */
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
        return new PiratesFSM();
    }

    @Override
    public CardFSM visit(Slavers slavers) {
        return new SlaversFSM();
    }

    @Override
    public CardFSM visit(Smugglers smugglers) {
        return new SmugglersFSM();
    }
}
