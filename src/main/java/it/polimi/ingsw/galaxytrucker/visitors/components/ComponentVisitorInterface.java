package it.polimi.ingsw.galaxytrucker.visitors.components;

import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.*;

/**
 * A generic visitor interface for handling different types of ship components.
 * <p>
 * This interface follows the Visitor design pattern and allows various operations to be performed
 * on component types without modifying their class definitions.
 *
 * @param <T> The return type of the visitor methods (e.g., a GUI node, a string, etc.)
 */
public interface ComponentVisitorInterface<T> {
    T visit(Component component);

    T visit(BatterySlot component);

    T visit(Cannon component);

    T visit(CentralHousingUnit component);

    T visit(DoubleCannon component);

    T visit(DoubleEngine component);

    T visit(Engine component);

    T visit(GenericCargoHolds component);

    T visit(LifeSupportSystem component);

    T visit(ModularHousingUnit component);

    T visit(Shield component);
}