package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;

public class MessageManager {
    private final GameNetworkModel model;
    private final ServerController controller;
    public MessageManager(GameNetworkModel model, ServerController controller) {
        this.model = model;
        this.controller = controller;
    }
    //logica

    public void handle(NetworkMessage message) throws TooManyPlayersException, PlayerAlreadyExistsException {

    }
}
