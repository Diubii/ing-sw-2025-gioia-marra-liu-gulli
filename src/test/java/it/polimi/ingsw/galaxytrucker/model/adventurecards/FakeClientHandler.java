package it.polimi.ingsw.galaxytrucker.model.adventurecards;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.controller.FakeClientController;
import it.polimi.ingsw.galaxytrucker.controller.GameController;
import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import it.polimi.ingsw.galaxytrucker.view.Tui.FakeTUI;
import it.polimi.ingsw.galaxytrucker.view.Tui.Tui;
import it.polimi.ingsw.galaxytrucker.view.View;
import it.polimi.ingsw.galaxytrucker.visitors.Network.ClientNetworkMessageVisitor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageCouplingVisitor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.UUID;

public class FakeClientHandler implements ClientHandler {
    private final ServerController serverController;
    private final FakeClientController fakeClientController;
    private final ArrayList<NetworkMessage> mockResponses;
    private final NetworkMessageVisitor serverControllerNetworkMessageVisitor;
    private final ClientNetworkMessageVisitor clientNetworkMessageVisitor;
    private final NetworkMessageCouplingVisitor serverControllerNetworkMessageCouplingVisitor = new NetworkMessageCouplingVisitor();
    private final NetworkMessageNameVisitor serverControllerNetworkMessageNameVisitor = new NetworkMessageNameVisitor();
    private final ArrayList<NetworkMessage> sentMessages = new ArrayList<>();
    private final UUID clientID;


    public FakeClientHandler(ServerController serverController, ArrayList<NetworkMessage> mockResponses) {
        clientID = UUID.randomUUID();
        this.serverController = serverController;

        FakeClientController fakeClientController = new FakeClientController(null, true, serverController, this);

        PrintStream out = new PrintStream(OutputStream.nullOutputStream()); //Non fa printare nulla
        View view = new FakeTUI(System.out, true, fakeClientController);
        fakeClientController.setView(view);

        this.fakeClientController = fakeClientController;

        clientNetworkMessageVisitor = new ClientNetworkMessageVisitor(fakeClientController);
        serverControllerNetworkMessageVisitor = new NetworkMessageVisitor(this.serverController, this);

        if(mockResponses == null)
            this.mockResponses = new ArrayList<>();
        else
            this.mockResponses = new ArrayList<>(mockResponses);
    }

    public void setMockResponses(ArrayList<NetworkMessage> responses) {
        this.mockResponses.clear();
        this.mockResponses.addAll(responses);
    }

    public FakeClientController getFakeClientController() {
        return fakeClientController;
    }

    @Override
    public UUID getClientID() {
        return clientID;
    }

    @Override
    public void sendMessage(NetworkMessage message) {
        message.accept(clientNetworkMessageVisitor);

        sentMessages.add(message);
        LobbyManager game = serverController.getLobbyFromHandler(this);
        if (game == null) return;

        GameController gameController = game.getGameController();
        if (gameController == null) return;

        CardContext currentCardContext = gameController.getCurrentCardContext();
        if (currentCardContext == null) return;

        Integer expectedNetworkMessagesOfTheSameType = serverController.getLobbyFromHandler(this).getGameController().getCurrentCardContext().getExpectedNumberOfNetworkMessagesPerType().get(message.accept(serverControllerNetworkMessageCouplingVisitor));

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