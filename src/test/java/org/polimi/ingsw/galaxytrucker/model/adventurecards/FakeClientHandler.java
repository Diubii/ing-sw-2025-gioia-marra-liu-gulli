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
import java.util.Optional;

public class FakeClientHandler implements ClientHandler {
    private final ServerController controller;
    private final ArrayList<NetworkMessage> mockResponses;
    private final NetworkMessageVisitor serverControllerNetworkMessageVisitor;
    private final NetworkMessageCouplingVisitor serverControllerNetworkMessageCouplingVisitor = new NetworkMessageCouplingVisitor();
    private final NetworkMessageNameVisitor serverControllerNetworkMessageNameVisitor = new NetworkMessageNameVisitor();
    private final ArrayList<NetworkMessage> sentMessages = new ArrayList<>();

    public FakeClientHandler(ServerController controller, ArrayList<NetworkMessage> mockResponses) {
        this.controller = controller;
        serverControllerNetworkMessageVisitor = new NetworkMessageVisitor(controller, this);
        this.mockResponses = new ArrayList<>(mockResponses);
    }


    public void setMockResponses(ArrayList<NetworkMessage> responses) {
        this.mockResponses.clear();
        this.mockResponses.addAll(responses);
    }
    @Override
    public void sendMessage(NetworkMessage message) {
        sentMessages.add(message);
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

        //System.out.println(mockResponses.toString());

        NetworkMessage response = mockResponses.stream().filter(r -> r.accept(serverControllerNetworkMessageNameVisitor).equals(message.accept(serverControllerNetworkMessageCouplingVisitor))).findFirst().orElse(null);

        if(response != null){
            mockResponses.remove(response);
            response.accept(serverControllerNetworkMessageVisitor);
        }
        else{
            System.out.println("[FakeClientHandler] Couldn't find response related to " + message.accept(serverControllerNetworkMessageNameVisitor) + ".");
        }

    }
    public ArrayList<NetworkMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clearSentMessages() {
        sentMessages.clear();
    }

}