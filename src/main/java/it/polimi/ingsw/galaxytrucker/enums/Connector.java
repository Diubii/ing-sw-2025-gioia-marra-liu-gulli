package it.polimi.ingsw.galaxytrucker.enums;

/**
 * Types of connectors on ship components.
 */
public enum Connector {
    EMPTY("Empty"),
    SINGLE("Single"),
    DOUBLE("Double"),
    UNIVERSAL("Universal");

    private final String connectorString;

    // Costruttore per associare una stringa a ciascun valore
    Connector(String connectorString) {
        this.connectorString = connectorString;
    }

    /**
     * @return connector name as a string.
     */
    public String getConnectorString() {
        return connectorString;
    }


    // Metodo per convertire una stringa nell'enum
    /**
     * Converts a string to a matching Connector enum.
     *
     * @param text connector string
     * @return corresponding Connector enum
     * @throws IllegalArgumentException if no match is found
     */
    public static Connector fromString(String text) {
        if (text != null) {
            for (Connector connector : Connector.values()) {
                if (connector.getConnectorString().equalsIgnoreCase(text.trim())) {
                    return connector;
                }
            }
        }
        throw new IllegalArgumentException("Nessun valore Connector corrisponde a: " + text);
    }

}
