package it.polimi.ingsw.galaxytrucker.view.Tui.util;

import it.polimi.ingsw.galaxytrucker.model.PlayerScore;

import java.util.List;

public class ScorePrintUtils {

    public static void printScoreTable(List<PlayerScore> scores) {
        String formatHeader = "| %-9s | %-17s | %-10s | %-7s | %-5s | %-18s | %-6s |%n";
        String formatRow =    "| %-9s | %-17d | %-10d | %-7d | %-5d | %-18d | %-6d |%n";

        System.out.println();
        System.out.println("I PUNTEGGI FINALI DEL GIOCO");
        System.out.format(            "+-----------+-------------------+------------+---------+-------+--------------------+--------+%n");
        System.out.format(formatHeader, "Giocatore","Punti piazzamento","Nave bella","Crediti","Merci","Perdita componenti","Totale");
        System.out.format(            "+-----------+-------------------+------------+---------+-------+--------------------+--------+%n");

        for (PlayerScore score : scores) {
            System.out.format(formatRow,
                    score.getNickName(),
                    score.getFinishOrderScore(),
                    score.getBestLookingShipScore(),
                    score.getCredits(),
                    score.getGoodRewardScore(),
                    -score.getLossesScore(),
                    score.getTotalScore()
            );
        }

        System.out.format("+-----------+-------------------+------------+---------+-------+--------------------+--------+%n");
    }
}
