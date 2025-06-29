package it.polimi.ingsw.galaxytrucker.enums;

/**
 * Player colors available in the game.
 */

public enum Color {

    RED("RED"),
    YELLOW("YELLOW"),
    GREEN("GREEN"),
    BLUE("BLUE"),
    EMPTY("EMPTY");

    private final String colorString;

    Color(String colorString) {
        this.colorString = colorString;
    }

    /**
     * @return the color name as a string.
     */
    public String getColorString() {
        return colorString;
    }


    /**
     * Parses a string to a corresponding Color.
     *
     * @param text the color name
     * @return the matching Color enum
     * @throws IllegalArgumentException if no match is found
     */
    public static Color fromString(String text) {
        if (text != null) {
            for (Color color : Color.values()) {
                if (color.getColorString().equalsIgnoreCase(text.trim())) {
                    return color;
                }
            }
        }
        throw new IllegalArgumentException("Nessun valore Connector corrisponde a: " + text);
    }
}