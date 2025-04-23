package org.polimi.ingsw.galaxytrucker.view;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface View2 {
    void askServerInfo() throws IOException, ExecutionException, InterruptedException;
    void askNickname() throws IOException, ExecutionException, InterruptedException;



    void askJoinOrCreateRoom();
    void askRoomCode();

    void askFetchShip();
    void askDrawTile();
    void askTilePlacement();
    void askFinishBuilding();


    void showAdventureDeck();


    void handlePhaseUpdate(PhaseUpdate phaseUpdate);
    void showBuildingMenu();
    void showcheckShipMenu();
    void showembarkCrewMenu();

    void showGenericMessage(String message);

    void showLobbies(List<LobbyInfo> lobbies);
    void showPlayerJoined(Map<String, Color> playerInfo);
    void showError(String error);
    void showShipStatus(Object ship);

    void showShip(String targetname);
    void showTileTaken(int tileID);


    void checkShipMenu();

    void embarkCrewMenu();
}