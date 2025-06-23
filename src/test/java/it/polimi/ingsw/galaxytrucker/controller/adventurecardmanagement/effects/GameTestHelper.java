package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.FakeClientHandler;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
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
        public HashMap<String, ClientHandler> nicknameToHandlerMap;
    }

    public static GameTestContext setupGame(Map<String, ArrayList<NetworkMessage>> responses, ArrayList<Player> players) {
        final ServerController serverController;
        int createdGameId = -1;
        try {
            serverController = new ServerController();
            serverController.setSynchronousExecution(true);
        } catch (RemoteException e) {
            System.err.println("Couldn't export ServerController: " + e.getMessage() + ".");
            return null;
        }

        HashMap<String, ClientHandler> nicknameToHandlerMap = new HashMap<>();

        for (Player p : players) {
            FakeClientHandler fakeClientHandler = new FakeClientHandler(serverController, responses.get(p.getNickName()));
            NicknameRequest nicknameRequest = new NicknameRequest(p.getNickName());

            try {
                serverController.handleNicknameRequest(nicknameRequest, fakeClientHandler);
                serverController.addClient(fakeClientHandler);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            if (p.equals(players.getFirst())) {
                CreateRoomRequest createRoomRequest = new CreateRoomRequest(players.size(), false, p.getNickName());
                try {

                    serverController.handleCreateRoomRequest(createRoomRequest, fakeClientHandler);
                    createdGameId = serverController.getLastCreatedGameId();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                JoinRoomRequest joinRoomRequest = new JoinRoomRequest(createdGameId, p.getNickName());
                try {
                    serverController.handleJoinRoomRequest(joinRoomRequest, fakeClientHandler);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            nicknameToHandlerMap.put(p.getNickName(), fakeClientHandler);
        }

        AtomicInteger position = new AtomicInteger();
        AtomicInteger id = new AtomicInteger(17);
        players.forEach(p -> {
            try {
                serverController.handleFinishBuildingRequest(new FinishBuildingRequest(p.getShip(), null), nicknameToHandlerMap.get(p.getNickName()));
                serverController.handleAskPositionResponse(new AskPositionResponse(id.incrementAndGet(), position.incrementAndGet()), nicknameToHandlerMap.get(p.getNickName()));
                serverController.handleCheckShipStatusRequest(new CheckShipStatusRequest(), nicknameToHandlerMap.get(p.getNickName()));
                serverController.handleCrewInitUpdate(new CrewInitUpdate(), nicknameToHandlerMap.get(p.getNickName()));
                serverController.handleReadyTurnRequest(new ReadyTurnRequest(), nicknameToHandlerMap.get(p.getNickName()));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        LobbyManager lobby = serverController.getLobbyFromHandler(nicknameToHandlerMap.get(players.getFirst().getNickName()));
        lobby.getGameController().getCardDeckTest().clear();

        GameTestContext context = new GameTestContext();
        context.serverController = serverController;
        context.lobby = lobby;
        context.nicknameToHandlerMap = nicknameToHandlerMap;
        return context;
    }
    public static GameTestContext setupGameForBuildingPhase(Map<String, ArrayList<NetworkMessage>> responses, ArrayList<Player> players) {
        final ServerController serverController;
        int createdGameId = -1;
        try {
            serverController = new ServerController();
            serverController.setSynchronousExecution(true);
        } catch (RemoteException e) {
            System.err.println("Couldn't export ServerController: " + e.getMessage() + ".");
            return null;
        }

        HashMap<String, ClientHandler> nicknameToHandlerMap = new HashMap<>();

        for (Player p : players) {
            FakeClientHandler fakeClientHandler = new FakeClientHandler(serverController, responses.get(p.getNickName()));
            NicknameRequest nicknameRequest = new NicknameRequest(p.getNickName());

            try {
                serverController.handleNicknameRequest(nicknameRequest, fakeClientHandler);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            if (p.equals(players.getFirst())) {
                CreateRoomRequest createRoomRequest = new CreateRoomRequest(players.size(), false, p.getNickName());
                try {

                    serverController.handleCreateRoomRequest(createRoomRequest, fakeClientHandler);
                    createdGameId = serverController.getLastCreatedGameId();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                JoinRoomRequest joinRoomRequest = new JoinRoomRequest(createdGameId, p.getNickName());
                try {
                    serverController.handleJoinRoomRequest(joinRoomRequest, fakeClientHandler);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            nicknameToHandlerMap.put(p.getNickName(), fakeClientHandler);
        }


        LobbyManager lobby = serverController.getLobbyFromHandler(nicknameToHandlerMap.get(players.getFirst().getNickName()));
        lobby.getGameController().getCardDeckTest().clear();

        GameTestContext context = new GameTestContext();
        context.serverController = serverController;
        context.lobby = lobby;
        context.nicknameToHandlerMap = nicknameToHandlerMap;
        return context;
    }
}
