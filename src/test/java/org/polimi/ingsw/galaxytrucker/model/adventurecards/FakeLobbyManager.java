package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.SelectPlanetResponse;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FakeLobbyManager extends LobbyManager {
    private final HashMap<String, ClientHandler> handlers = new HashMap<>();
    private final ArrayList<Pair<Integer, CompletableFuture<NetworkMessage>>> pendingResponses = new ArrayList<>();

    public FakeLobbyManager() {
        super();
//        PlayerColors = new HashMap<>();
//        PlayerColors.put("Diubi", Color.RED);
//        PlayerColors.put("Smattimat", Color.YELLOW);
    }

    public void addPlayerHandler(String nickname, ClientHandler handler) {
        handlers.put(nickname, handler);
    }

    @Override
    public HashMap<String, ClientHandler> getPlayerHandlers() {
        return handlers;
    }

    @Override
    public void addPendingResponse(CompletableFuture<NetworkMessage> future, Integer id) {
//        Planet selectedPlanet = new Planet(false, null);
//        selectedPlanet.setOccupied(false); // Non ancora occupato
//        SelectPlanetResponse response = new SelectPlanetResponse(selectedPlanet);
//        future.complete(response);
//        pendingResponses.add(new Pair<>(id, future));
    }

    @Override
    public void completePendingResponse(Integer id, NetworkMessage future) {

    }

    @Override
    public ArrayList<Pair<Integer, CompletableFuture<NetworkMessage>>> getPendingResponses() {
        return pendingResponses;
    }
}
