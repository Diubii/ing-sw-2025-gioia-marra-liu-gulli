package org.polimi.ingsw.galaxytrucker.view;

import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface View {

    //La view deve avere il riferimento al clientController e anche al Model

    Boolean autoShowUpdates();

    void forceReset();

    void askNickname() throws IOException, ExecutionException, InterruptedException;

    void showShip(Ship targetShipView);

    void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException;

    void showGenericMessage(String s);

    void askJoinOrCreateRoom();

    void askRoomCode();

    void askCreateRoom() throws ExecutionException;

    void showLobbies(List<LobbyInfo> lobbies);

    void toShowCurrentMenu();
//    void showPlayerJoined(HashMap<String, Color> playerInfo);

    void showPlayerJoined(PlayerInfo playerInfos);

//    void showPlayersLobby(ArrayList<PlayerInfo> playerInfos);

    void handleChoiceForPhase(GameState phase);

        void showPlayersLobby(PlayerInfo myinfo, ArrayList<PlayerInfo> infoPlayer);

    void handlePhaseUpdate(PhaseUpdate phaseUpdate);

    //building
    void showBuildingMenu();

    void showFaceUpTiles();


//    void showShip(Ship ship, String Nickname);

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

    void chooseComponent(Ship myShip, ActivatableComponent component) throws ExecutionException, InterruptedException;

    void chooseDiscardCrew(Ship myShip, int nCrewToDiscard) throws ExecutionException, InterruptedException;

    void chooseTroncone(ArrayList<Ship> tronconi) throws ExecutionException, InterruptedException;

    void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException, InvalidTilePosition, TooManyPlayersException, PlayerAlreadyExistsException;

    // Flight
    void askDrawCard();

    void showFlightBoard(FlightBoard flightBoard, ArrayList<PlayerInfo> infoPlayers, PlayerInfo myinfo);

    void showCurrentAdventureCard();


    void showFlightMenu();

    void askActivateAdventureCard();

    void askDiscardCrew(int nCrewToDiscard, Ship myShip);

    void askSelectPlanetChoice(ArrayList<Planet> planetChoices);



    void askLoadGoodChoice();

    void askSelectGoodToLoad(ArrayList<Good> goods, Ship myShip);

    void askSelectGoodToDiscard(Ship myShip);

    void showEndGame(ArrayList<PlayerScore> scores);

    void askCollectRewards();

    void showTimerInfos();

//    void askFlightMenuChoice();


//    public void askNickname(Thread thread) throws IOException;
}
