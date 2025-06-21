package it.polimi.ingsw.galaxytrucker.model.utils;

import it.polimi.ingsw.galaxytrucker.enums.Connector;
import it.polimi.ingsw.galaxytrucker.model.MockShipFactory;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
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


        Tile tile = new Tile(0,0,connectors,new BatterySlot(2));

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
}