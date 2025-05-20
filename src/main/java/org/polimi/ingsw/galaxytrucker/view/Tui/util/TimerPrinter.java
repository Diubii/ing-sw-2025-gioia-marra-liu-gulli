package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.model.game.TimerInfo;

import java.util.ArrayList;
import java.util.List;

public class TimerPrinter {

    public static void printTimers(ArrayList<TimerInfo> timers) {
        StringBuilder hourglassLine = new StringBuilder();
        StringBuilder indexLine = new StringBuilder();
        StringBuilder valueLine = new StringBuilder();
        StringBuilder flippedLine = new StringBuilder();

        for (TimerInfo timer : timers) {
            // Rappresentazione clessidra
            String hourglass = timer.isFlipped() ? "⌛" : "⏳";
            hourglassLine.append(String.format("  %s   ", hourglass));
            indexLine.append(String.format("Idx:%-2d ", timer.getIndex()));
            valueLine.append(String.format("Sec:%-2d ", timer.getValue()));
            flippedLine.append(String.format("Flip:%s ", timer.isFlipped() ? "Y" : "N"));
        }

        // Stampa
        System.out.println(hourglassLine);
        System.out.println(indexLine);
        System.out.println(valueLine);
        System.out.println(flippedLine);
    }
}
