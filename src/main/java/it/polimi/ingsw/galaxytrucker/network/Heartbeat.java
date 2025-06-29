package it.polimi.ingsw.galaxytrucker.network;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.time.Duration;


/**
 * Monitors client connectivity via a heartbeat mechanism.
 * If the client does not send a ping within the expected time,
 * the client is considered disconnected and removed from the server.
 */
public class Heartbeat extends Thread {
    private final ServerController serverController;
    private final ClientHandler clientHandler;
    private final static Duration sleepDuration = Duration.ofSeconds(15);
    private boolean canBeInterrupted = true;
    //Se non ricevo il ping, killo il thread che sta dormendo aspettando che riceva il ping

    /**
     * Constructs a heartbeat monitor for a specific client.
     *
     * @param serverController the controller managing the game state.
     * @param clientHandler    the client to monitor.
     */
    public Heartbeat(ServerController serverController, ClientHandler clientHandler) {
        this.serverController = serverController;
        this.clientHandler = clientHandler;
        //this.setPriority(Thread.MAX_PRIORITY);
    }


    @Override
    public void run() {
        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException _){
            return;
        }

        canBeInterrupted = false;
        String heartbeatLabel = PrinterUtils.getLabel(PrinterLabels.Heartbeat, TuiColor.BRIGHT_RED);
        String clientAddress = clientHandler.toString();
        System.out.println(heartbeatLabel + " " + clientAddress + " is dead. Kicking from server.");
        serverController.removeClient(clientHandler);
    }


    /**
     * Interrupts the current heartbeat and starts a new one.
     * This method should be called whenever a client responds in time.
     */
    public void regenerate() {
        if(!canBeInterrupted) return;
        this.interrupt();
        serverController.startNewHeartbeat(clientHandler);
        //System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.Heartbeat, TuiColor.BRIGHT_RED, "Regenerated heartbeat for " + clientHandler.toString() + "."));
    }

    /**
     * Returns the client handler associated with this heartbeat.
     *
     * @return the client handler.
     */
    public ClientHandler getClientHandler() {
        return clientHandler;
    }
}
