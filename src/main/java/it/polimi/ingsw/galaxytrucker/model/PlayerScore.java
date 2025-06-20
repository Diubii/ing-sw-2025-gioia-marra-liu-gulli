package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;

import java.io.Serial;
import java.io.Serializable;

public class PlayerScore implements Serializable {
    @Serial
    private static final long serialVersionUID = 1019019019L;

    private final String nickName;
    private final int bestLookingShipScore;
    private final int finishOrderScore;
    private final int goodRewardScore;
    private final int lossesScore;
    private final int credits;
    private final int totalScore;

    public PlayerScore(String nickName,
                       int bestLookingShipScore,
                       int finishOrderScore,
                       int goodRewardScore,
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
    @NeedsToBeCompleted


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
    public int getGoodRewardScore() {
        return goodRewardScore;
    }
    public int getLossesScore() {
        return lossesScore;
    }
    public int getCredits() {
        return credits;
    }
    public int getTotalScore() {
        return totalScore;
    }

}

