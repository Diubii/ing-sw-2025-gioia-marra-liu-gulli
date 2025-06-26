package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.controller.GameTestHelper;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.*;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DrawAdventureCardRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.SelectPlanetRequest;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class CardEffectTest {


    private final String playerANickname = "A";
    private final String playerBNickname = "B";
    private final String playerCNickname = "C";

    private final ArrayList<Player> players = new ArrayList<>(
            Arrays.asList(
                    new Player(playerANickname, 0, 1, false),
                    new Player(playerBNickname, 0, 2, false),
                    new Player(playerCNickname, 0, 3, false)
            )
    );
    private AdventureCard loadAndAddCard(String name, int index, int nCard) {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType(name, nCard);
        AdventureCard card = cards.get(index);

        assertNotNull(card, "Card should not be null");
        assertEquals(name, card.getName(), "Card name mismatch");
        System.out.println("Carta pescata: " + card.getName());

        return card;
    }

    private void setPlayersShipClientSideAndServerSide(ArrayList<Player> players, ArrayList<Ship> ships) {
        final HashMap<String, ClientController> ntccmap = GameTestHelper.GameTestContext.nicknameToClientControllerMap;

        assertEquals(players.size(), ntccmap.size());
        assertEquals(players.size(), ships.size());

        for(int i=0; i<players.size(); i++) {
            if(ships.get(i) == null) continue;

            players.get(i).replaceShip(ships.get(i));
            ntccmap.get(players.get(i).getNickName()).setShip(ships.get(i));
        }
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
        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(responses, players);
        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        Player second = ctx.lobby.getGameController().getRankedPlayers().get(1);
        Player third = ctx.lobby.getGameController().getRankedPlayers().get(2);
        System.out.println( "Size Cart:" +ctx.lobby.getGameController().getCardDeckTest().getSize());
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

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
        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(responses, players);
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        Player first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        Player second = ctx.lobby.getGameController().getRankedPlayers().get(1);
        Player third = ctx.lobby.getGameController().getRankedPlayers().get(2);
        System.out.println( "Size Cart:" +ctx.lobby.getGameController().getCardDeckTest().getSize());
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

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

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player first = rankedPlayers.getFirst();
        first.replaceShip(MockShipFactory.createMockShip()); //per test a non serve
        Player second =rankedPlayers.get(1);
        Player third = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(),
                        null,
                        null
                )
        ));

        int positionFirstPlayer = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(first.getColor());
        System.out.println("Player 'A' position : "  + positionFirstPlayer );
        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forAbandonedStation_A(rankedPlayers, abandonedStationCard);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

        positionFirstPlayer = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(first.getColor());
        Ship newShipA = first.getShip();
        Position goodsPositions = new Position(2,3);

        Component c = newShipA.getComponentFromPosition(goodsPositions);
        GenericCargoHolds hold = (GenericCargoHolds) c;
        ArrayList <Good> loadedGoods =  hold.getGoods();
        //AbandonedStationTest a: sola Player a ha abbastanza crew e ha scelto di attivare l'effetto della carta
        System.out.println("Player 'A' position : "  + positionFirstPlayer);
        assertEquals(((AbandonedStation) card).getGoods().size(),loadedGoods.size());
        Good loadedGood1 = loadedGoods.get(0);
        Good loadedGood2 = loadedGoods.get(1);
        assertEquals(((AbandonedStation) card).getGoods().get(0).getColor(),loadedGood1.getColor());
        assertEquals(((AbandonedStation) card).getGoods().get(1).getColor(),loadedGood2.getColor());
        assertEquals(0, first.getNCredits());

        assertEquals(6, first.getShip().getnCrew());
        assertEquals(0, third.getNCredits());
        assertEquals(2, third.getShip().getnCrew());
        assertEquals(0, second.getNCredits());
        assertEquals(2, second.getShip().getnCrew());
        assertEquals(5,positionFirstPlayer);


    }

    @Test
    void testAbandonedStation_Abis() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Stazione abbandonata", 2);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Stazione abbandonata", card.getName());
        System.out.println("Carta pescata: " + card.getName());
        assertTrue(card instanceof AbandonedStation);

        AbandonedStation abandonedStationCard = (AbandonedStation) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player first = rankedPlayers.getFirst();
        first.replaceShip(MockShipFactory.createMockShip()); //per test a non serve
        Player second =rankedPlayers.get(1);
        Player third = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(),
                        MockShipFactory.createMockShip(),
                        MockShipFactory.createMockShip()
                )
        ));

        int positionFirstPlayer = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(first.getColor());
        System.out.println("Player 'A' position : "  + positionFirstPlayer );
        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forAbandonedStation_Abis(rankedPlayers, abandonedStationCard);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }


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


        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player first = rankedPlayers.getFirst();
        Player second =rankedPlayers.get(1);
        Player third = rankedPlayers.get(2);

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }


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

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(responses, players);

        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        Player playerB = ctx.lobby.getGameController().getRankedPlayers().get(1);
        Player playerC = ctx.lobby.getGameController().getRankedPlayers().get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(),
                        MockShipFactory.createMockShip2(),
                        null
                )
        ));

        FlightBoard flightBoard = ctx.lobby.getRealGame().getFlightBoard();
        int positionPlayerA = flightBoard.getPlayerPosition(playerA.getColor());
        int positionPlayerB = flightBoard.getPlayerPosition(playerB.getColor());
        int positionPlayerC = flightBoard.getPlayerPosition(playerC.getColor());
        System.out.println("PlayerA's previous position : "  + positionPlayerA);
        System.out.println("PlayerB's previous position : "  + positionPlayerB);
        System.out.println("PlayerC's previous position : "  + positionPlayerC);

        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

        Ship shipA = playerA.getShip();
        Ship shipB = playerB.getShip();
        Ship shipC = playerC.getShip();

        ShipPrintUtils.printShip(shipC);
        positionPlayerA = flightBoard.getPlayerPosition(playerA.getColor());
        positionPlayerB = flightBoard.getPlayerPosition(playerB.getColor());

        System.out.println("PlayerA's current position : "  + positionPlayerA);
        System.out.println("PlayerB's current position : "  + positionPlayerB);


        Tile batteryTileA = shipA.getTileFromPosition(new Position(2,2));
        BatterySlot batterySlotA =  (BatterySlot) batteryTileA.getMyComponent();

        int numBatteryA = batterySlotA.getBatteriesLeft();
        int powerEngineA =  shipA.calculateEnginePower();

        Tile batteryTileB = shipB.getTileFromPosition(new Position(2,2));
        BatterySlot batterySlotB =  (BatterySlot) batteryTileB.getMyComponent();

        int numBatteryB = batterySlotB.getBatteriesLeft();
        int powerEngineB =  shipB.calculateEnginePower();



        assertEquals(1,numBatteryA);
        assertEquals(0,powerEngineA);
        assertEquals(2,numBatteryB);
        assertEquals(1,powerEngineB);
        assertEquals(8, positionPlayerA);
        assertEquals(4, positionPlayerB);

