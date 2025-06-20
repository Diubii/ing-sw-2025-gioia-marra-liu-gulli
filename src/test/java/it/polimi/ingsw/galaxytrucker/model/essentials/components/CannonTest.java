package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CannonTest {

    @Test
    public void getFirePowerWithoutRotationTest() {
        Cannon cannon = new Cannon(2.0f);
        Float expectedFirePower = 2.0f;
        Assertions.assertEquals(expectedFirePower, cannon.getFirePower(), "Cannon fire power doesn't match expected value when no rotation applied");
    }

    @Test
    public void getFirePowerWithRotationTest() {
        Cannon cannon = new Cannon(2.0f);
        cannon.setRotation(90);
        Float expectedFirePower = 1.0f;
        Assertions.assertEquals(expectedFirePower, cannon.getFirePower(), "Cannon fire power doesn't match expected when rotation applied");
    }

    @Test
    public void getFirePowerAfterCloneTest() {
        Cannon cannonOrig = new Cannon(3.0f);
        Cannon cannonCloned = cannonOrig.clone();
        Assertions.assertEquals(cannonOrig.getFirePower(), cannonCloned.getFirePower(), "Cannon fire power doesn't match after clone operation");
    }
}