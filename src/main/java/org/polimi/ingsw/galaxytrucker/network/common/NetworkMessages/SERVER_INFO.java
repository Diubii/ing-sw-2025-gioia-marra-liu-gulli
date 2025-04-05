package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

public class SERVER_INFO extends NetworkMessage {

    private final String address;
    private final int port;


    public SERVER_INFO(NetworkMessageType type,String address, int port) {
        super(type);
        this.address = address;
        this.port = port;

    }
    public String getAddress() {
        return address;
    }
    public int getPort() {
        return port;
    }

    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }



}