//        int size = rankedPlayers.size();
//        assertEquals(2, size);

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

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();

        Player first = rankedPlayers.getFirst();


        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createShipWithConnectedHousingUnits(),
                        null,
                        null
                )
        ));

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

        first = ctx.lobby.getGameController().getRankedPlayers().getFirst();
        Ship shipA = first.getShip();

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

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();

        Player first = rankedPlayers.getFirst();

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createShipWithConnectedHousingUnits(),
                        null,
                        null
                )
        ));

        Ship shipA = first.getShip();
        Position pos4_2 = new Position(4,2);
        CentralHousingUnit hP4_2 = (CentralHousingUnit) shipA.getComponentFromPosition(pos4_2);
        hP4_2.setHumanCrewNumber(0);
        first.replaceShip(shipA);

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

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
        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(responses,players);
        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();

        Player first = rankedPlayers.getFirst();
        Player second = rankedPlayers.get(1);
        Player third = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(),
                        MockShipFactory.createMockShip2(),
                        null //1
                )
        ));

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

        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(first.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

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
        assertInstanceOf(Planets.class, card);
        Planets planetCard = (Planets) card;

        planetCard.getPlanets().get(0).setOccupied(true);
        planetCard.getPlanets().get(2).setOccupied(true);

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);
        Player currentPlayer = players.get(0);
        CardContext cardContext = new CardContext(ctx.lobby, planetCard);

        cardContext.setCurrentPlayer(currentPlayer);

        FakeClientHandler handler = (FakeClientHandler) GameTestHelper.GameTestContext.nicknameToHandlerMap.get(currentPlayer.getNickName());
        handler.clearSentMessages();

        PlanetsEffect.sendSelectPlanetRequest(cardContext);
        ArrayList<NetworkMessage> sentMessages = handler.getSentMessages();

