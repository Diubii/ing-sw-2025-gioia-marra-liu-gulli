package org.polimi.ingsw.galaxytrucker.view.Tui.util;

public class Printer {
    public static void printLabel(PrinterLabels label, TuiColor color) {
        System.out.print(color.toString() + "[" + label + "]" + TuiColor.RESET.toString() + " ");
    }

    public static void printlnWithLabel(PrinterLabels label, TuiColor color, String text) {
        printLabel(label, color);
        System.out.println(text);
    }
}
