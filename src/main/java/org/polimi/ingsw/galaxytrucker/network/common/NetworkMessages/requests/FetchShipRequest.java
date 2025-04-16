package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;

public class FetchShipRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43847L;

    private final String targetNickname;

    public FetchShipRequest(String targetNickname) {
        this.targetNickname = targetNickname;
    }

    @Override
    public void accept(ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }

    public String getTargetNickname() {
        return targetNickname;
    }
}
