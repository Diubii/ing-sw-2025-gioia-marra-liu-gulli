package it.polimi.ingsw.galaxytrucker.visitors.adventurecards;

import it.polimi.ingsw.galaxytrucker.model.adventurecards.*;

/**
 * Visitor interface for handling different types of {@link AdventureCard}.
 * <p>
 * This allows operations to be performed on specific card types without modifying their classes.
 *
 * @param <T> The result type returned by each visit method (e.g., UI element, String, etc.)
 */
public interface AdventureCardVisitorsInterface<T> {
    T visit(AbandonedShip abandonedShip);

    T visit(AbandonedStation abandonedStation);

    T visit(CombatZone combatZone);

    T visit(Epidemic epidemic);

    T visit(MeteorSwarm meteorSwarm);

    T visit(OpenSpace openSpace);

    T visit(Planets planets);

    T visit(Stardust stardust);

    T visit(Pirates pirates);

    T visit(Slavers slavers);

    T visit(Smugglers smugglers);
}