//        assertEquals(1, sentMessages.size());
//        assertTrue(sentMessages.get(0) instanceof SelectPlanetRequest);

        SelectPlanetRequest request = (SelectPlanetRequest) sentMessages.stream()
                .filter(m -> m instanceof SelectPlanetRequest)
                .map(m -> (SelectPlanetRequest) m)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No SelectPlanetRequest sent"));

        List<Planet> expectedAvailablePlanets = planetCard.getPlanets().stream()
                .filter(p -> !p.isOccupied()).toList();

        assertEquals(expectedAvailablePlanets.size(), request.getLandablePlanets().size());
        assertTrue(request.getLandablePlanets().values().containsAll(expectedAvailablePlanets));

    }
    @Test
    void testPlanetsEffect_NormalConditions() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Pianeti", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Planets);
        Planets planetCard = (Planets) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        FlightBoard flightBoard = ctx.lobby.getRealGame().getFlightBoard();
        int PosA = flightBoard.getPlayerPosition(playerA.getColor());
        System.out.println(PosA);




        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forPlanet_NormalConditions(rankedPlayers, planetCard);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) GameTestHelper.GameTestContext.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

        Ship updatedShipA = playerA.getShip();
        assertNotNull(updatedShipA);


        int posA = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerA.getColor());
        int posB = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerB.getColor());
        int posC = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerC.getColor());

        assertEquals(3,posA);
        assertEquals(0,posB);
        assertEquals(22,posC);


    }

    @Test
    void testPlanetsEffect_NormalConditions2Card() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Pianeti", 2);
        AdventureCard card = cards.getFirst();
        AdventureCard card2 = cards.getLast();
        assertNotNull(card);
        assertNotNull(card2);
        assertTrue(card instanceof Planets);
        assertTrue(card2 instanceof Planets);
        Planets planetCard = (Planets) card;
        Planets planetCard2 = (Planets) card2;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forPlanet_NormalConditions(rankedPlayers, planetCard);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) GameTestHelper.GameTestContext.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


         FlightBoard flightBoard = ctx.lobby.getRealGame().getFlightBoard();
         int PosA = flightBoard.getPlayerPosition(playerA.getColor());
         System.out.println(PosA);


        ctx.lobby.getGameController().getCardDeckTest().clear();

        ctx.lobby.getGameController().getCardDeckTest().addCard(card2);
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
        Ship updatedShipA = playerA.getShip();
        assertNotNull(updatedShipA);


        int posA = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerA.getColor());
        int posB = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerB.getColor());
        int posC = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerC.getColor());

        assertEquals(3,posA);
        assertEquals(0,posB);
        assertEquals(22,posC);


        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) GameTestHelper.GameTestContext.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }





    }

    @Test
    void testPirates_NormalConditions() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Pirati", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Pirates);
        Pirates planetCard = (Pirates) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createHighFirePowerShip(), //5~7
                        MockShipFactory.createHighFirePowerShipWithMultiDirection(), //power 3.5~5
                        MockShipFactory.createMockShip() //1
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forPirates(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });




        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

        Ship updatedShipA = playerA.getShip();
        assertNotNull(updatedShipA);


        int posA = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerA.getColor());
        int posB = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerB.getColor());
        int posC = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerC.getColor());

        int creditA = playerA.getNCredits();
        assertEquals(4, creditA);
        assertEquals(5,posA);


    }
    @Test
    void testPirates_AllPlayerLost() throws RemoteException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Pirati", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Pirates);
        Pirates piratesCard = (Pirates) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.getFirst();

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(), //1
                        MockShipFactory.createMockShip(), //power 1
                        MockShipFactory.createMockShip()  //1
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forPirates_AllPlayerLost(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(playerA.getNickName())
        );
    }

    @Test
    void testPirates_TestTruck() throws RemoteException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Pirati", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Pirates);
        Pirates piratesCard = (Pirates) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.getFirst();

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createEasyDestroyedShip(), //1
                        MockShipFactory.createMockShip(),
                        MockShipFactory.createMockShip()
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forPirates_TestTruck(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(playerA.getNickName())
        );
    }

        @Test
    void testSlavers_PlayerADefeatedEnemy() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Schiavisti", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Slavers);
        Slavers slavers = (Slavers) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.getFirst();
