package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.SelectPlanetResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private GameTestContext setupGame(Map<String, ArrayList<NetworkMessage>> responses,ArrayList<Player> players) {
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


    /**
     * Tests the effect of the "Nave abbandonata" (Abandoned Ship) adventure card.
     * <p>
     * This test simulates a scenario in which:
     * <ul>
     *   <li>Player A chooses not to activate the card effect.</li>
     *   <li>Player B accepts the effect, loses all their crew, and gains 3 credits.</li>
     *   <li>Since the effect is limited to one successful player, Player C is <b>not</b> prompted at all.</li>
     * </ul>
     *
     * The test verifies the following:
     * <ul>
     *   <li>Correct credit and crew count updates for each player.</li>
     *   <li>Players with no remaining crew are removed from the ranked player list.</li>
     * </ul>
     *
     * <p>
     * The response flow is simulated using {@link MockResponsesFactory#forAbandonedShip_A()}, which mocks player decisions and crew discards.
     */
    @Test
    void testAbandonedShip_A() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Nave abbandonata", 2);
        AdventureCard card =cards.getFirst();
        assertNotNull(card);
        assertEquals("Nave abbandonata", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof AbandonedShip);

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forAbandonedShip_A();
        GameTestContext ctx = setupGame(responses,players);
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

        //AbandonedShip Test A: A sceglie di non accettare l'effetto della carta, mentre B e C scelgono di accettarlo
        assertEquals(0, first.getNCredits());
        assertEquals(2, first.getShip().getnCrew());
        assertEquals(3, second.getNCredits());
        assertEquals(0, second.getShip().getnCrew());
        assertEquals(0, third.getNCredits());
        assertEquals(2, third.getShip().getnCrew());
        assertEquals(2, ctx.lobby.getGameController().getRankedPlayers().size());
        assertEquals(first, ctx.lobby.getGameController().getRankedPlayers().getFirst());
        assertEquals(third, ctx.lobby.getGameController().getRankedPlayers().get(1));
    }


    /**
     * Abandoned Ship - Test B
     * <p>
     * This test covers the scenario where all players have the default starting ship configuration,
     * each with only 2 crew members — not enough to activate the effect of the "Nave abbandonata" (Abandoned Ship) card.
     * <p>
     * Compared to {@link #testAbandonedShip_A()}, where some players successfully activate the effect,
     * in this test:
     * <ul>
     *   <li>All players choose to activate the effect (via mock responses).</li>
     *   <li>However, none meet the crew requirement (≥3), so the effect is skipped entirely.</li>
     *   <li>No credits are awarded, and no crew are lost.</li>
     * </ul>
     *
     * <p>Responses are simulated using {@link MockResponsesFactory#forAbandonedShip_B()}.
     */
    @Test
    void testAbandonedShip_B() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Nave abbandonata", 2);
        AdventureCard card = cards.get(1);
        assertNotNull(card);
        assertEquals("Nave abbandonata", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof AbandonedShip);
        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forAbandonedShip_B();
        GameTestContext ctx = setupGame(responses,players);
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



        //AbandonedShip Test b: Tutti i giocatori non hanno abbastanza crew, si salta la Nave Abbandonata.
        assertEquals(0, first.getNCredits());
        assertEquals(2, first.getShip().getnCrew());
        assertEquals(0, second.getNCredits());
        assertEquals(2, second.getShip().getnCrew());
        assertEquals(0, third.getNCredits());
        assertEquals(2, third.getShip().getnCrew());

    }


    /**
 * Tests the behavior of the "Abandoned Station" adventure card when only Player A has
 * enough crew to activate the card and chooses to do so.
 *
 * <p>The test checks the following conditions:
 * <ul>
 *     <li>Player A activates the card and receives the additional goods and loads them in his ship at the position 2,3.</li>
 *     <li>Players B and C do not activate the card and retain their initial state.</li>
 *     <li>Credits remain unchanged for all players.</li>
 *     <li>The goods from the Abandoned Station card are correctly loaded into Player A's cargo hold.</li>
 *     <li>The position of Player A on the flight board is updated as expected.</li>
 * </ul>
 *
 * <p>The setup uses mocked player responses via {@link MockResponsesFactory#forAbandonedStation_A}.
 * A mock ship is assigned to Player A to control the initial state before the card is triggered.
 */
    @Test
    void testAbandonedStation_A() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Stazione abbandonata", 2);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Stazione abbandonata", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof AbandonedStation);

        AbandonedStation abandonedStationCard = (AbandonedStation) card;


        GameTestContext ctx = setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player first = rankedPlayers.getFirst();
        first.replaceShip(MockShipFactory.createMockShip()); //per test a non serve
        Player second =rankedPlayers.get(1);
        Player third = rankedPlayers.get(2);

        int positionFirstPlayer = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(first.getColor());
        System.out.println("Player 'A' position : "  + positionFirstPlayer );
        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forAbandonedStation_A(rankedPlayers, abandonedStationCard);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );

        positionFirstPlayer = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(first.getColor());
        Ship newShipA = first.getShip();
        Position goodsPositions = new Position(2,3);

        Component c = newShipA.getComponentFromPosition(goodsPositions);
        GenericCargoHolds hold = (GenericCargoHolds) c;
        ArrayList <Good> loadedGoods =  hold.getGoods();
        //AbandonedStationTest a: sola Player a ha abbastanza crew e ha scelto di attivare l'effetto della carta
        System.out.println("Player 'A' position : "  + positionFirstPlayer);
        assertEquals(((AbandonedStation) card).getGoods(),loadedGoods);
        assertEquals(0, first.getNCredits());

        assertEquals(6, first.getShip().getnCrew());
        assertEquals(0, third.getNCredits());
        assertEquals(2, third.getShip().getnCrew());
        assertEquals(0, second.getNCredits());
        assertEquals(2, second.getShip().getnCrew());
        assertEquals(5,positionFirstPlayer);


    }


    /**
     * Tests the behavior of the "Abandoned Station" adventure card when none of the players
     * have enough crew to activate the card.
     *
     * <p>The test verifies that:
     * <ul>
     *     <li>No player activates the card effect.</li>
     *     <li>No credits are awarded to any player.</li>
     * </ul>
     *
     * <p>This scenario ensures that the game correctly skips the effect of the
     * Abandoned Station card when all players are ineligible due to insufficient crew (cart requires 5 crew).
     */
    @Test
    void testAbandonedStation_B() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Stazione abbandonata", 2);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Stazione abbandonata", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof AbandonedStation);

        AbandonedStation abandonedStationCard = (AbandonedStation) card;


        GameTestContext ctx = setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player first = rankedPlayers.getFirst();
        Player second =rankedPlayers.get(1);
        Player third = rankedPlayers.get(2);

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );


