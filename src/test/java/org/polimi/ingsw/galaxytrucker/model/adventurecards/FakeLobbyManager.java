package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.PlayerState;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeLobbyManager extends LobbyManager {
    private final HashMap<String, ClientHandler> handlers = new HashMap<>();
    private final HashMap<String, ArrayList<NetworkMessage>> mockResponses;

    public FakeLobbyManager(ArrayList<Player> players, ServerController controller, HashMap<String, ArrayList<NetworkMessage>> mockResponses) {
        super(0);
        this.mockResponses = mockResponses;

//        FlightBoard flightBoard = new FlightBoard(false);
//        AtomicInteger i = new AtomicInteger(-1);
//        players.forEach(p -> {
//            p.setPlayerState(PlayerState.Playing);
//            addPlayerHandler(p.getNickName(), new FakeClientHandler(controller, p.getNickName(), this));
//            try {
//                getRealGame().addPlayer(p);
//            } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
//                throw new RuntimeException(e);
//            }
//            Color c = useNextAvailableColor();
//            getPlayerColors().put(p.getNickName(), c);
//            flightBoard.positionPlayer(c, i.incrementAndGet());
//        });
    }

    public void addPlayerHandler(String nickname, ClientHandler handler) {
        handlers.put(nickname, handler);
    }

    @Override
    public HashMap<String, ClientHandler> getPlayerHandlers() {
        return handlers;
    }

    public HashMap<String, ArrayList<NetworkMessage>> getMockResponses() {
        return mockResponses;
    }
}
