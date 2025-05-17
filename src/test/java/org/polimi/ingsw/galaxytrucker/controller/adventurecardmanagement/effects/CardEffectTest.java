package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.FakeClientHandler;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CheckShipStatusResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.AskPositionUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardEffectTest {
    private final String playerANickname = "A";
    private final String playerBNickname = "B";
    private final String playerCNickname = "C";

    private final ArrayList<Player> players = new ArrayList<>(
            List.of(
                    new Player(playerANickname, 0, 1, false),
                    new Player(playerBNickname, 0, 2, false),
                    new Player(playerCNickname, 0, 3, false)
            )
    );

    private final ArrayList<NetworkMessage> playerAResponseMockResponses = new ArrayList<>(
            List.of(
                    new ActivateAdventureCardResponse(false),
                    new DiscardCrewMembersResponse(new ArrayList<>(
                            List.of(
                                    new Position(3, 2)
                            )
                    ))
            )
    );
    private final ArrayList<NetworkMessage> playerBResponseMockResponses = new ArrayList<>(
            List.of(
                    new ActivateAdventureCardResponse(true),
                    new DiscardCrewMembersResponse(new ArrayList<>(
                            List.of(
                                    new Position(3, 2)
                            )
                    ))
            )
    );
    private final ArrayList<NetworkMessage> playerCResponseMockResponses = new ArrayList<>(
            List.of(
                    new ActivateAdventureCardResponse(true),
                    new DiscardCrewMembersResponse(new ArrayList<>(
                            List.of(
                                    new Position(3, 2)
                            )
                    ))
            )
    );

    private final HashMap<String, ArrayList<NetworkMessage>> mockResponses = new HashMap<>(
            Map.of(
                    playerANickname, playerAResponseMockResponses,
                    playerBNickname, playerBResponseMockResponses,
                    playerCNickname, playerCResponseMockResponses
            )
    );

    @Test
    void cardEffectTest() {

    }

    @BeforeEach
    public void setupGame() {
        ServerController serverController = new ServerController();
        HashMap<String, ClientHandler> nicknameToHandlerMap = new HashMap<>();

        players.forEach(p -> {
            FakeClientHandler fakeClientHandler = new FakeClientHandler(serverController, mockResponses.get(p.getNickName()));

            NicknameRequest nicknameRequest = new NicknameRequest(p.getNickName());
            try {
                serverController.handleNicknameRequest(nicknameRequest, fakeClientHandler);
            } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
                throw new RuntimeException(e);
            }

            if (p.equals(players.getFirst())) {
                CreateRoomRequest createRoomRequest = new CreateRoomRequest(players.size(), false, p.getNickName());
                try {
                    serverController.handleCreateRoomRequest(createRoomRequest, fakeClientHandler);
                } catch (TooManyPlayersException | PlayerAlreadyExistsException | InvalidTilePosition e) {
                    throw new RuntimeException(e);
                }
            } else {
                JoinRoomRequest joinRoomRequest = new JoinRoomRequest(serverController.getLobbyInfos().size() - 1, p.getNickName());
                try {
                    serverController.handleJoinRoomRequest(joinRoomRequest, fakeClientHandler);
                } catch (TooManyPlayersException | PlayerAlreadyExistsException | IOException | InvalidTilePosition e) {
                    throw new RuntimeException(e);
                }
            }
            nicknameToHandlerMap.put(p.getNickName(), fakeClientHandler);
        });

        System.out.println("Here!!!");

        AtomicInteger position = new AtomicInteger();
        AtomicInteger id = new AtomicInteger(17);
        players.forEach(p -> {
            FinishBuildingRequest finishBuildingRequest = new FinishBuildingRequest(p.getShip(), null);
            serverController.handleFinishBuildingRequest(finishBuildingRequest, nicknameToHandlerMap.get(p.getNickName()));
            AskPositionResponse askPositionResponse = new AskPositionResponse(id.incrementAndGet(), position.incrementAndGet());
            serverController.handleAskPositionResponse(askPositionResponse, nicknameToHandlerMap.get(p.getNickName()));
        });

        players.forEach(p -> {
            CheckShipStatusRequest checkShipStatusRequest = new CheckShipStatusRequest();
            serverController.handleCheckShipStatusRequest(checkShipStatusRequest, nicknameToHandlerMap.get(p.getNickName()));
        });

        players.forEach(p -> {
            CrewInitUpdate crewInitUpdate = new CrewInitUpdate();
            serverController.handleCrewInitUpdate(crewInitUpdate, nicknameToHandlerMap.get(p.getNickName()));
        });

        players.forEach(p -> {
            ReadyTurnRequest readyTurnRequest = new ReadyTurnRequest();
            serverController.handleReadyTurnRequest(readyTurnRequest, nicknameToHandlerMap.get(p.getNickName()));
        });

        LobbyManager game = serverController.getLobbyFromHandler(nicknameToHandlerMap.get(players.getFirst().getNickName()));

        Player firstPlayer = game.getGameController().getRankedPlayers().getFirst();
        Player secondPlayer = game.getGameController().getRankedPlayers().get(1);
        Player thirdPlayer = game.getGameController().getRankedPlayers().get(2);
        DrawAdventureCardRequest drawAdventureCardRequest = new DrawAdventureCardRequest();
        serverController.handleDrawAdventureCardRequest(drawAdventureCardRequest, nicknameToHandlerMap.get(firstPlayer.getNickName()));

        assertEquals(2, firstPlayer.getShip().getnCrew());
        assertEquals(0, secondPlayer.getShip().getnCrew());
        assertEquals(2, thirdPlayer.getShip().getnCrew());

        assertEquals(0, firstPlayer.getNCredits());
        assertEquals(3, secondPlayer.getNCredits());
        assertEquals(0, thirdPlayer.getNCredits());

        assertEquals(2, game.getGameController().getRankedPlayers().size());
    }
}