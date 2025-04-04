package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.network.NetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serializable;

public class NICKNAME_REQUEST extends NetworkMessage implements Serializable {

    private final String nickname;

    public NICKNAME_REQUEST(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void accept(NetworkMessageVisitor visitor) {
        visitor.getNicknameRequest(this);
    }

    public String getNickname() {
        return nickname;
    }
}
