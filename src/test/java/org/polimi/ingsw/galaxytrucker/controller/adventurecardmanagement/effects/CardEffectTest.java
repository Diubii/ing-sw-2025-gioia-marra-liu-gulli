package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.MockShipFactory;
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

import static org.junit.jupiter.api.Assertions.*;

class CardEffectTest {

    private static class GameTestContext {
        ServerController serverController;
        LobbyManager lobby;
        HashMap<String, ClientHandler> nicknameToHandlerMap;
    }
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

    @Test
    void cardEffectTest() {

    }


    private GameTestContext setupGame(Map<String, ArrayList<NetworkMessage>> responses)
    {
        ServerController serverController = new ServerController();
        HashMap<String, ClientHandler> nicknameToHandlerMap = new HashMap<>();

        players.forEach(p -> {
            FakeClientHandler fakeClientHandler = new FakeClientHandler(serverController, responses.get(p.getNickName()));

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
            serverController.handleFinishBuildingRequest(new FinishBuildingRequest(p.getShip(), null), nicknameToHandlerMap.get(p.getNickName()));
            serverController.handleAskPositionResponse(new AskPositionResponse(id.incrementAndGet(), position.incrementAndGet()), nicknameToHandlerMap.get(p.getNickName()));
            serverController.handleCheckShipStatusRequest(new CheckShipStatusRequest(), nicknameToHandlerMap.get(p.getNickName()));
            serverController.handleCrewInitUpdate(new CrewInitUpdate(), nicknameToHandlerMap.get(p.getNickName()));
            serverController.handleReadyTurnRequest(new ReadyTurnRequest(), nicknameToHandlerMap.get(p.getNickName()));
        });

        LobbyManager lobby = serverController.getLobbyFromHandler(nicknameToHandlerMap.get(players.getFirst().getNickName()));
        lobby.getGameController().getCardDeckTest().clear();

        GameTestContext context = new GameTestContext();
        context.serverController = serverController;
        context.lobby = lobby;
        context.nicknameToHandlerMap = nicknameToHandlerMap;
        return context;
    }

    @Test
    void testAbandonedShip() throws IOException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Nave abbandonata", 2);
        AdventureCard card =cards.get(0);
//        AdventureCard card = cards.get(1);  //per test b
        assertNotNull(card);
        assertEquals("Nave abbandonata", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof AbandonedShip);

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forAbandonedShip();
        GameTestContext ctx = setupGame(responses);
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);


        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        Player second = ctx.lobby.getGameController().getRankedPlayers().get(1);
        Player third = ctx.lobby.getGameController().getRankedPlayers().get(2);
        System.out.println( "Size Cart:" +ctx.lobby.getGameController().getCardDeckTest().getSize());
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );

        System.out.println( "Size Cart:" +ctx.lobby.getGameController().getCardDeckTest().getSize());
        ctx.lobby.getGameController().getCurrentCardContext().executePhase();

        //AbandonedShip Test a :A sceglie di non accettare l'effetto della carta, mentre B e C scelgono di accettarlo
        assertEquals(0, first.getNCredits());
        assertEquals(2, first.getShip().getnCrew());
        assertEquals(3, second.getNCredits());
        assertEquals(0, second.getShip().getnCrew());
        assertEquals(0, third.getNCredits());
        assertEquals(2, third.getShip().getnCrew());
        assertEquals(2, ctx.lobby.getGameController().getRankedPlayers().size());


        //AbandonedShip Test b: Tutti i giocatori non hanno abbastanza crew, si salta la Nave Abbandonata.
//        assertEquals(0, first.getNCredits());
//        assertEquals(2, first.getShip().getnCrew());
//        assertEquals(0, second.getNCredits());
//        assertEquals(2, second.getShip().getnCrew());
//        assertEquals(0, third.getNCredits());
//        assertEquals(2, third.getShip().getnCrew());

    }


    @Test
    void testAbandonedStation() throws IOException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Stazione abbandonata", 2);
        AdventureCard card =cards.get(0);
        assertNotNull(card);
        assertEquals("Stazione abbandonata", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof AbandonedStation);

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forAbandonedStation();
        GameTestContext ctx = setupGame(responses);
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        first.replaceShip(MockShipFactory.createMockShip()); //per test a non serve

        Player second = ctx.lobby.getGameController().getRankedPlayers().get(1);
        Player third = ctx.lobby.getGameController().getRankedPlayers().get(2);
        System.out.println( "Size Cart:" +ctx.lobby.getGameController().getCardDeckTest().getSize());
        ctx.serverController.handleDrawAdventureCardRequest(

                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );




//        AbandonedStationTest a: Tutti i giocatori non hanno abbastanza crew, si salta la Stazione Abbandonata.
//        assertEquals(0, first.getNCredits());
//        assertEquals(2, first.getShip().getnCrew());
//        assertEquals(0, third.getNCredits());
//        assertEquals(2, third.getShip().getnCrew());
//        assertEquals(0, second.getNCredits());
//        assertEquals(2, second.getShip().getnCrew());

        //AbandonedStationTest b: sola Player a ha abbastanza crew e ha scelto di attivare l'effetto della carta
        //ShipUpdate?
        assertEquals(0, first.getNCredits());
        assertEquals(6, first.getShip().getnCrew());
        assertEquals(0, third.getNCredits());
        assertEquals(2, third.getShip().getnCrew());
        assertEquals(0, second.getNCredits());
        assertEquals(2, second.getShip().getnCrew());



    }

    @Test
    void testOpenSpaceEffect() throws IOException {

        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Spazio aperto", 3);
        AdventureCard card = cards.get(0);
        assertNotNull(card);
        assertEquals("Spazio aperto", card.getName());
        assertTrue(card instanceof OpenSpace);


        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forOpenSpace();

        GameTestContext ctx = setupGame(responses);
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);


        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        first.replaceShip(MockShipFactory.createMockShip());
        Player second = ctx.lobby.getGameController().getRankedPlayers().get(1);
        Player third = ctx.lobby.getGameController().getRankedPlayers().get(2);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );


        ctx.lobby.getGameController().getCurrentCardContext().executePhase();


    }


}