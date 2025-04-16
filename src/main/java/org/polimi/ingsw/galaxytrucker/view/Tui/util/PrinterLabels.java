package org.polimi.ingsw.galaxytrucker.view.Tui.util;

/**
 * This enum provides colors to use in a console in ANSI format. At each value is assigned a String.
 * @author Alessandro Giuseppe Gioia
 */
public enum PrinterLabels {
    ServerRMI("RMI - Server"),
    ClientSocket("Socket - Client"),
    ServerSocket("Socket - Server"),
    LobbyInfo("Lobby Info");

    private final String label;

    PrinterLabels(String label){
        this.label = label;
    }

    public String toString(){
        return label;
    }
}
