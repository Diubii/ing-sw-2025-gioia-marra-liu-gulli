package org.polimi.ingsw.galaxytrucker.visitors.Network;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface NetworkMessageVisitorsInterface<T> {
    T visit(CreateRoomRequest createRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition;

    T visit(JoinRoomRequest joinRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException, IOException, InvalidTilePosition;

    T visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest) throws ExecutionException, InterruptedException;

    T visit(NicknameRequest nicknameRequest) throws TooManyPlayersException, PlayerAlreadyExistsException, ExecutionException, InterruptedException;

    T visit(AdventureCardExampleResponse adventureCardExampleResponse);

    T visit(JoinRoomResponse joinRoomResponse);

    T visit(NicknameResponse nicknameResponse);

    T visit(JoinRoomOptionsResponse joinRoomOptionsResponse);

    T visit(DrawTileResponse drawTileResponse);

    T visit(PlaceTileResponse placeTileResponse);

    T visit(DrawTileRequest drawTileRequest) throws ExecutionException, InterruptedException;

    T visit(FetchShipRequest fetchShipRequest) throws ExecutionException, InterruptedException;

    T visit(ShipUpdate shipUpdate);

    T visit(SERVER_INFO serverInfo);

    T visit(NUM_PLAYERS_REQUEST numPlayersRequest);

    T visit(CheckShipStatusResponse checkShipStatusResponse);

    T visit(CheckShipStatusRequest checkShipStatusRequest) throws ExecutionException, InterruptedException;

    T visit(DiscardTileRequest discardTileRequest) throws ExecutionException, InterruptedException;

    T visit(FinishBuildingRequest finishBuildingRequest) throws ExecutionException, InterruptedException;

    T visit(PlayerRemovedUpdate removedPlayerUpdate);



    T visit(TileDrawnUpdate tileDrawnUpdate);

    T visit(TileDiscardedUpdate tileDiscardedUpdate);

    T visit(PlaceTileRequest placeTileRequest) throws InvalidTilePosition, ExecutionException, InterruptedException;

    T visit(FetchShipResponse fetchShipResponse);

    T visit(ViewAdventureDecksRequest viewAdventureDecksRequest);

    T visit(EndTimerUpdate endTimerUpdate);

    T visit(PhaseUpdate phaseUpdate);

    T visit(PlayerJoinedUpdate playerJoinedUpdate);

    T visit(CrewInitUpdate crewInitUpdate);

    T visit(AskPositionUpdate askPositionUpdate);

    T visit(AskPositionResponse askPositionResponse);

    T visit(MatchInfoUpdate matchInfoUpdate);

    T visit(DecksUpdate decksUpdate);

    T visit(FlightBoardUpdate flightBoardUpdate);



    T visit(FaceUpTileUpdate faceUpTileUpdate);

    T visit(DrawnAdventureCardUpdate drawnAdventureCardUpdate);

    T visit(ActivateAdventureCardRequest activateAdventureCardRequest);
    T visit(ActivateAdventureCardResponse activateAdventureCardResponse);

    T visit(ActivateDoubleEnginesRequest activateDoubleEnginesRequest);
    T visit(ActivateDoubleEnginesResponse activateDoubleEnginesResponse);

    T visit(SelectPlanetRequest selectPlanetRequest);
    T visit(SelectPlanetResponse selectPlanetResponse);
    T visit(SelectedPlanetUpdate selectedPlanetUpdate);

    T visit(DiscardCrewMembersRequest discardCrewMembersRequest);
    T visit(DiscardCrewMembersResponse discardCrewMembersResponse);
}
