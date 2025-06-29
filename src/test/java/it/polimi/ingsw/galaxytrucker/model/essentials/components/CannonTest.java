package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Verifies fire power without rotation matches the initial value.
 */

public class CannonTest {

    /**
     * Tests getFirePower when no rotation is applied.
     * Verifies that the fire power matches the initial value set in the constructor.
     */
    @Test
    public void getFirePowerWithoutRotationTest() {
        Cannon cannon = new Cannon(2.0f);
        Float expectedFirePower = 2.0f;
        Assertions.assertEquals(expectedFirePower, cannon.getFirePower(), "Cannon fire power doesn't match expected value when no rotation applied");
    }

    /**
     * Tests getFirePower after applying a 90-degree rotation.
     * Verifies that fire power is halved as expected due to rotation.
     */
    @Test
    public void getFirePowerWithRotationTest() {
        Cannon cannon = new Cannon(2.0f);
        cannon.setRotation(90);
        Float expectedFirePower = 1.0f;
        Assertions.assertEquals(expectedFirePower, cannon.getFirePower(), "Cannon fire power doesn't match expected when rotation applied");
    }

    /**
     * Tests cloning behavior of Cannon.
     * Ensures cloned instance has the same fire power as the original.
     */
    @Test
    public void getFirePowerAfterCloneTest() {
        Cannon cannonOrig = new Cannon(3.0f);
        Cannon cannonCloned = cannonOrig.clone();
        Assertions.assertEquals(cannonOrig.getFirePower(), cannonCloned.getFirePower(), "Cannon fire power doesn't match after clone operation");
    }
}