package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatterySlotTest {

    @Test
    void getBatteriesLeft() {
    }

    @Test
    void removeBatteryWhenBatteriesAreLeft() {
        // Initialization of BatterySlot with 1 battery
        BatterySlot batterySlot = new BatterySlot(1);
        // Assertion to ensure battery was removed
        assertTrue(batterySlot.removeBattery());
        // Assertion to ensure number of batteries is now 0
        assertEquals(0, batterySlot.getBatteriesLeft());
    }

    @Test
    void testClone() {
    }

    @Test
    void removeBatteryWhenNoBatteriesAreLeft() {
        // Initialization of BatterySlot with 0 batteries
        BatterySlot batterySlot = new BatterySlot(0);
        // Assertion to ensure no battery was removed
        assertFalse(batterySlot.removeBattery());
        // Assertion to ensure number of batteries is still 0
        assertEquals(0, batterySlot.getBatteriesLeft());
    }

    @Test
    void accept() {
    }
}