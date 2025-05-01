package org.polimi.ingsw.galaxytrucker.view;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
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
    void forceReset();

    void askNickname() throws IOException, ExecutionException, InterruptedException;

    void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException;

    void showGenericMessage(String s);

    void askJoinOrCreateRoom();
    void askRoomCode();
    void askCreateRoom() throws ExecutionException;
    void showLobbies(List<LobbyInfo> lobbies);

    void toShowCurrentMenu();
//    void showPlayerJoined(HashMap<String, Color> playerInfo);

    void showPlayerJoined(PlayerInfo playerInfo);

    void handleChoiceForPhase(GameState phase);
    void handlePhaseUpdate(PhaseUpdate phaseUpdate);

    //building
    void showBuildingMenu();
    void showFaceUpTiles();
    void showShip(Ship ship);

    void FetchMyShip();

    //asks_building
    void askShowFaceUpTiles();
    void askRotation();
    void askPosition() throws ExecutionException;
    void askViewAdventureDecks();
    void showTile(Tile tile);
    void askChooseTile();
    void askPickOrPlaceReservedTile(boolean isPicking);

    void askFetchShip();
    void askDrawTile();
    void askTilePlacement();
    void askFinishBuilding();

    void showcheckShipMenu();
    void showembarkCrewMenu();

    void askRemoveTile(Ship ship);

    void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException, InvalidTilePosition, TooManyPlayersException, PlayerAlreadyExistsException;

    void showFlightBoard();
    void showCurrentAdventureCard();



//    public void askNickname(Thread thread) throws IOException;
}
