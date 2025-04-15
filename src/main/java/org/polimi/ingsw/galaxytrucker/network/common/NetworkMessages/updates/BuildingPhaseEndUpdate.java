package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class BuildingPhaseEndUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1122352456401321L;

    private final ArrayList<String> finishOrder;

    public BuildingPhaseEndUpdate(ArrayList<String> finishOrder) {
        this.finishOrder = finishOrder;
    }

    @Override
    public void accept(ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }

    public ArrayList<String> getFinishOrder() {
        return finishOrder;
    }
}
