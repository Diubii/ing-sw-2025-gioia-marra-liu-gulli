package it.polimi.ingsw.galaxytrucker.controller;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import it.polimi.ingsw.galaxytrucker.view.View;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

public class FakeClientController extends ClientController {
    private final NetworkMessageVisitor visitor;

    public FakeClientController(View view, Boolean flag, ServerController serverController, ClientHandler clientHandler) {
        super(view, flag);
        visitor = new NetworkMessageVisitor(serverController, clientHandler);
    }

    @Override
    public boolean safeSendMessage(NetworkMessage message) {
        message.accept(visitor);
        return true;
    }
}
