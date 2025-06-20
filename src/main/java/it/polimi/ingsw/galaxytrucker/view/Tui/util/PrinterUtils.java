package it.polimi.ingsw.galaxytrucker.view.Tui.util;

public class PrinterUtils {
    public static String getLabel(PrinterLabels label, TuiColor color) {
        return color + "[" + label + "]" + TuiColor.RESET;
    }

    public static String getTextWithLabel(PrinterLabels label, TuiColor color, String text) {
        return getLabel(label, color) + " " + text;
    }
}
