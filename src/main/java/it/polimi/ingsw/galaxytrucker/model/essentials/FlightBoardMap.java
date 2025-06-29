package it.polimi.ingsw.galaxytrucker.model.essentials;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * Represents the layout of the flight board with different start positions
 * depending on whether it's a learning match or standard match.
 */

public class FlightBoardMap implements Serializable {

    @Serial
    private static final long serialVersionUID = 456983986094L;

    ArrayList<FlightBoardMapSlot> flightBoardMapSlots;
    int nSlots;

    int fourthPos;
    int thirdPos;
    int secondPos;
    int firstPos;

    /**
     * Constructs the flight board layout based on match type.
     *
     * @param learnignMatch true if it's a learning match
     */
    public FlightBoardMap(Boolean learnignMatch) {
        if (learnignMatch) generateLearning();
        else generateLvl2();
    }

    /**
     * @return List of all slots on the flight board
     */
    public ArrayList<FlightBoardMapSlot> getFlightBoardMapSlots() {
        return flightBoardMapSlots;
    }


    private void generateLvl2() {
        nSlots = 24;
        fourthPos = 0;
        thirdPos = 1;
        secondPos = 3;
        firstPos = 6;

        flightBoardMapSlots = new ArrayList<>(nSlots);
        for (int j = 0; j < nSlots; j++) {
            flightBoardMapSlots.add(new FlightBoardMapSlot());
        }

        int i = 0;
        for (FlightBoardMapSlot slot : flightBoardMapSlots) {
            if (i == firstPos || i == secondPos || i == thirdPos || i == fourthPos) {
                slot.setIsStartingPos(true);
                slot.setStartingPos(i);
            } else slot.setIsStartingPos(false);

            i++;
        }
    }

    private void generateLearning() {

        nSlots = 18;
        fourthPos = 0;
        thirdPos = 1;
        secondPos = 2;
        firstPos = 4;

        flightBoardMapSlots = new ArrayList<>(nSlots);
        for (int j = 0; j < nSlots; j++) {
            flightBoardMapSlots.add(new FlightBoardMapSlot());
        }
        int i = 0;
        for (FlightBoardMapSlot slot : flightBoardMapSlots) {
            if (i == firstPos || i == secondPos || i == thirdPos || i == fourthPos) {
                slot.setIsStartingPos(true);
                slot.setStartingPos(i);
            } else slot.setIsStartingPos(false);

            i++;
        }


    }

    //+24 per caso in cui si va da giro -1, in caso in cui i step nel  flightboard siano < 0
    int increment = 0;

    public int getFirstPos() {
        return firstPos + increment;
    }

    public int getSecondPos() {
        return secondPos + increment;
    }

    public int getThirdPos() {
        return thirdPos + increment;
    }

    public int getFourthPos() {
        return fourthPos + increment;
    }
}
