package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

/**
 * The type Server info.
 */
public class SERVER_INFO extends NetworkMessage {

    private final String address;
    private final int port;


    /**
     * Instantiates a new Server info.
     *
     * @param address the address
     * @param port    the port
     */
    public SERVER_INFO(String address, int port) {
        super();
        this.address = address;
        this.port = port;

    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
