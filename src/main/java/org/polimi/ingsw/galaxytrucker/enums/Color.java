package org.polimi.ingsw.galaxytrucker.enums;

public enum Color {

    RED("RED"),
    YELLOW("YELLOW"),
    GREEN("GREEN"),
    BLUE("BLUE"),
    EMPTY("EMPTY");

    private final String colorString;

    // Costruttore per associare una stringa a ciascun valore
    Color(String colorString) {
        this.colorString = colorString;
    }

    public String getColorString() {
        return colorString;
    }

    // Metodo per convertire una stringa nell'enum
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
