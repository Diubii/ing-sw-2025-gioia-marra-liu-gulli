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

    @Test
    void testConstructor() {
        assertEquals(Color.EMPTY, modularHousingUnit.getColor());
        assertEquals(AlienColor.EMPTY, modularHousingUnit.getAlienColor());
        assertEquals(0,modularHousingUnit.getNBrownAlien());
        assertEquals(0,modularHousingUnit.getNPurpleAlien());

    }

    @Test
    void testGetNCrewMembers() {
        ModularHousingUnit modularHousingUnit1 = (ModularHousingUnit) TileRegistry.getFirstTileOfType("ModularHousingUnit").getMyComponent();
        modularHousingUnit1.getNCrewMembers();
        assertEquals(0, modularHousingUnit1.getNCrewMembers());

    }

    @Test
    void testRemoveAlien() {

    }

    @Test
    void testAddAndRemoveAlien() {
     modularHousingUnit.addBrownAlien();
     assertEquals(1, modularHousingUnit.getNBrownAlien());
     assertEquals(0, modularHousingUnit.getNPurpleAlien());
     modularHousingUnit.addPurpleAlien();
     assertEquals(1, modularHousingUnit.getNBrownAlien());
     assertEquals(1, modularHousingUnit.getNPurpleAlien());




    }


    @Test
    void addBrownAlien() {
        modularHousingUnit.addBrownAlien();
        assertTrue(modularHousingUnit.getNBrownAlien() > 0 && modularHousingUnit.getAlienColor() == AlienColor.BROWN);
    }

    @Test
    void addPurpleAlien() {
        modularHousingUnit.addPurpleAlien();
        assertTrue(modularHousingUnit.getNPurpleAlien() > 0 && modularHousingUnit.getAlienColor() == AlienColor.PURPLE);
    }

    @Test
    void addHumanCrew() {
        modularHousingUnit.addHumanCrew();
        assertTrue(modularHousingUnit.getNCrewMembers() == 2);


    }

    @Test
    void removeBrownAlien() {
        modularHousingUnit.addBrownAlien();
        modularHousingUnit.removeBrownAlien();
        assertEquals(0, modularHousingUnit.getNBrownAlien());
        assertEquals(AlienColor.EMPTY, modularHousingUnit.getAlienColor());

    }

    @Test
    void removeAlienCrew() {
        modularHousingUnit.addBrownAlien();
        modularHousingUnit.removeAlienCrew();
        assertEquals(0, modularHousingUnit.getNBrownAlien());
        assertEquals(AlienColor.EMPTY, modularHousingUnit.getAlienColor());
    }

    @Test
    void removePurpleAlien() {
    }

    @Test
    void getNBrownAlien() {
    }

    @Test
    void getNPurpleAlien() {
    }

    @Test
    void getAlienColor() {
    }
}
