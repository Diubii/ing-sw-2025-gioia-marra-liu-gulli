package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;

public class DrawTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 533L;
    private final int tileId;

    public DrawTileRequest() {
        this.tileId = -1;
    }


    public DrawTileRequest(int tileId) {
        //pesco da faceup tiles
        this.tileId = tileId;
    }

    public int getTileId() {
        return tileId;
    }

    @Override
    public void accept(ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }
}
