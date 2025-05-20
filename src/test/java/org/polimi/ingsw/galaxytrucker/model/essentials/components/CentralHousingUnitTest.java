package org.polimi.ingsw.galaxytrucker.model.essentials.components;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

import static org.junit.jupiter.api.Assertions.*;

class CentralHousingUnitTest {


    private CentralHousingUnit coloredUnit;
    private CentralHousingUnit emptyUnit;


    @BeforeEach
    void setUp() throws Exception {
        coloredUnit = new CentralHousingUnit(Color.RED);
        emptyUnit   = new CentralHousingUnit(Color.EMPTY);
    }

    @Test
    void testConstructorWithColor() {

        assertEquals(Color.RED, coloredUnit.getColor());
        assertEquals(2, coloredUnit.getNCrewMembers());
        assertTrue(coloredUnit.getIsColored());
    }

    @Test
    void testConstructorWithEmptyColor() {
        assertEquals(Color.EMPTY, emptyUnit.getColor());
        assertEquals(0, emptyUnit.getNCrewMembers());
        assertFalse(emptyUnit.getIsColored());
    }


    @Test
    void testRemoveCrewMember() {

        coloredUnit.removeCrewMember();
        assertEquals(1, coloredUnit.getNCrewMembers());
        coloredUnit.removeCrewMember();
        assertEquals(0, coloredUnit.getNCrewMembers());
        coloredUnit.removeCrewMember();
        assertEquals(0, coloredUnit.getNCrewMembers());
    }

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
