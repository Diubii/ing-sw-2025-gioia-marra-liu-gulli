package it.polimi.ingsw.galaxytrucker.visitors.components;

import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.*;

public class ComponentNameVisitor implements ComponentVisitorInterface<String> {
    @Override
    public String visit(Component component) {
        return "Component";
    }

    @Override
    public String visit(BatterySlot component) {
        return "BatterySlot";
    }

    @Override
    public String visit(Cannon component) {
        return "Cannon";
    }

    @Override
    public String visit(CentralHousingUnit component) {
        return "CentralHousingUnit";
    }

    @Override
    public String visit(DoubleCannon component) {
        return "DoubleCannon";
    }

    @Override
    public String visit(DoubleEngine component) {
        return "DoubleEngine";
    }

    @Override
    public String visit(Engine component) {
        return "Engine";
    }

    @Override
    public String visit(GenericCargoHolds component) {
        return "GenericCargoHolds";
    }

    @Override
    public String visit(LifeSupportSystem component) {
        if (component.getColor().equals(AlienColor.PURPLE)) {
            return "PurpleLifeSupportSystem";
        }
        return "BrownLifeSupportSystem";
    }

    @Override
    public String visit(ModularHousingUnit component) {
        return "ModularHousingUnit";
    }

    @Override
    public String visit(Shield component) {
        return "Shield";
    }
}


