package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.annotations.TemporaryType;
import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;

public class ShipViewUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 44662262421L;

    @TemporaryType(temporaryType = "Ship", actualType = "ShipView")
    private final Ship shipView; //CAMBIARE TIPO Ship->ShipView

    public ShipViewUpdate(Ship shipView) {
        this.shipView = shipView;
    }

    @Override
    public void accept(ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }

    public Ship getShipView() {
        return shipView;
    }
}
