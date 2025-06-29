package it.polimi.ingsw.galaxytrucker.view.Tui.util;

/**
 * Utility class to format labels and messages with color for TUI output.
 */
public class PrinterUtils {
    public static String getLabel(PrinterLabels label, TuiColor color) {
        return color + "[" + label + "]" + TuiColor.RESET;
    }

    public static String getTextWithLabel(PrinterLabels label, TuiColor color, String text) {
        return getLabel(label, color) + " " + text;
    }
}
