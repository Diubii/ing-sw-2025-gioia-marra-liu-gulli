package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.FakeClientHandler;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Planets;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.TileRegistry;
import it.polimi.ingsw.galaxytrucker.network.Heartbeat;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskTrunkResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DrawTileResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomOptionsResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.PlaceTileResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ServerControllerTest {

    Player player1, player2;
    FakeClientHandler handler1, handler2;
    ServerController serverController;
    GameTestHelper.GameTestContext context;

    @BeforeEach
    void setUp() {
        player1 = new Player("TestPlayer", 0, 1, true);
        player2 = new Player("TestPlayer2", 0, 2, true);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        responses.put("TestPlayer", new ArrayList<>());
        responses.put("TestPlayer2", new ArrayList<>());

        context = GameTestHelper.setupGameForBuildingPhase(responses, players);
        handler1 = (FakeClientHandler) context.nicknameToHandlerMap.get("TestPlayer");
        handler2 = (FakeClientHandler) context.nicknameToHandlerMap.get("TestPlayer2");
        serverController = context.serverController;
    }


    @Test
    public void testAddRemoveAndGetClients() {
        FakeClientHandler client1 = new FakeClientHandler(serverController, null);
        FakeClientHandler client2 = new FakeClientHandler(serverController, null);

        serverController.addClient(client1);
        serverController.addClient(client2);

        ArrayList<ClientHandler> clients = serverController.getClients();

        assertEquals(2, clients.size());
        assertTrue(clients.contains(client1));
        assertTrue(clients.contains(client2));

        serverController.removeClient(client1);
        clients = serverController.getClients();
        assertEquals(1, clients.size());
        assertFalse(clients.contains(client1));
        assertTrue(clients.contains(client2));
    }

    @Test
    public void testHandlePlaceTileRequest() throws RemoteException {
        Tile tile = TileRegistry.getFirstTileOfType("Cannon");
        Position pos = new Position(3, 3);

        PlaceTileRequest firstRequest = new PlaceTileRequest(tile, pos);
        serverController.handlePlaceTileRequest(firstRequest, handler1);

        PlaceTileResponse response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof PlaceTileResponse)
                .map(m -> (PlaceTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("VALID", response.getMessage());

        handler1.clearSentMessages();


        PlaceTileRequest secondRequest = new PlaceTileRequest(tile, pos);
        serverController.handlePlaceTileRequest(secondRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof PlaceTileResponse)
                .map(m -> (PlaceTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("OCCUPIED_POS", response.getMessage());
        handler1.clearSentMessages();


        pos = new Position(0, 0);
        PlaceTileRequest thirdRequest = new PlaceTileRequest(tile, pos);
        serverController.handlePlaceTileRequest(thirdRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof PlaceTileResponse)
                .map(m -> (PlaceTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("INVALID_POS", response.getMessage());
        handler1.clearSentMessages();


        int index = 0;
        PlaceTileRequest forthRequest = new PlaceTileRequest(tile, index);
        serverController.handlePlaceTileRequest(forthRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof PlaceTileResponse)
                .map(m -> (PlaceTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("VALID", response.getMessage());
        handler1.clearSentMessages();

    }

    @Test
    public void testHandleDrawAndDiscardTileRequest() throws RemoteException {



        //
        DrawTileRequest drawRequest = new DrawTileRequest();
        serverController.handleDrawTileRequest(drawRequest, handler1);

        DrawTileResponse response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("VALID", response.getErrorMessage());

        handler1.clearSentMessages();


        //
        DiscardTileRequest discardTileRequest = new DiscardTileRequest(response.getTile());
        serverController.handleDiscardTileRequest(discardTileRequest, handler1);

        handler1.clearSentMessages();


        //
        DrawTileRequest drawRequest2 = new DrawTileRequest(response.getTile());
        serverController.handleDrawTileRequest(drawRequest2, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("VALID", response.getErrorMessage());

        handler1.clearSentMessages();


        //
        DrawTileRequest drawRequest3 = new DrawTileRequest(response.getTile());
        serverController.handleDrawTileRequest(drawRequest3, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("TAKEN", response.getErrorMessage());

        handler1.clearSentMessages();


        //

        DrawTileRequest reclaimLastTileRequest = DrawTileRequest.reclaimLastTileRequest();
        serverController.handleDrawTileRequest(reclaimLastTileRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("NO_TILE", response.getErrorMessage());

        handler1.clearSentMessages();

        //
        Tile tile = TileRegistry.getFirstTileOfType("Cannon");
        Position pos = new Position(3, 3);

        PlaceTileRequest firstRequest = new PlaceTileRequest(tile, pos);
        serverController.handlePlaceTileRequest(firstRequest, handler1);

        //
        serverController.handleDrawTileRequest(reclaimLastTileRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("VALID", response.getErrorMessage());

        handler1.clearSentMessages();



        //
        assertNotNull(tile);
        tile.setFixed(true);
        firstRequest = new PlaceTileRequest(tile, pos);
        serverController.handlePlaceTileRequest(firstRequest, handler1);

       ShipUpdate shipUpdate = new ShipUpdate(player1.getShip(),player1.getNickName());
        shipUpdate.setOnlyFix(true);
        serverController.handleShipUpdate(shipUpdate,handler1);


        serverController.handleDrawTileRequest(reclaimLastTileRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("FIXED", response.getErrorMessage());

        handler1.clearSentMessages();


        //
        int index = 0;
        PlaceTileRequest forthRequest = new PlaceTileRequest(tile, index);
        serverController.handlePlaceTileRequest(forthRequest, handler1);

        DrawTileRequest fromReservedSlotRequest = DrawTileRequest.fromReservedSlot(0);
        serverController.handleDrawTileRequest(fromReservedSlotRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("VALID", response.getErrorMessage());

        handler1.clearSentMessages();


        //
        fromReservedSlotRequest = DrawTileRequest.fromReservedSlot(1);
        serverController.handleDrawTileRequest(fromReservedSlotRequest, handler1);

        response = handler1.getSentMessages().stream()
                .filter(m -> m instanceof DrawTileResponse)
                .map(m -> (DrawTileResponse) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No response received"));

        assertEquals("NO_TILE_AT_INDEX", response.getErrorMessage());

        handler1.clearSentMessages();

        //




    }

    @Test
    void testHandleEarlyLandingRequest(){
        player1 = new Player("TestPlayer", 0, 1, true);
        player2 = new Player("TestPlayer2", 0, 2, true);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        responses.put("TestPlayer", new ArrayList<>());
        responses.put("TestPlayer2", new ArrayList<>());

        context = GameTestHelper.setupGame(responses,players);
        ClientHandler clientHandler1 = context.nicknameToHandlerMap.get("TestPlayer");
        ClientHandler clientHandler2 = context.nicknameToHandlerMap.get("TestPlayer2");
        EarlyLandingRequest earlyLandingRequest = new EarlyLandingRequest();
        try {
            context.serverController.handleEarlyLandingRequest(earlyLandingRequest,clientHandler1);
            context.serverController.handleEarlyLandingRequest(earlyLandingRequest,clientHandler2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }


    }
    @Test
    void testTimer() throws RemoteException {
        setUp();
        AskTimerInfoRequest askTimerInfoRequest = new AskTimerInfoRequest();
        serverController.handleAskTimerInfoRequest(askTimerInfoRequest, handler1);
        FlipTimerRequest flipTimerRequest = new FlipTimerRequest();
        serverController.handleFlipTimerRequest(flipTimerRequest, handler1);

        ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
        clientHandlers.add(handler1);
        clientHandlers.add(handler2);



        serverController.startTimer(10,context.lobby.getGameController(),clientHandlers,false,0);

        JoiniRoomOptionsRequest joiniRoomOptionsRequest = new JoiniRoomOptionsRequest();
        serverController.handleJoinRoomOptionsRequest(joiniRoomOptionsRequest, handler1);


    }

    @Test
    void testHeartBeat() throws RemoteException {

        ClientHandler clientHandler = context.nicknameToHandlerMap.get("TestPlayer");
        serverController.startNewHeartbeat(clientHandler);
        HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
        serverController.handleHeartbeatRequest(heartbeatRequest, clientHandler);

    }


}