//        Player playerB = rankedPlayers.get(1);
//        Player playerC = rankedPlayers.get(2);
            setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                    Arrays.asList(
                            MockShipFactory.createHighFirePowerShipWithMultiDirection2(), //power 5.5~8
                            null,
                            null
                    )
            ));
//        playerB.replaceShip(MockShipFactory.createHighFirePowerShip());//5~7
//        playerC.replaceShip(MockShipFactory.createMockShip());//1

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forSlavers_PlayerADefeatedEnemy(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

        int posA = ctx.lobby.getRealGame().getFlightBoard().getPlayerPosition(playerA.getColor());
        int creditA = playerA.getNCredits();
        assertEquals(5,posA);
        assertEquals(5,creditA);

    }

    @Test
    void testSlavers_AllPlayersWereDefeated() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Schiavisti", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Slavers);
        Slavers slavers = (Slavers) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2(), //power 5.5~8
                        MockShipFactory.createHighFirePowerShip(),//5~7
                        MockShipFactory.createMockShip() //1
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forSlavers_AllPlayersWereDefeated(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        int nCrewA = playerA.getShip().getnCrew();
        int nCrewB = playerB.getShip().getnCrew();
        int nCrewC = playerC.getShip().getnCrew();
        System.out.println("Before");
        System.out.println("nCrewA: "+ nCrewA);
        System.out.println("nCrewB: "+ nCrewB);
        System.out.println("nCrewC: "+ nCrewC);
        System.out.println();
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
        nCrewA = playerA.getShip().getnCrew();
        nCrewB = playerB.getShip().getnCrew();
        nCrewC = playerC.getShip().getnCrew();

        System.out.println();
        System.out.println("After");
        System.out.println("nCrewA: "+ nCrewA);
        System.out.println("nCrewB: "+ nCrewB);
        System.out.println("nCrewC: "+ nCrewC);



        assertEquals(0,nCrewA);
        assertEquals(1,nCrewB);
        assertEquals(3,nCrewC);

    }

    /**
     * Tests the effect of the "Slaver" card on multiple players during combat resolution.
     *
     * <p>Test setup:
     * <ul>
     *   <li>Player A has 2 crew members.</li>
     *   <li>Player B has enough power to tie with the Slaver.</li>
     *   <li>Player C has enough power to defeat the Slaver.</li>
     *   <li>The Slaver card requires 3 crew to be discarded upon defeat.</li>
     * </ul>
     *
     * <p>Expected behavior:
     * <ol>
     *   <li>Player A is defeated and receives the discardCrew penalty.</li>
     *   <li>Since Player A has fewer crew members than required, all crew are removed.</li>
     *   <li>Player A is eliminated from the game due to having no remaining crew.</li>
     *   <li>Player B ties with the Slaver and does not receive any penalty.</li>
     *   <li>Player C defeats the Slaver, receives a reward, and moves backward according to the value specified on the Slaver card.</li>
     * </ol>
     *
     * <p>Assertions:
     * <ul>
     *   <li>Player A has been removed from the match after losing all crew.</li>
     *   <li>Player B remains in the same state, with no penalties applied.</li>
     *   <li>Player C's reward is granted and their position is updated correctly based on the backward movement.</li>
     *   <li>The remaining number of players in the match is reduced by one.</li>
     * </ul>
     */

    @Test
    void testSlavers_PlayerC_DefeatedEnemy() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Schiavisti", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Slavers);
        Slavers slavers = (Slavers) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2(), //power 5.5~8
                        MockShipFactory.createHighFirePowerShip(),//5~7
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2() //power 5.5~8
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forSlavers_PlayerC_DefeatedEnemy(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        FlightBoard flightBoard =  ctx.lobby.getRealGame().getFlightBoard();

        int nCrewA = playerA.getShip().getnCrew();
        int nCrewB = playerB.getShip().getnCrew();
        int nCrewC = playerC.getShip().getnCrew();
        System.out.println("Before");
        System.out.println("nCrewA: "+ nCrewA);
        System.out.println("nCrewB: "+ nCrewB);
        System.out.println("nCrewC: "+ nCrewC);
        System.out.println();
        System.out.println("Position A  :" +flightBoard.getPlayerPosition(playerA.getColor()));
        System.out.println("Position B  :" +flightBoard.getPlayerPosition(playerB.getColor()));
        System.out.println("Position C  :" + flightBoard.getPlayerPosition(playerC.getColor()));
        System.out.println();
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
        nCrewA = playerA.getShip().getnCrew();
        nCrewB = playerB.getShip().getnCrew();
        nCrewC = playerC.getShip().getnCrew();

        System.out.println();
        System.out.println("After");
        System.out.println("nCrewA: "+ nCrewA);
        System.out.println("nCrewB: "+ nCrewB);
        System.out.println("nCrewC: "+ nCrewC);
//
//
//
        assertEquals(0,nCrewA);
        assertEquals(4,nCrewB);
        assertEquals(2,nCrewC);

         ArrayList<Player>  rankedPlayer = ctx.lobby.getGameController().getRankedPlayers();

        assertFalse(rankedPlayer.contains(playerA));

        int PosB = flightBoard.getPlayerPosition(playerB.getColor());
        int PosC = flightBoard.getPlayerPosition(playerC.getColor());

        System.out.println();
        System.out.println("Position B  :" +PosB);
        System.out.println("Position C  :" + PosC);

        assertEquals(5,playerC.getNCredits());
        assertEquals(0,playerB.getNCredits());

        assertEquals(0,playerA.getNCredits());

        assertEquals(3,PosB);
        assertEquals(0,PosC);


    }

    @Test
    void testSmugglers_PlayerA_DefeatedEnemy() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Contrabbandieri", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Smugglers);
        Smugglers smugglers = (Smugglers) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.getFirst();

        final HashMap<String, ClientController> ntccmap = GameTestHelper.GameTestContext.nicknameToClientControllerMap;

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2(), //power 5.5~8
                        MockShipFactory.createHighFirePowerShip(), //5~7
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2()//power 5.5~8
                )
        ));


        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forSmugglers_PlayerA_DefeatedEnemy(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        FlightBoard flightBoard =  ctx.lobby.getRealGame().getFlightBoard();

        ClientController leaderClientController = ntccmap.get(playerA.getNickName());
        leaderClientController.getView().showFlightBoard(flightBoard, leaderClientController.getMyModel().getPlayerInfos(), leaderClientController.getMyModel().getMyInfo());
        leaderClientController.sendDrawAdventureCardRequest();

        int posA = flightBoard.getPlayerPosition(playerA.getColor());

        assertEquals(5,posA);

    }
    @Test
    void testSmugglers_PlayerA_DefeatedEnemy_ChooseToNoCollect() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Contrabbandieri", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Smugglers);
        Smugglers smugglers = (Smugglers) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.getFirst();

        final HashMap<String, ClientController> ntccmap = GameTestHelper.GameTestContext.nicknameToClientControllerMap;

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2(), //power 5.5~8
                        MockShipFactory.createHighFirePowerShip(), //5~7
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2()//power 5.5~8
                )
        ));


        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forSmugglers_PlayerA_DefeatedEnemyB(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });

        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);

        FlightBoard flightBoard =  ctx.lobby.getRealGame().getFlightBoard();

        ClientController leaderClientController = ntccmap.get(playerA.getNickName());
        leaderClientController.getView().showFlightBoard(flightBoard, leaderClientController.getMyModel().getPlayerInfos(), leaderClientController.getMyModel().getMyInfo());
        leaderClientController.sendDrawAdventureCardRequest();

        int posA = flightBoard.getPlayerPosition(playerA.getColor());

        assertEquals(6,posA);

    }
    @Test
    void testSmugglers_PlayerA_TieCondition() throws RemoteException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Contrabbandieri", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Smugglers);
        Smugglers smugglers = (Smugglers) card;
        System.out.println(smugglers.getFirePower());

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createHighFirePowerShip2(),
                        MockShipFactory.createHighFirePowerShip(),
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2()
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forSmugglers_PlayerA_TieCondition(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(playerA.getNickName())
        );
        FlightBoard flightBoard =  ctx.lobby.getRealGame().getFlightBoard();
        int posA = flightBoard.getPlayerPosition(playerA.getColor());
        int posB = flightBoard.getPlayerPosition(playerB.getColor());

        assertEquals(6,posA);
        assertEquals(2,posB);
    }
    @Test
    void testSmugglers_PlayerA_SmugglersWin() throws RemoteException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Contrabbandieri", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof Smugglers);
        Smugglers smugglers = (Smugglers) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(), //crew: 6  Engine Power: {0,2}  Fire Power:1.0
                        MockShipFactory.createHighFirePowerShip(), // crew 4  Engine Power: {0,2}  Fire Power:5.0 ~ 7.0
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2() //crew 2 Engine Power: {0,2}  Fire Power:3.5~5.0
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forSmugglers_PlayerA_SmugglersWin(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(playerA.getNickName())
        );
        FlightBoard flightBoard =  ctx.lobby.getRealGame().getFlightBoard();
        int posA = flightBoard.getPlayerPosition(playerA.getColor());
        int posB = flightBoard.getPlayerPosition(playerB.getColor());

        assertEquals(6,posA);
        assertEquals(2,posB);

    }



    @Test
    void testMeteorSwarm( ) throws RemoteException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Meteoriti", 1);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertTrue(card instanceof MeteorSwarm);
        MeteorSwarm meteorSwarm = (MeteorSwarm) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(), //power 1
                        MockShipFactory.createHighFirePowerShip(), //5~7
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2()//power 5.5~8
                )
        ));


        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forMeteorSwarm(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(playerA.getNickName())
        );

    }

    @Test
    void testMeteorSwarm2_TestTruck( ) throws RemoteException {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Meteoriti", 3);
        AdventureCard card = cards.get(2);
        assertNotNull(card);
        assertTrue(card instanceof MeteorSwarm);
        MeteorSwarm meteorSwarm = (MeteorSwarm) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createEasyDestroyedShip2(),
                        MockShipFactory.createEasyDestroyedShip2(),
                        MockShipFactory.createEasyDestroyedShip2()
                )
        ));


        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forCombatZone(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        ctx.serverController.handleDrawAdventureCardRequest(
                new DrawAdventureCardRequest(),
                ctx.nicknameToHandlerMap.get(playerA.getNickName())
        );

    }
    @Test
    void testCombatZone() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Zona Guerra", 3);
        AdventureCard card = cards.getFirst();
        assertNotNull(card);
        assertEquals("Zona Guerra", card.getName());

        CombatZone combatZone = (CombatZone) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        playerA.replaceShip(MockShipFactory.createMockShip_CombatZone()); //crew: 6  Engine Power: {0,2}  Fire Power:1.0
        playerB.replaceShip(MockShipFactory.createHighFirePowerShip());// crew 4  Engine Power: {0,2}  Fire Power:5.0 ~ 7.0
        playerC.replaceShip(MockShipFactory.createHighFirePowerShipWithMultiDirection2());//crew 2 Engine Power: {0,2}  Fire Power:3.5~5.0

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip_CombatZone(), //crew: 6  Engine Power: {0,2}  Fire Power:1.0
                        MockShipFactory.createHighFirePowerShip(), // crew 4  Engine Power: {0,2}  Fire Power:5.0 ~ 7.0
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2()//crew 2 Engine Power: {0,2}  Fire Power:3.5~5.0
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forCombatZone(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void testCombatZone2() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Zona Guerra", 3);
        AdventureCard card = cards.get(1);
        assertNotNull(card);
        assertEquals("Zona Guerra", card.getName());

        CombatZone combatZone = (CombatZone) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);

        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createMockShip(), //crew: 6  Engine Power: {0,2}  Fire Power:1.0
                        MockShipFactory.createHighFirePowerShip(), // crew 4  Engine Power: {0,2}  Fire Power:5.0 ~ 7.0
                        MockShipFactory.createHighFirePowerShipWithMultiDirection2() //crew 2 Engine Power: {0,2}  Fire Power:3.5~5.0
                )
        ));

        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forCombatZone(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testCombatZone3() {
        List<AdventureCard> cards = CardTestUtils.loadCardsByType("Zona Guerra", 3);
        AdventureCard card = cards.get(1);
        assertNotNull(card);
        assertEquals("Zona Guerra", card.getName());

        CombatZone combatZone = (CombatZone) card;

        GameTestHelper.GameTestContext ctx = GameTestHelper.setupGame(MockResponsesFactory.emptyResponsesFor(players), players);

        ArrayList<Player> rankedPlayers = ctx.lobby.getGameController().getRankedPlayers();
        Player playerA = rankedPlayers.get(0);
        Player playerB = rankedPlayers.get(1);
        Player playerC = rankedPlayers.get(2);


        setPlayersShipClientSideAndServerSide(rankedPlayers, new ArrayList<>(
                Arrays.asList(
                        MockShipFactory.createEasyDestroyedShip(),
                        MockShipFactory.createEasyDestroyedShip(),
                        MockShipFactory.createEasyDestroyedShip()
                )
        ));


        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.forCombatZone(rankedPlayers);
        responses.forEach((nick, responseList) -> {
            FakeClientHandler handler = (FakeClientHandler) ctx.nicknameToHandlerMap.get(nick);
            handler.setMockResponses(responseList);
        });


        ctx.lobby.getGameController().getCardDeckTest().clear();
        ctx.lobby.getGameController().getCardDeckTest().addCard(card);
        try {
            ctx.serverController.handleDrawAdventureCardRequest(
                    new DrawAdventureCardRequest(),
                    ctx.nicknameToHandlerMap.get(playerA.getNickName())
            );
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
}