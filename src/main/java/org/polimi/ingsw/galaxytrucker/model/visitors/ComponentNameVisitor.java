package org.polimi.ingsw.galaxytrucker.model.visitors;

import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.LOBBY_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;

public class ComponentNameVisitor implements ComponentNameVisitorInterface {
    @Override
    public String visit(Component component) {
        return "";
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
        if (component.getColor().equals(AlienColor.PURPLE)){
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

    @Override
    public String visit(NICKNAME_REQUEST nicknameRequest) {
        return "NICKNAME_REQUEST";
    }

    @Override
    public String visit(NICKNAME_RESPONSE nicknameResponse) {
        return "NICKNAME_RESPONSE";
    }
    @Override
    public String visit(NetworkMessage nicknameRequest) {
        return "NM";
    }

    @Override
    public String visit(SERVER_INFO nicknameRequest) {
        return "SERVER_INFO";
    }
    @Override
    public String visit(LOBBY_INFO nicknameRequest) {
        return "LOBBY_INFO";
    }

}



