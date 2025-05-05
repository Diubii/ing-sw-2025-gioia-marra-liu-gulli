package org.polimi.ingsw.galaxytrucker.network;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.HeartbeatRequest;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class Heartbeat implements Runnable {
    private final ServerController serverController;
    private final ClientHandler clientHandler;
    private CompletableFuture<NetworkMessage> heartbeatFuture;

    public Heartbeat(ServerController serverController, ClientHandler clientHandler) {
        this.serverController = serverController;
        this.clientHandler = clientHandler;
        this.heartbeatFuture = new CompletableFuture<>();
    }

    public ServerController getServerController() {
        return serverController;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public CompletableFuture<NetworkMessage> getHeartbeatFuture() {
        return heartbeatFuture;
    }

    @Override
    public void run() {
        final int maxTries = 5;
        //LobbyManager game = serverController.getLobbyFromHandler(clientHandler);
        //String nickname = serverController.getNicknameFromClientHandler(clientHandler, game);
        String heartbeatLabel = PrinterUtils.getLabel(PrinterLabels.Heartbeat, TuiColor.BRIGHT_RED);
        String clientAddress = clientHandler.toString();

        System.out.println(heartbeatLabel + " " + "Starting heartbeat for " + clientAddress + ".");

        while (serverController.getClients().contains(clientHandler)) {
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();

            clientHandler.sendMessage(heartbeatRequest);

            int tries = 0;


            while (tries != maxTries) {
                try {
                    Thread.sleep(Duration.ofSeconds(1)); //Aspetto un secondo prima di ritentare
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (!heartbeatFuture.isDone()) { //Controllo che la future sia stata completata
                    tries++; //Se non è ancora stata completata incremento il counter dei controlli
                    System.out.println(heartbeatLabel + " " + clientAddress + " is not responding. Number of tries: " + tries + ".");


                } else {
                    //System.out.println("[HEARTBEAT] Received heartbeat");
                    break; //Se ricevo l'heartbeat
                }

                if (tries == maxTries) {
                    try {
                        System.out.println(heartbeatLabel + " " + clientAddress + " is dead. Kicking from server.");
                        serverController.removeClient(clientHandler);
                    } catch (PlayerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            heartbeatFuture = new CompletableFuture<>();
        }
    }
}
