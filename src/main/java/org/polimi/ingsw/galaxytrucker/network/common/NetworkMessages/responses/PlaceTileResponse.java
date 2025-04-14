package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;

public class PlaceTileResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 4383506L;

    private final int tileId;
    private final int x;

    private final int y;
    private final int rotation;
    private final int reservedIndex;

    public PlaceTileResponse(int tileId, int x, int y, int rotation, int reservedIndex) {
        this.tileId = tileId;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.reservedIndex = reservedIndex;
    }

    @Override
    public void accept(ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }

    public int getReservedIndex() {
        return reservedIndex;
    }
    public int getRotation() {
        return rotation;
    }
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
    public int getTileId() {
        return tileId;
    }
}
