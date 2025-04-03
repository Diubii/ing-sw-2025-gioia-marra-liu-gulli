package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serializable;
import java.net.Socket;

public class NICKNAME_REQUEST extends NetworkMessage implements Serializable {

    private final String nickname;
    private transient final Socket socket;
    private Boolean learningMatch = null;

    public NICKNAME_REQUEST(NetworkMessageType type, String nickname, Socket socket, Boolean flag) {
        super(type);
        this.nickname = nickname;
        this.socket = socket;
        this.learningMatch = flag;

    }

    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }


    public String getNickname() {
        return nickname;
    }
    public Socket getSocket() {
        return socket;
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }


}
