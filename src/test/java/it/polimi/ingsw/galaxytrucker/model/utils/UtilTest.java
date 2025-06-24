package it.polimi.ingsw.galaxytrucker.model.utils;

import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.enums.Connector;
import it.polimi.ingsw.galaxytrucker.model.MockShipFactory;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.LifeSupportSystem;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void createLvl1Deck() throws IOException {

        CardDeck dexk = Util.createLvl1Deck();


        System.out.println(dexk.pop().getName());
    }

    @Test
    void createLvl2Deck() {
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
        assertTrue(Util.wellConnectedConnectors(ship, ship.getShipBoard()[][], myTile));


    }
}