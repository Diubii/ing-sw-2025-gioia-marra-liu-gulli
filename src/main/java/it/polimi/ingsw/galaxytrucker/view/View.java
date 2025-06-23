package it.polimi.ingsw.galaxytrucker.view;

import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.GameState;
import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import it.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import it.polimi.ingsw.galaxytrucker.model.*;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Interface for all types of view to implement.
 * Must be implemented so that the ClientController can interact with a view.
 */
public interface View {

    //Every view must have the reference to the Model and ClientController to implement MVC

    /**
     * Describes if the view is "concurrent" and always shows  everithing or not.
     * For example a Grafical interface is concurrent, a text interface can depend on implementation,
     * If everithing is redrawn on every update even a text interface can "always show updates"
     * @return
     */
    Boolean autoShowUpdates();

    void forceReset();

    void askServerInfo();

    void askNickname() throws IOException, ExecutionException, InterruptedException;

    void showShip(Ship targetShipView, String nickname);

    void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException;

    void showGenericMessage(String s,Boolean important);

    void showWaitOtherPlayers();

    void askJoinOrCreateRoom();

    void askRoomCode();

    void askCreateRoom() throws ExecutionException;

    void showLobbies(List<LobbyInfo> lobbies);

    void toShowCurrentMenu();

    void showPlayerJoined(PlayerInfo playerInfos);


    void handleChoiceForPhase(GameState phase);

    void showPlayersLobby(PlayerInfo myInfo, ArrayList<PlayerInfo> infoPlayer);

    void handlePhaseUpdate(PhaseUpdate phaseUpdate);

    //building
    void showBuildingMenu();

    void handleFaceUpTilesUpdate();

    void showFaceUpTiles();


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


    void showCheckShipMenu();

    void showEmbarkCrewMenu();

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

    void askSelectPlanetChoice(HashMap<Integer, Planet> landablePlanets);



    void askLoadGoodChoice();

    void askSelectGoodToLoad(ArrayList<Good> goods, Ship myShip);

    void askSelectGoodToDiscard(Ship myShip);

    void showEndGame(ArrayList<PlayerScore> scores);

    void askCollectRewards();

    void showTimerInfos();

    void showYouAreNowSpectating();


}
