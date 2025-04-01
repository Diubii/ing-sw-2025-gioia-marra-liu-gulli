package org.polimi.ingsw.galaxytrucker.model.visitors;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;

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

    String visit(NICKNAME_REQUEST nicknameRequest);

    String visit(NICKNAME_RESPONSE nicknameRequest);

    String visit(NetworkMessage nicknameRequest);

    String visit(SERVER_INFO nicknameRequest);
}
