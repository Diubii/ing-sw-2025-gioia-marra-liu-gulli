package org.polimi.ingsw.galaxytrucker.network;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;

public interface NetworkMessageVisitor {
    void getNicknameRequest(NICKNAME_REQUEST nickname);
    void getNicknameResponse(NICKNAME_RESPONSE response);
}
