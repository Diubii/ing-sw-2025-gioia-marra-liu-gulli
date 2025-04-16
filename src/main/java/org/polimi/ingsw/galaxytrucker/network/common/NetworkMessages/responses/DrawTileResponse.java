package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;

public class DrawTileResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 522L;
    private final Tile tile;
    private  String errorMessage;

    public DrawTileResponse(Tile tile) {

        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void accept(ServerController serverController, ClientHandler clientHandler) {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }
}
