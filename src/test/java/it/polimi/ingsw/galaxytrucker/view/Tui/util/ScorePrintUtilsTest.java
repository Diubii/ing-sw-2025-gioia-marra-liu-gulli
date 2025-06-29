package it.polimi.ingsw.galaxytrucker.view.Tui.util;

import it.polimi.ingsw.galaxytrucker.model.PlayerScore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for {@link ScorePrintUtils}, verifying correct rendering of score tables
 * for multiple players.
 */
class ScorePrintUtilsTest {

    /**
     * Tests the {@link ScorePrintUtils#printScoreTable(List)} method with mock player scores
     * to verify the table output is generated without errors.
     */
     @Test
    void printScoreTotalTest() {
         List<PlayerScore> scores = new ArrayList<>();

         scores.add(new PlayerScore("Player1", 100, 100, 100, 100, 100));
         scores.add(new PlayerScore("Player2", 100, 100, 100, 100, 100));
         scores.add(new PlayerScore("Player3", 100, 100, 88, 100, 100));

        ScorePrintUtils.printScoreTable(scores);



     }
}
