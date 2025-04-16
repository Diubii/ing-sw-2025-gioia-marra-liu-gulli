package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameStartedUpdate;

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
