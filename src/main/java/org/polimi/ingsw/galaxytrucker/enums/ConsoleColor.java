package org.polimi.ingsw.galaxytrucker.enums;

/**
 * This enum provides colors to use in a console in ANSI format. At each value is assigned a String.
 * @author Alessandro Giuseppe Gioia
 */
public enum ConsoleColor {
    Banner("\033[1;34m"),
    ServerSocket("\033[38;5;82m"),
    ClientSocket("\033[38;5;82m"),
    Reset("\033[0m");

    private final String ansiColorCode;

    ConsoleColor(String ansiColorCode){
        this.ansiColorCode = ansiColorCode;
    }

    public String getAnsiColorCode(){
        return ansiColorCode;
    }
}
