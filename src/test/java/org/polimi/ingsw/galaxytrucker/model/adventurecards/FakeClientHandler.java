package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageCouplingVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

import java.util.ArrayList;

public class FakeClientHandler implements ClientHandler {
    private final ServerController controller;
    private final ArrayList<NetworkMessage> mockResponses;
    private final NetworkMessageVisitor serverControllerNetworkMessageVisitor;
    private final NetworkMessageCouplingVisitor serverControllerNetworkMessageCouplingVisitor = new NetworkMessageCouplingVisitor();

    public FakeClientHandler(ServerController controller, ArrayList<NetworkMessage> mockResponses) {
        this.controller = controller;
        serverControllerNetworkMessageVisitor = new NetworkMessageVisitor(controller, this);
        this.mockResponses = mockResponses;
    }

    @Override
    public void sendMessage(NetworkMessage message) {
        LobbyManager game = controller.getLobbyFromHandler(this);
        if (game == null) return;

        GameController gameController = game.getGameController();
        if (gameController == null) return;

        CardContext currentCardContext = gameController.getCurrentCardContext();
        if (currentCardContext == null) return;

        Integer expectedNetworkMessagesOfTheSameType = controller.getLobbyFromHandler(this).getGameController().getCurrentCardContext().getExpectedNumberOfNetworkMessagesPerType().get(message.accept(serverControllerNetworkMessageCouplingVisitor));

        if (expectedNetworkMessagesOfTheSameType == null || expectedNetworkMessagesOfTheSameType == 0) return;

        if (mockResponses.isEmpty()) {
            System.err.println("[FakeClientHandler] No more mock responses to send for message: " + message.accept(new NetworkMessageNameVisitor()));
            return;
        }

        NetworkMessage response = mockResponses.getFirst();
        mockResponses.removeFirst();

        response.accept(serverControllerNetworkMessageVisitor);
    }

}