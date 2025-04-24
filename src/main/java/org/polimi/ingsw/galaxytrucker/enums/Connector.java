package org.polimi.ingsw.galaxytrucker.enums;

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

    public String getConnectorString() {
        return connectorString;
    }


    // Metodo per convertire una stringa nell'enum
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
