package it.polimi.ingsw.galaxytrucker.model.utils;

import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.enums.Connector;
import it.polimi.ingsw.galaxytrucker.model.MockShipFactory;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.TileRegistry;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.Cannon;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.LifeSupportSystem;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    private static final Logger log = LoggerFactory.getLogger(UtilTest.class);

    @Test
    void testCreateLvl1Deck() throws IOException {

            CardDeck deck = Util.createLvl1Deck();
            assertNotNull(deck);
            assertFalse(deck.getCards().isEmpty());
            assertTrue(deck.getCards().stream().allMatch(card -> card.getLevel() == 1));

    }

    @Test
    void createLvl2Deck() {
            CardDeck deck = Util.createLvl2Deck();
            assertNotNull(deck);
            assertFalse(deck.getCards().isEmpty());
            assertTrue(deck.getCards().stream().allMatch(card -> card.getLevel() == 2));

    }

    @Test
    void createLearningDeck() {
    }

    @Test

    void checkShipS(){
        Ship ship = MockShipFactory.createMockShip();

        ArrayList<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);


        LifeSupportSystem lifeSupportSystem = new LifeSupportSystem(AlienColor.PURPLE);

        Tile tile = new Tile(0,0,connectors,lifeSupportSystem);

        ship.putTile(tile, new Position(5,3));
        ship.putTile(tile, new Position(5,2));


        Pair<Boolean, ArrayList<Integer>> pair =  Util.checkShipStructure(ship, new Position(3,2));
        ShipPrintUtils.printShip(ship);
        System.out.println("B: " + pair.getKey() );

        ship.removeTile(new Position(5,2),false);

        ship.checkShip();
        System.out.println(ship.toString());

        ArrayList<Ship> ships = ship.getTronc();

        for (Ship ship1 : ships) {
            ShipPrintUtils.printShip(ship1);

        }

    }
    @Test
    void testInBoundaries() {
        assertTrue(Util.inBoundaries(0, 0));
        assertTrue(Util.inBoundaries(6, 4));
        assertFalse(Util.inBoundaries(-1, 3));
        assertFalse(Util.inBoundaries(7, 3));
    }


    @Test
    void testEngineWellConnected() {
        //test 1 rotation != 0
        Ship ship = MockShipFactory.createMockShipForCheckShip();
        ShipPrintUtils.printShip(ship);
        Slot slotToCheck = ship.getShipBoard()[4][1];
        Tile tileToCheck = slotToCheck.getTile();

        Boolean wellConnected  = Util.EngineWellConnected(tileToCheck,ship,slotToCheck);
        assertEquals(false,wellConnected );

        //test2 rotation == 0  !wellConnected
        slotToCheck = ship.getShipBoard()[2][2];
        tileToCheck = slotToCheck.getTile();

        wellConnected = Util.EngineWellConnected(tileToCheck,ship,slotToCheck);
        assertEquals(false,wellConnected);

        //test3 sud in boundaries == true
        slotToCheck =ship.getShipBoard()[2][4];
        tileToCheck = slotToCheck.getTile();

        wellConnected = Util.EngineWellConnected(tileToCheck,ship,slotToCheck);
        assertEquals(true,wellConnected);

        //test 4  sud == null
        slotToCheck = ship.getShipBoard()[1][1];
        tileToCheck = slotToCheck.getTile();

        wellConnected = Util.EngineWellConnected(tileToCheck,ship,slotToCheck);
        assertEquals(true,wellConnected);




    }

    @Test
    void testGetAdjacentPositions() {
        var pos = Util.getAdjacentPositions(new Position(3, 3));
        assertEquals(4, pos.size());
        assertEquals(new Position(3, 2), pos.get(0)); // North
    }

    @Test

    void testCompatible() {
        var connector1 = Connector.UNIVERSAL;
        var connector2 = Connector.UNIVERSAL;
        assertTrue(Util.compatible(connector1, connector2));
        connector2 = Connector.SINGLE;
        assertTrue(Util.compatible(connector1, connector2));
        connector2 = Connector.EMPTY;
        assertFalse(Util.compatible(connector1, connector2));
        connector2 = Connector.DOUBLE;
        assertTrue(Util.compatible(connector1, connector2));
        connector2 = Connector.SINGLE;
        assertTrue(Util.compatible(connector1, connector2));

    }


    @Test

    void testCheckNearLFS(){

        Ship ship = MockShipFactory.createMockShip();
        ShipPrintUtils.printShip(ship);
        LifeSupportSystem lifeSupportSystem = new LifeSupportSystem(AlienColor.PURPLE);
        LifeSupportSystem BrownlifeSupportSystem = new LifeSupportSystem(AlienColor.BROWN);


        ArrayList<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.EMPTY);
        connectors.add(Connector.EMPTY);
        connectors.add(Connector.EMPTY);
        connectors.add(Connector.UNIVERSAL);

        Tile myTile = new Tile(0,0,connectors,lifeSupportSystem);
        Tile myTile2 = new Tile(0,0,connectors,BrownlifeSupportSystem);

        ship.putTile(myTile,new Position(5,2));
//        ship.putTile(myTile2,new Position(4,3));


        ShipPrintUtils.printShip(ship);

        //4,2 si trova la cabin

        assertTrue(Util.checkNearLFS(new Position(4,2), AlienColor.PURPLE, ship));
//        assertTrue(Util.wellConnectedConnectors(ship, ship.getShipBoard()[][], myTile));


    }


    @Test
    public void testVisitTile_simpleConnectedComponent() {
        Ship ship = new Ship(true);

        List<Tile> Cannons = TileRegistry.getClonedTilesOfType("Cannon");
        Tile tileA = Cannons.get(2);
        tileA.rotate(90);

        Tile tileB = TileRegistry.getFirstTileOfType("Engine");

        ship.putTile(tileA,new Position(4,3));
        ship.putTile(tileB,new Position(3,3));
        ShipPrintUtils.printShip(ship);
        Slot slotA = ship.getShipBoard()[4][3];


        ArrayList<Integer> visited = new ArrayList<>();
        Queue<Position> broken = new LinkedList<>();
        Util.visitTile(tileA, visited, slotA, new ArrayList<>(), broken, ship);


        assertTrue(visited.contains(tileA.getId()));
        assertTrue(visited.contains(tileB.getId()));
        assertTrue(broken.isEmpty());
    }

}