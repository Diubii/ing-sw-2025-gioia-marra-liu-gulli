package org.polimi.ingsw.galaxytrucker.view.Tui.util;

public enum TuiColor {
    // Colori base
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    ORANGE("\u001B[38;5;208m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),

    // Colori brillanti
    BRIGHT_BLACK("\u001B[90m"),
    BRIGHT_RED("\u001B[91m"),
    BRIGHT_GREEN("\u001B[92m"),
    BRIGHT_YELLOW("\u001B[93m"),
    BRIGHT_BLUE("\u001B[94m"),
    BRIGHT_PURPLE("\u001B[95m"),
    BRIGHT_CYAN("\u001B[96m"),
    BRIGHT_WHITE("\u001B[97m"),

    // Background (se ti serve)
    BG_RED("\u001B[41m"),
    BG_GREEN("\u001B[42m"),
    BG_YELLOW("\u001B[43m"),
    BG_BLUE("\u001B[44m"),
    BG_PURPLE("\u001B[45m"),
    BG_CYAN("\u001B[46m"),
    BG_WHITE("\u001B[47m");

    private final String code;

    TuiColor(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
