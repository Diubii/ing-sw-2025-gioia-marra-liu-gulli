package it.polimi.ingsw.galaxytrucker.model.essentials.components;


import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CentralHousingUnitTest {

    private CentralHousingUnit coloredUnit;
    private CentralHousingUnit emptyUnit;

    @BeforeEach
    void setUp() throws Exception {
        coloredUnit = new CentralHousingUnit(Color.RED);
        emptyUnit   = new CentralHousingUnit(Color.EMPTY);
    }

    /**
     * Tests constructor with Color.RED.
     * Verifies that the color is set correctly, crew count is 2, and isColored flag is true.
     */
    @Test
    void testConstructorWithColor() {
        assertEquals(Color.RED, coloredUnit.getColor());
        assertEquals(2, coloredUnit.getNCrewMembers());
        assertTrue(coloredUnit.getIsColored());
    }

    /**
     * Tests constructor with Color.EMPTY.
     * Verifies that the color is EMPTY, crew count is 0, and isColored flag is false.
     */
    @Test
    void testConstructorWithEmptyColor() {
        assertEquals(Color.EMPTY, emptyUnit.getColor());
        assertEquals(0, emptyUnit.getNCrewMembers());
        assertFalse(emptyUnit.getIsColored());
    }

    /**
     * Tests removeCrewMember method.
     * Ensures it decreases crew count but does not go below zero.
     */
    @Test
    void testRemoveCrewMember() {
        coloredUnit.removeCrewMember();
        assertEquals(1, coloredUnit.getNCrewMembers());
        coloredUnit.removeCrewMember();
        assertEquals(0, coloredUnit.getNCrewMembers());
        coloredUnit.removeCrewMember();
        assertEquals(0, coloredUnit.getNCrewMembers());
    }

    /**
     * Tests accept method for visitor pattern.
     * Ensures the correct visit method is called for both colored and empty units.
     */
    @Test
    void testAcceptVisitor() {
        ComponentVisitorInterface<String> visitor = new ComponentVisitorInterface<String>() {
            @Override
            public String visit(Component component) {
                return "";
            }

            @Override
            public String visit(BatterySlot component) {
                return "";
            }

            @Override
            public String visit(Cannon component) {
                return "";
            }

            @Override
            public String visit(CentralHousingUnit centralHousingUnit) {
                return "CentralHousingUnit";
            }

            @Override
            public String visit(DoubleCannon component) {
                return "";
            }

            @Override
            public String visit(DoubleEngine component) {
                return "";
            }

            @Override
            public String visit(Engine component) {
                return "";
            }

            @Override
            public String visit(GenericCargoHolds component) {
                return "";
            }

            @Override
            public String visit(LifeSupportSystem component) {
                return "";
            }

            @Override
            public String visit(ModularHousingUnit component) {
                return "";
            }

            @Override
            public String visit(Shield component) {
                return "";
            }
        };

        assertEquals("CentralHousingUnit", coloredUnit.accept(visitor));
        assertEquals("CentralHousingUnit", emptyUnit.accept(visitor));
    }
}
