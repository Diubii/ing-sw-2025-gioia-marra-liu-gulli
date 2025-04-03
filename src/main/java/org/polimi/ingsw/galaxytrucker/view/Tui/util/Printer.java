package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.enums.ConsoleColor;
import org.polimi.ingsw.galaxytrucker.enums.PrinterLabels;

public class Printer {
    public static void printLabel(PrinterLabels label, ConsoleColor color) {
        System.out.print(color.getAnsiColorCode() + "[" + label + "]" + ConsoleColor.Reset.getAnsiColorCode() + " ");
    }

    public static void printlnWithLabel(PrinterLabels label, ConsoleColor color, String text) {
        printLabel(label, color);
        System.out.println(text);
    }
}
