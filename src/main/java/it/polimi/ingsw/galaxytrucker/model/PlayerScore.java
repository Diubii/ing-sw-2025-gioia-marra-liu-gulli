package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the final score breakdown for a player.
 * Stores all sub-scores and the total result.
 */
public class PlayerScore implements Serializable {
    @Serial
    private static final long serialVersionUID = 1019019019L;

    private final String nickName;
    private final int bestLookingShipScore;
    private final int finishOrderScore;
    private final double goodRewardScore;
    private final int lossesScore;
    private final int credits;
    private final double totalScore;

    public PlayerScore(String nickName,
                       int bestLookingShipScore,
                       int finishOrderScore,
                       double goodRewardScore,
                       int lossesScore,
                       int credits) {

        this.nickName = nickName;
        this.bestLookingShipScore = bestLookingShipScore;
        this.finishOrderScore = finishOrderScore;
        this.goodRewardScore = goodRewardScore;
        this.lossesScore = lossesScore;
        this.credits = credits;
        this.totalScore = bestLookingShipScore + finishOrderScore + goodRewardScore - lossesScore + credits;
    }


    //getter
    public String getNickName() {
        return nickName;
    }
    public int getBestLookingShipScore() {
        return bestLookingShipScore;
    }
    public int getFinishOrderScore() {
        return finishOrderScore;
    }
    public double getGoodRewardScore() {
        return goodRewardScore;
    }
    public int getLossesScore() {
        return lossesScore;
    }
    public int getCredits() {
        return credits;
    }
    public double getTotalScore() {
        return totalScore;
    }

}

