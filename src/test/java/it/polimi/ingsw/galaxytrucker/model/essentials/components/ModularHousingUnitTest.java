package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.TileRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModularHousingUnitTest {

    private ModularHousingUnit modularHousingUnit;

    @BeforeEach
    void setUp() {
        modularHousingUnit = new ModularHousingUnit();
    }

    /**
     * Tests the default constructor initializes alien color, crew count and alien counts correctly.
     */
    @Test
    void testConstructor() {
        assertEquals(Color.EMPTY, modularHousingUnit.getColor());
        assertEquals(AlienColor.EMPTY, modularHousingUnit.getAlienColor());
        assertEquals(0,modularHousingUnit.getNBrownAlien());
        assertEquals(0,modularHousingUnit.getNPurpleAlien());
    }

    /**
     * Tests getNCrewMembers returns correct value (0) when no crew has been added.
     */
    @Test
    void testGetNCrewMembers() {
        ModularHousingUnit modularHousingUnit1 = (ModularHousingUnit) TileRegistry.getFirstTileOfType("ModularHousingUnit").getMyComponent();
        modularHousingUnit1.getNCrewMembers();
        assertEquals(0, modularHousingUnit1.getNCrewMembers());
    }

    /**
     * Placeholder for testRemoveAlien method - currently empty, needs implementation.
     */

    @Test
    void testAddAndRemoveAlien() {
        modularHousingUnit.addBrownAlien();
        assertEquals(1, modularHousingUnit.getNBrownAlien());
        assertEquals(0, modularHousingUnit.getNPurpleAlien());
        modularHousingUnit.addPurpleAlien();
        assertEquals(1, modularHousingUnit.getNBrownAlien());
        assertEquals(1, modularHousingUnit.getNPurpleAlien());
    }

    /**
     * Tests addBrownAlien increases brown alien count and sets correct alien color.
     */
    @Test
    void addBrownAlien() {
        modularHousingUnit.addBrownAlien();
        assertTrue(modularHousingUnit.getNBrownAlien() > 0 && modularHousingUnit.getAlienColor() == AlienColor.BROWN);
    }

    /**
     * Tests addPurpleAlien increases purple alien count and sets correct alien color.
     */
    @Test
    void addPurpleAlien() {
        modularHousingUnit.addPurpleAlien();
        assertTrue(modularHousingUnit.getNPurpleAlien() > 0 && modularHousingUnit.getAlienColor() == AlienColor.PURPLE);
    }

    /**
     * Tests addHumanCrew adds exactly 2 human crew members to the housing unit.
     */
    @Test
    void addHumanCrew() {
        modularHousingUnit.addHumanCrew();
        assertTrue(modularHousingUnit.getNCrewMembers() == 2);
    }

    /**
     * Tests removeBrownAlien decreases brown alien count and resets alien color if count reaches zero.
     */
    @Test
    void removeBrownAlien() {
        modularHousingUnit.addBrownAlien();
        modularHousingUnit.removeBrownAlien();
        assertEquals(0, modularHousingUnit.getNBrownAlien());
        assertEquals(AlienColor.EMPTY, modularHousingUnit.getAlienColor());
    }

    /**
     * Tests removeAlienCrew removes all aliens and resets alien color accordingly.
     */
    @Test
    void removeAlienCrew() {
        modularHousingUnit.addBrownAlien();
        modularHousingUnit.removeAlienCrew();
        assertEquals(0, modularHousingUnit.getNBrownAlien());
        assertEquals(AlienColor.EMPTY, modularHousingUnit.getAlienColor());
    }

    /**
     * Placeholder for removePurpleAlien test method - currently empty, needs implementation.
     */
    @Test
    void removePurpleAlien() {
    }

    /**
     * Placeholder for getNBrownAlien test method - currently empty, needs implementation.
     */
    @Test
    void getNBrownAlien() {
    }

    /**
     * Placeholder for getNPurpleAlien test method - currently empty, needs implementation.
     */
    @Test
    void getNPurpleAlien() {
    }

    /**
     * Placeholder for getAlienColor test method - currently empty, needs implementation.
     */
    @Test
    void getAlienColor() {
    }
}
