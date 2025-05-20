package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


}
