package org.polimi.ingsw.galaxytrucker.enums;

public enum AlienColor {
    BROWN("BROWN"),
    PURPLE("PURPLE"),
    EMPTY("EMPTY");

    private final String colorString;

    // Costruttore per associare una stringa a ciascun valore
    AlienColor(String colorString) {
        this.colorString = colorString;
    }

    public String getAlienColorString() {
        return colorString;
    }

    // Metodo per convertire una stringa nell'enum
    public static AlienColor fromString(String text) {
        if (text != null) {
            for (AlienColor color : AlienColor.values()) {
                if (color.getAlienColorString().equalsIgnoreCase(text.trim())) {
                    return color;
                }
            }
        }
        throw new IllegalArgumentException("Nessun valore Connector corrisponde a: " + text);
    }
}
