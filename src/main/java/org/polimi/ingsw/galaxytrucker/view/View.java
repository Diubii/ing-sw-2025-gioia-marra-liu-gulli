package org.polimi.ingsw.galaxytrucker.view;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface View {
    void askNickname() throws IOException, ExecutionException, InterruptedException;

    void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException;

    void showGenericMessage(String s);

    void askJoinOrCreateRoom();
    void askRoomCode();
    void askCreateRoom();
    void showLobbies(List<LobbyInfo> lobbies);

//    void showPlayerJoined(HashMap<String, Color> playerInfo);

    void showPlayerJoined(PlayerInfo playerInfo);

    void handlePhaseUpdate(PhaseUpdate phaseUpdate);

    //building
    void showBuildingMenu();
    //asks_building
    void askRotation();
    void askPosition();
    void showTile(Tile tile);


    void askFetchShip();
    void askDrawTile();
    void askTilePlacement();
    void askFinishBuilding();

    void showcheckShipMenu();
    void showembarkCrewMenu();


//    public void askNickname(Thread thread) throws IOException;
}
