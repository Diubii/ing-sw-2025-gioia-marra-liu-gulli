package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MockShipFactoryTest class contains unit tests for verifying the functionality
 * of the MockShipFactory class, which is responsible for creating various mock ship configurations.
 */
class MockShipFactoryTest {

    /**
     * Tests that findTilesByComponentType correctly identifies all BatterySlot components.
     * Ensures that only tiles with "BatterySlot" type are retrieved.
     */
    @Test
    void testFindTilesByComponentType() {
        List<Tile> batteryTiles = MockShipFactory.findTilesByComponentType("BatterySlot");

        assertNotNull(batteryTiles);
        assertFalse(batteryTiles.isEmpty(), "No BatterySlot tiles found");

        for (Tile tile : batteryTiles) {
            String type = tile.getMyComponent().accept(new ComponentNameVisitor());
            assertEquals("BatterySlot", type, "Unexpected component type found: " + type);
        }
    }

    /**
     * Tests createMockShip by constructing a predefined ship configuration and printing its details.
     * Validates basic ship creation and prints attributes like crew count, engine power, etc.
     */
    @Test
    void testCreateMockShip() {
        Ship ship = MockShipFactory.createMockShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createMockShip2 by generating another standard ship and printing relevant properties.
     * Used to validate different ship layout configurations.
     */
    @Test
    void testCreateMockShip2() {
        Ship ship = MockShipFactory.createMockShip2();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createShipWithConnectedHousingUnits by building and displaying a ship
     * where housing units are interconnected.
     */
    @Test
    void testCreateShipWithConnectedHousingUnits() {
        Ship ship = MockShipFactory.createShipWithConnectedHousingUnits();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createHighFirePowerShip by constructing a ship optimized for high firepower.
     * Prints calculated stats for verification.
     */
    @Test
    void testCreateShipCreateHighFirePowerShip() {
        Ship ship = MockShipFactory.createHighFirePowerShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Similar to testCreateShipCreateHighFirePowerShip but uses an alternative configuration
     * for achieving high firepower.
     */
    @Test
    void testCreateShipCreateHighFirePowerShip2() {
        Ship ship = MockShipFactory.createHighFirePowerShip2();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createMockShipForCheckShip by generating a ship specifically designed
     * to fail validation in checkShip().
     */
    @Test
    void testCreateMockShipForCheckShip() {
        Ship ship = MockShipFactory.createMockShipForCheckShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createMockShipWithShield by creating a ship that includes shield components.
     * Useful for validating shield-related logic elsewhere.
     */
    @Test
    void testCreateMockShipWithShield() {
        Ship ship = MockShipFactory.createMockShipWithShield();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createHighFirePowerShipWithMultiDirection by creating a ship with cannons
     * spread across multiple directions.
     */
    @Test
    void testCreateHighFirePowerShipWithMultiDirection() {
        Ship ship = MockShipFactory.createHighFirePowerShipWithMultiDirection();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * A second version of multi-directional high-firepower ship test,
     * used to verify consistency across variations.
     */
    @Test
    void testCreateHighFirePowerShipWithMultiDirection2() {
        Ship ship = MockShipFactory.createHighFirePowerShipWithMultiDirection2();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createEasyDestroyedShip by simulating a ship easily broken into segments.
     * Used for testing ship truncation logic.
     */
    @Test
    void testCreateEasyDestroyedShip() {
        Ship ship = MockShipFactory.createEasyDestroyedShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * An alternate version of testCreateEasyDestroyedShip for comparison purposes.
     */
    @Test
    void testCreateEasyDestroyedShip2() {
        Ship ship = MockShipFactory.createEasyDestroyedShip2();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Tests createMockShip_CombatZone by generating a ship suitable for combat scenarios.
     * Useful for testing battle-specific behaviors.
     */
    @Test
    void testCreateMockShip_CombatZone() {
        Ship ship = MockShipFactory.createMockShip_CombatZone();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Third variation of mock ship creation for broader test coverage.
     * Intended for additional structural testing.
     */
    @Test
    void testCreateMockShip3() {
        Ship ship = MockShipFactory.createMockShip3();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }

    /**
     * Fourth variation of mock ship creation, used to further test diverse configurations.
     */
    @Test
    void testCreateMockShip4() {
        Ship ship = MockShipFactory.createMockShip4();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );
    }
}
