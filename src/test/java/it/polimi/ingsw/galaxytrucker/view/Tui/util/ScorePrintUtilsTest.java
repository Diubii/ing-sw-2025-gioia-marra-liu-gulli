package it.polimi.ingsw.galaxytrucker.view.Tui.util;

import it.polimi.ingsw.galaxytrucker.model.PlayerScore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ScorePrintUtilsTest {
     @Test
    void printScoreTotalTest() {


         List<PlayerScore> scores = new ArrayList<>();

         scores.add(new PlayerScore("Player1", 100, 100, 100, 100, 100));
         scores.add(new PlayerScore("Player2", 100, 100, 100, 100, 100));
         scores.add(new PlayerScore("Player3", 100, 100, 88, 100, 100));

        ScorePrintUtils.printScoreTable(scores);



     }
}
