package it.polimi.ingsw.galaxytrucker.controller;

import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.FakeClientHandler;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GameTestHelper {

    public static class GameTestContext {
        public ServerController serverController;
        public LobbyManager lobby;
        public static HashMap<String, ClientHandler> nicknameToHandlerMap;
        public static HashMap<String, ClientController> nicknameToClientControllerMap;
    }

    public static GameTestContext setupGame(Map<String, ArrayList<NetworkMessage>> responses, ArrayList<Player> players) {
        final ServerController serverController;
        final int maxPlayers = players.size();
        final boolean learningFlight = false;

        int createdGameId = -1;
        try {
            serverController = new ServerController();
            serverController.setSynchronousExecution(true);
        } catch (RemoteException e) {
            System.err.println("Couldn't export ServerController: " + e.getMessage() + ".");
            return null;
        }

        GameTestContext.nicknameToHandlerMap = new HashMap<>();
        GameTestContext.nicknameToClientControllerMap = new HashMap<>();

        for (Player p : players) {
            FakeClientHandler fakeClientHandler = new FakeClientHandler(serverController, responses.get(p.getNickName()));
            ClientController clientController = fakeClientHandler.getFakeClientController();

            clientController.handleNicknameInput(p.getNickName());

            if (p.equals(players.getFirst())) {
                clientController.handleCreateChoice(maxPlayers, learningFlight);
                createdGameId = serverController.getLastCreatedGameId();
            } else {
                clientController.handleJoinChoice(createdGameId);
            }

            GameTestContext.nicknameToHandlerMap.put(p.getNickName(), fakeClientHandler);
            GameTestContext.nicknameToClientControllerMap.put(p.getNickName(), clientController);
        }

        AtomicInteger position = new AtomicInteger();
        AtomicInteger id = new AtomicInteger(17);
        players.forEach(p -> {
            ClientController clientController = GameTestContext.nicknameToClientControllerMap.get(p.getNickName());

            AskPositionResponse askPositionResponse = new AskPositionResponse(id.incrementAndGet(), position.incrementAndGet());
            clientController.safeSendMessage(askPositionResponse);
            clientController.handleCheckShipRequest();
            clientController.handleCrewInitUpdate(new CrewInitUpdate());
            clientController.handleReadyTurnRequest();
        });

        LobbyManager lobby = serverController.getLobbyFromHandler(GameTestContext.nicknameToHandlerMap.get(players.getFirst().getNickName()));
        lobby.getGameController().getCardDeckTest().clear();

        GameTestContext context = new GameTestContext();
        context.serverController = serverController;
        context.lobby = lobby;
        return context;
    }

    public static GameTestContext setupGameForBuildingPhase(Map<String, ArrayList<NetworkMessage>> responses, ArrayList<Player> players) {
        final ServerController serverController;
        final int maxPlayers = players.size();
        final boolean learningFlight = false;

        int createdGameId = -1;
        try {
            serverController = new ServerController();
            serverController.setSynchronousExecution(true);
        } catch (RemoteException e) {
            System.err.println("Couldn't export ServerController: " + e.getMessage() + ".");
            return null;
        }

        GameTestContext.nicknameToHandlerMap = new HashMap<>();
        GameTestContext.nicknameToClientControllerMap = new HashMap<>();

        for (Player p : players) {
            FakeClientHandler fakeClientHandler = new FakeClientHandler(serverController, responses.get(p.getNickName()));
            ClientController clientController = fakeClientHandler.getFakeClientController();

            clientController.handleNicknameInput(p.getNickName());

            if (p.equals(players.getFirst())) {
                clientController.handleCreateChoice(maxPlayers, learningFlight);
                createdGameId = serverController.getLastCreatedGameId();
            } else {
                clientController.handleJoinChoice(createdGameId);
            }

            GameTestContext.nicknameToHandlerMap.put(p.getNickName(), fakeClientHandler);
            GameTestContext.nicknameToClientControllerMap.put(p.getNickName(), clientController);
        }


        LobbyManager lobby = serverController.getLobbyFromHandler(GameTestContext.nicknameToHandlerMap.get(players.getFirst().getNickName()));
        lobby.getGameController().getCardDeckTest().clear();

        GameTestContext context = new GameTestContext();
        context.serverController = serverController;
        context.lobby = lobby;
        //context.nicknameToHandlerMap = GameTestContext.nicknameToHandlerMap;
        return context;
    }
}
