package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.enums.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class FlightBoardMap {


    ArrayList<FlightBoardMapSlot> flightBoardMapSlots;
    int nSlots;

    int fourthPos ;
    int thirdPos;
    int secondPos ;
    int firstPos ;
    
    public FlightBoardMap(Boolean learnignMatch){
        if (learnignMatch) generateLearning();
        else generateLvl2();
    }

    public ArrayList<FlightBoardMapSlot> getFlightBoardMapSlots() {
        return flightBoardMapSlots;
    }




    private void generateLvl2() {
        nSlots = 24;
        fourthPos = 0;
        thirdPos = 1;
        secondPos = 3;
        firstPos = 6;

        flightBoardMapSlots = new ArrayList<>(Arrays.asList(new FlightBoardMapSlot[nSlots]));

        int i = 0;
        for (FlightBoardMapSlot slot : flightBoardMapSlots) {
            if (i == firstPos || i == secondPos || i == thirdPos || i == fourthPos ){
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

         flightBoardMapSlots = new ArrayList<>(Arrays.asList(new FlightBoardMapSlot[nSlots]));

         int i = 0;
         for (FlightBoardMapSlot slot : flightBoardMapSlots) {
             if (i == firstPos || i == secondPos || i == thirdPos || i == fourthPos ){
                 slot.setIsStartingPos(true);
                 slot.setStartingPos(i);
             } else slot.setIsStartingPos(false);

             i++;
         }


    }

    public int getFirstPos() {
        return firstPos;
    }
    public int getSecondPos() {
        return secondPos;
    }
    public int getThirdPos() {
        return thirdPos;
    }
    public int getFourthPos() {
        return fourthPos;
    }
}
