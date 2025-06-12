package org.polimi.ingsw.galaxytrucker.network;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.time.Duration;

public class Heartbeat extends Thread {
    private final ServerController serverController;
    private final ClientHandler clientHandler;

    private final static Duration sleepDuration = Duration.ofSeconds(5);
    private volatile boolean canBeInterrupted = true;


    //Se non ricevo il ping, killo il thread che sta dormendo aspettando che riceva il ping

    public Heartbeat(ServerController serverController, ClientHandler clientHandler) {
        this.setPriority(Thread.MAX_PRIORITY);
        this.serverController = serverController;
        this.clientHandler = clientHandler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

//    @Override
//    public void run() {
//        final int maxTries = 5;
//        String heartbeatLabel = PrinterUtils.getLabel(PrinterLabels.Heartbeat, TuiColor.BRIGHT_RED);
//        String clientAddress = clientHandler.toString();
//
//        System.out.println(heartbeatLabel + " " + "Starting heartbeat for " + clientAddress + ".");
//
//        while (true) {
//            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
//
//            clientHandler.sendMessage(heartbeatRequest);
//
//            int tries = 0;
//
//
//            while (tries != maxTries) {
//                try {
//                    Thread.sleep(Duration.ofSeconds(1)); //Aspetto un secondo prima di ritentare
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//                if (!heartbeatFuture.isDone()) { //Controllo che la future sia stata completata
//                    tries++; //Se non è ancora stata completata incremento il counter dei controlli
//                    System.out.println(heartbeatLabel + " " + clientAddress + " is not responding. Number of tries: " + tries + ".");
//
//
//                } else {
//                    //System.out.println("[HEARTBEAT] Received heartbeat");
//                    break; //Se ricevo l'heartbeat
//                }
//            }
//
//            if (tries == maxTries) {
//                try {
//                    System.out.println(heartbeatLabel + " " + clientAddress + " is dead. Kicking from server.");
//                    serverController.removeClient(clientHandler);
//                } catch (PlayerNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//                break;
//            }
//
//            heartbeatFuture = new CompletableFuture<>();
//        }
//    }

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

    public void regenerate() {
        if(!canBeInterrupted) return;

        this.interrupt();

        serverController.startNewHeartbeat(clientHandler);
        //System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.Heartbeat, TuiColor.BRIGHT_RED, "Regenerated heartbeat for " + clientHandler.toString() + "."));
    }
}
