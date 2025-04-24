package org.polimi.ingsw.galaxytrucker.model.utils;

import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void createLvl1Deck() throws IOException {

        CardDeck dexk = Util.createLvl1Deck();
        System.out.println(dexk.pop().getName());
    }

    @Test
    void createLvl2Deck() {
    }

    @Test
    void createLearningDeck() {
    }
}