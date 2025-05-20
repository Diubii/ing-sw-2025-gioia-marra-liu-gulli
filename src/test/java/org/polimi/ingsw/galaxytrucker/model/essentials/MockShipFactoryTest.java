package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockShipFactoryTest {

    @Test
     void testFindTilesByComponentType() throws IOException {
        List<Tile> batteryTiles = MockShipFactory.findTilesByComponentType("BatterySlot");

        assertNotNull(batteryTiles);
        assertFalse(batteryTiles.isEmpty(), "No BatterySlot tiles found");

        for (Tile tile : batteryTiles) {
            String type = tile.getMyComponent().accept(new ComponentNameVisitor());
            assertEquals("BatterySlot", type, "Unexpected component type found: " + type);
        }
    }

    @Test
        void testCreateMockShip() throws IOException {
        Ship ship = MockShipFactory.createMockShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);

        }
}
