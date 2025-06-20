package it.polimi.ingsw.galaxytrucker.visitors.components;

import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.*;

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