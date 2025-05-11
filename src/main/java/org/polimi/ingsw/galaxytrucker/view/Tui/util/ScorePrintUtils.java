package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.model.PlayerScore;

import java.util.List;

public class ScorePrintUtils {

    public static void printScoreTable(List<PlayerScore> scores) {
        String formatHeader = "| %-10s | %-12s |%-10s | %-7s | %-6s | %-6s | %-6s|%n";
        String formatRow =    "| %-10s | %-12d |%-10d | %-7d | %-6d | %-6d | %-6d|%n";

        System.out.println();
        System.out.println("LE SCORE FINALI DEL GIOCO");
        System.out.format("+------------+--------------+-----------+---------+--------+--------+-------+%n");
        System.out.format(formatHeader, "Giocatore", "finish Order","Nave bella", "Carditi", "Merci", "Losses", "Totale");
        System.out.format("+------------+--------------+-----------+---------+--------+--------+-------+%n");

        for (PlayerScore score : scores) {
            System.out.format(formatRow,
                    score.getNickName(),
                    score.getFishOrderScore(),
                    score.getBestLookingShipScore(),
                    score.getCredits(),
                    score.getGoodRewardScore(),
                    -score.getLossesScore(),
                    score.getTotalScore()
            );
        }

        System.out.format("+------------+--------------+-----------+---------+--------+--------+-------+%n");
    }
}
