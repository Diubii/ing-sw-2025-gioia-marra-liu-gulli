package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.network.NetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

public class NICKNAME_REQUEST extends NetworkMessage {

    private final String nickname;

    public NICKNAME_REQUEST(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void accept(NetworkMessageVisitor visitor) {
        visitor.getNicknameRequest(this);
    }
}
