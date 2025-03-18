package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;

public interface ComponentNameVisitorInterface {
    String visit(Component component);

    String visit(BatterySlot component);
    String visit(Cannon component);
    String visit(CentralHousingUnit component);
    String visit(DoubleCannon component);
    String visit(DoubleEngine component);
    String visit(Engine component);
    String visit(GenericCargoHolds component);
    String visit(LifeSupportSystem component);
    String visit(ModularHousingUnit component);
    String visit(Shield component);
}
