package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException {
        return visitor.visit(this);
    }
}
