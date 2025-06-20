package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

public class SERVER_INFO extends NetworkMessage {

    private final String address;
    private final int port;


    public SERVER_INFO(String address, int port) {
        super();
        this.address = address;
        this.port = port;

    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
