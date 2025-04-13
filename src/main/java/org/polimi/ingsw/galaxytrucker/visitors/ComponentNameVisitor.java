package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;

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
    public NetworkMessageType visit(NicknameRequest nicknameRequest) {
        return NetworkMessageType.NicknameRequest;
    }

    @Override
    public NetworkMessageType visit(NicknameResponse nicknameResponse) {
        return NetworkMessageType.NicknameResponse;
    }
    @Override
    public NetworkMessageType visit(NetworkMessage nicknameRequest) {
        return NetworkMessageType.NetworkMessage;
    }

    //@Override
    //public NetworkMessageType visit(SERVER_INFO nicknameRequest) {
    //    return NetworkMessage;
    //}

    @Override
    public NetworkMessageType visit(CreateRoomRequest nicknameRequest) {
        return NetworkMessageType.CreateRoomRequest;
    }

    @Override
    public NetworkMessageType visit(JoinRoomRequest nicknameRequest) {
        return NetworkMessageType.JoinRoomRequest;
    }

    @Override
    public NetworkMessageType visit(JoiniRoomOptionsRequest nicknameRequest) {
        return NetworkMessageType.JoinRoomOptionsRequest;
    }

    @Override
    public NetworkMessageType visit(DrawTileRequest nicknameRequest) {
        return NetworkMessageType.DrawTileRequest;
    }

}



