package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.*;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.FakeClientHandler;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameTestHelper {

    public static class GameTestContext {
        public ServerController serverController;
        public LobbyManager lobby;
        public HashMap<String, ClientHandler> nicknameToHandlerMap;
    }

    public static GameTestContext setupGame(Map<String, ArrayList<NetworkMessage>> responses, ArrayList<Player> players) {
        final ServerController serverController;
        try {
            serverController = new ServerController();
        } catch (RemoteException e) {
            System.err.println("Couldn't export ServerController: " + e.getMessage() + ".");
            return null;
        }

        HashMap<String, ClientHandler> nicknameToHandlerMap = new HashMap<>();

        players.forEach(p -> {
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
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                JoinRoomRequest joinRoomRequest = new JoinRoomRequest(serverController.getLobbyInfos().size() - 1, p.getNickName());
                try {
                    serverController.handleJoinRoomRequest(joinRoomRequest, fakeClientHandler);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            nicknameToHandlerMap.put(p.getNickName(), fakeClientHandler);
        });

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
}
