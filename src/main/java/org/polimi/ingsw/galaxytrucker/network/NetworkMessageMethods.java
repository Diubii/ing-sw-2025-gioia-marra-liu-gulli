package org.polimi.ingsw.galaxytrucker.network;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;

public class NetworkMessageMethods implements NetworkMessageVisitor {
    public void getNicknameRequest(NICKNAME_REQUEST nickReq) {
        System.out.println("Got nickname request: " + nickReq.getNickname());
    }

    public void getNicknameResponse(NICKNAME_RESPONSE response) {
        System.out.println("Got nickname response");
    }
}