//        AbandonedStationTest a: Tutti i giocatori non hanno abbastanza crew, si salta la Stazione Abbandonata.
        assertEquals(0, first.getNCredits());
        assertEquals(2, first.getShip().getnCrew());
        assertEquals(0, third.getNCredits());
        assertEquals(2, third.getShip().getnCrew());
        assertEquals(0, second.getNCredits());
        assertEquals(2, second.getShip().getnCrew());


    }


    /**
     * Unit test to validate the behavior of the "Open Space" adventure card when players have different propulsion setups.
     * <p>
     * Scenario:
     * <ul>
     *   <li>Player A has a double engine and a battery, activates the double engine, and moves forward 2 spaces.</li>
     *   <li>Player B has a double engine and a single engine, but does not activate the double engine, thus only moves forward 2 spaces.</li>
     *   <li>Player C has no engine modules and is removed from the game due to immobility.</li>
     * </ul>
     *
     * This test verifies:
     * <ol>
     *   <li>The "Spazio aperto" card is correctly loaded and instantiated as an {@link OpenSpace} object.</li>
     *   <li>The game context is correctly set up with mock ships and predefined responses for the Open Space effect.</li>
     *   <li>Player A and Player B are correctly updated on the flight board according to their engine configurations.</li>
     *   <li>The ship modules (batteries and engines) reflect correct usage and remaining power after movement.</li>
     *   <li>Immobile players (e.g., Player C) are properly removed from the game.</li>
     * </ol>
     * @see OpenSpace
     */
    @Test
    void testOpenSpaceEffect() {

        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Spazio aperto", 3);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Spazio aperto", card.getName());
        assertInstanceOf(OpenSpace.class, card);

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forOpenSpace();

        GameTestContext ctx = setupGame(responses,players);
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        first.replaceShip(MockShipFactory.createMockShip());
        Player second = ctx.lobby.getGameController().getRankedPlayers().get(1);
        second.replaceShip(MockShipFactory.createMockShip2());
        Player third = ctx.lobby.getGameController().getRankedPlayers().get(2);
        int positionFirstPlayer = 0;
        int positionSecondPlayer = 0;
        FlightBoard flightBoard = ctx.lobby.getRealGame().getFlightBoard();
        positionFirstPlayer = flightBoard.getPlayerPosition(first.getColor());
        positionSecondPlayer = flightBoard.getPlayerPosition(second.getColor());
        System.out.println("PlayerA's previous position : "  + positionFirstPlayer);
        System.out.println("PlayerB's previous position : "  + positionSecondPlayer);

        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );
        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        first = rankedPlayers.getFirst();
        second =  rankedPlayers.get(1);


        positionFirstPlayer = flightBoard.getPlayerPosition(first.getColor());
        positionSecondPlayer = flightBoard.getPlayerPosition(second.getColor());

        Ship shipA = rankedPlayers.get(0).getShip();
        Ship shipB = rankedPlayers.get(1).getShip();

        System.out.println("PlayerA's current position : "  + positionFirstPlayer);
        System.out.println("PlayerB's current position : "  + positionSecondPlayer);


        Tile batteryTileA = shipA.getTileFromPosition(new Position(2,2));
        BatterySlot batterySlotA =  (BatterySlot) batteryTileA.getMyComponent();

        int numBatteryA = batterySlotA.getBatteriesLeft();
        int powerEngineA =  shipA.calculateEnginePower();

        Tile batteryTileB = shipB.getTileFromPosition(new Position(2,2));
        BatterySlot batterySlotB =  (BatterySlot) batteryTileB.getMyComponent();

        int numBatteryB = batterySlotB.getBatteriesLeft();
        int powerEngineB =  shipB.calculateEnginePower();

        assertEquals(1,numBatteryA);
        assertEquals(1,powerEngineA);
        assertEquals(2,numBatteryB);
        assertEquals(2,powerEngineB);
        assertEquals(8, positionFirstPlayer);
        assertEquals(5, positionSecondPlayer);

        int size = rankedPlayers.size();
        assertEquals(2, size);

    }


    /**
     * Tests the effect of the "Epidemic" adventure card.
     *
     *
     * <p>Expected behavior:</p>
     * <ul>
     *   <li>The card is successfully drawn and is an instance of Epidemic.</li>
     *   <li>The crew members in connected housing units are correctly affected by the card’s effect.</li>
     *   <li>Verify that crew members between two connected housing units (position1 and position2) are only removed once to avoid double subtraction.</li>
     *   <li>Verify that if a housing unit is connected to multiple other housing units, its crew members are removed accordingly (e.g., hP1_1 connected to two units should lose 2 crew members).</li>
     *   <li>Verify that the effect works correctly between a CentralHousingUnit (e.g., hP3_2) and a ModularHousingUnit (e.g., hP4_2), reducing crew members on both sides.</li>
     * </ul>
     */
    @Test
    void testEpidemicEffect_NormalConditions() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Epidemia", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Epidemia", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof Epidemic);

        Epidemic epidemic = (Epidemic) card;

        GameTestContext ctx = setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();

        first.replaceShip(MockShipFactory.createShipWithConnectedHousingUnits());

        Ship shipA = first.getShip();
        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );

        first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        shipA = first.getShip();

        int crewNumberA = shipA.getnCrew();
        System.out.println("Crew number: " + crewNumberA);

        CentralHousingUnit hP3_2 = (CentralHousingUnit) shipA.getComponentFromPosition(new Position(3, 2));
        CentralHousingUnit hP4_2 = (CentralHousingUnit) shipA.getComponentFromPosition(new Position(4, 2));

        CentralHousingUnit hP1_1 = (CentralHousingUnit) shipA.getComponentFromPosition(new Position(1, 1));
        CentralHousingUnit hP1_2 = (CentralHousingUnit) shipA.getComponentFromPosition(new Position(1, 2));
        CentralHousingUnit hP2_1 = (CentralHousingUnit) shipA.getComponentFromPosition(new Position(2, 1));

        assertEquals(1, hP3_2.getNCrewMembers());
        assertEquals(1, hP4_2.getNCrewMembers());

        assertEquals(0, hP1_1.getNCrewMembers());

        assertEquals(1, hP1_2.getNCrewMembers());
        assertEquals(1, hP2_1.getNCrewMembers());

    }


    /**
     * Tests the Epidemic card effect when one of the connected housing units has zero crew members.
     *
     * <p>Expected behavior:</p>
     * <ul>
     *   <li>Housing units with zero crew members are skipped and not affected by the epidemic effect.</li>
     *   <li>Connected housing units with crew members remain unchanged since the connected unit has no crew.</li>
     * </ul>
     */
    @Test
    void testEpidemicEffect_NoCrew() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Epidemia", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Epidemia", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof Epidemic);

        Epidemic epidemic = (Epidemic) card;

        GameTestContext ctx = setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();

        first.replaceShip(MockShipFactory.createShipWithConnectedHousingUnits());

        Ship shipA = first.getShip();
        Position pos4_2 = new Position(4,2);
        CentralHousingUnit hP4_2 = (CentralHousingUnit) shipA.getComponentFromPosition(pos4_2);
        hP4_2.setHumanCrewNumber(0);
        first.replaceShip(shipA);

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );

        first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        shipA = first.getShip();

        int crewNumberA = shipA.getnCrew();
        System.out.println("Crew number: " + crewNumberA);

        CentralHousingUnit hP3_2 = (CentralHousingUnit) shipA.getComponentFromPosition(new Position(3, 2));
        hP4_2 = (CentralHousingUnit) shipA.getComponentFromPosition(pos4_2);

        assertEquals(2, hP3_2.getNCrewMembers());
        assertEquals(0, hP4_2.getNCrewMembers());

    }


    /**
     * Tests the effect of the "Stardust" (Polvere di stelle) card on player positions.
     *
     * <p>Test setup:
     * <ul>
     *   <li>Players A, B, and C are each assigned mock ships with different numbers of exposed connectors: 5, 7, and 4 respectively.</li>
     *   <li>The initial positions of players A, B, and C are 6, 3, and 1 respectively.</li>
     * </ul>
     *
     * <p>Expected behavior:
     * <ol>
     *   <li>Each player moves backward according to the number of their exposed connectors.</li>
     *   <li>Player B moves one additional step backward because he passes Player C during the retreat.</li>
     *   <li>After moving backward, players are reordered in descending order of their new positions.</li>
     * </ol>
     *
     * <p>Assertions:
     * <ul>
     *   <li>Player positions are updated correctly based on the exposed connectors and interaction rules.</li>
     *   <li>The total number of players remains unchanged.</li>
     * </ul>
     */
    @Test
    void testStardustEffect() {

        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Polvere di stelle", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Polvere di stelle", card.getName());


        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.emptyResponsesFor(players);
        GameTestContext ctx = setupGame(responses,players);
        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        first.replaceShip(MockShipFactory.createMockShip());
        Player second = ctx.lobby.getGameController().getRankedPlayers().get(1);
        second.replaceShip(MockShipFactory.createMockShip2());
        Player third = ctx.lobby.getGameController().getRankedPlayers().get(2);
        int positionFirstPlayer = 0;
        int positionSecondPlayer = 0;
        int positionThirdPlayer = 0;
        FlightBoard flightBoard = ctx.lobby.getRealGame().getFlightBoard();
        positionFirstPlayer = flightBoard.getPlayerPosition(first.getColor());
        positionSecondPlayer = flightBoard.getPlayerPosition(second.getColor());
        positionThirdPlayer = flightBoard.getPlayerPosition(third.getColor());
        System.out.println("PlayerA's previous position : "  + positionFirstPlayer);
        System.out.println("PlayerB's previous position : "  + positionSecondPlayer);
        System.out.println("PlayerC's previous position : "  + positionThirdPlayer);

        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(first.getNickName())
        );
        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        first = rankedPlayers.getFirst();
        second =  rankedPlayers.get(1);


        positionFirstPlayer = flightBoard.getPlayerPosition(first.getColor());
        positionSecondPlayer = flightBoard.getPlayerPosition(second.getColor());
        positionThirdPlayer = flightBoard.getPlayerPosition(third.getColor());


        System.out.println("PlayerA's current position : "  + positionFirstPlayer);
        System.out.println("PlayerB's current position : "  + positionSecondPlayer);
        System.out.println("PlayerC's current position : "  + positionThirdPlayer);


        assertEquals(1, positionFirstPlayer);
        assertEquals(19, positionSecondPlayer);
        assertEquals(21, positionThirdPlayer);

        int size = rankedPlayers.size();
        assertEquals(3, size);

    }


    @Test
    void testPlanetsEffect_sendsCorrectPlanetListToPlayer(){
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Pianeti", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Planets);
        Planets planetCard = (Planets) card;

        planetCard.getPlanets().get(0).setOccupied(true);
        planetCard.getPlanets().get(2).setOccupied(true);

        GameTestContext ctx = setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        Player currentPlayer = players.get(0);
        CardContext cardContext = new CardContext(ctx.lobby, planetCard);

        cardContext.setCurrentPlayer(currentPlayer);

        FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(currentPlayer.getNickName());
        handler.clearSentMessages();

        PlanetsEffect.sendSelectPlanetRequest(cardContext);
        ArrayList<NetworkMessage> sentMessages = handler.getSentMessages();

        assertEquals(1, sentMessages.size());
        assertTrue(sentMessages.get(0) instanceof SelectPlanetRequest);

        SelectPlanetRequest request = (SelectPlanetRequest) sentMessages.get(0);

        List<Planet> expectedAvailablePlanets = planetCard.getPlanets().stream()
                .filter(p -> !p.isOccupied()).toList();

        assertEquals(expectedAvailablePlanets.size(), request.getLandablePlanets().size());
        assertTrue(request.getLandablePlanets().containsAll(expectedAvailablePlanets));

    }
    @Test
    void testPlanetsEffect_NormalConditions() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Pianeti", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Planets);
        Planets planetCard = (Planets) card;

        GameTestContext ctx = setupGame(MockResponsesFactory.emptyResponsesFor(players), players);


        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forPlanet_NormalConditions(rankedPlayers, planetCard);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


//        Planet planet1 =  planetCard.getPlanets().get(0);
//        Planet planet2 =  planetCard.getPlanets().get(1);;
//        Planet planet3 =  planetCard.getPlanets().get(2);
//        Planet planet4 =  planetCard.getPlanets().get(3);

        // Step 3: 模拟响应 A 选择 planet1，B 选择 planet2，C 无法选
//        Map<String, ArrayList<NetworkMessage>> responses = Map.of(
//                playerA.getNickName(), ArrayList.of(new SelectPlanetResponse(planet1, 0)),
//                playerB.getNickName(), List.of(new SelectPlanetResponse(planet2, 1)),
//                playerC.getNickName(), List.of(new SelectPlanetResponse(null, -1)) // 玩家 C 没选
//        );
//        responses.forEach((nick, msgs) -> {
//            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
//            handler.setMockResponses(msgs);
//        });

        // Step 4: 添加卡牌 & 触发
        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(playerA.getNickName())
        );

        // Step 5: 验证断言

        // 行星是否被标记为占据
//        assertTrue(planet1.isOccupied());
//        assertTrue(planet2.isOccupied());

        // 玩家是否得到了正确的 ShipUpdate（可以加额外断言）
        Ship updatedShipA = playerA.getShip();
        assertNotNull(updatedShipA);


        int posA = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerA.getColor());
        int posB = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerB.getColor());
        int posC = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerC.getColor());

//        assertEquals(posA, posB); // A 和 B 应该没被罚时，仍然领先
//        assertEquals(posC, posB); // C 没选，也不动

        // 如果你后续调用 movePlayers(context)，可断言 A, B 被后退
    }



    @Test
    void testCombatZone() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Zona Guerra", 3);
        AdventureCard combatZone = cards.getFirst();
        assertNotNull(combatZone);
        assertEquals("Zona Guerra", combatZone.getName());
        assertInstanceOf(CombatZone.class, combatZone);
    }
}