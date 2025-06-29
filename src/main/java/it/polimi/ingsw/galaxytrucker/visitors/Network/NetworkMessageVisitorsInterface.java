package it.polimi.ingsw.galaxytrucker.visitors.Network;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

/**
 * A generic visitor interface for handling different types of network messages.
 * <p>
 * Implements the Visitor design pattern to decouple message processing logic
 * from message structure, enabling flexible handling of different message types.
 *
 * @param <T> The return type for each visit method (e.g., Boolean, void, response object, etc.)
 */
public interface NetworkMessageVisitorsInterface<T> {
    T visit(CreateRoomRequest createRoomRequest);

    T visit(JoinRoomRequest joinRoomRequest);

    T visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest);

    T visit(NicknameRequest nicknameRequest);

    T visit(JoinRoomResponse joinRoomResponse);

    T visit(NicknameResponse nicknameResponse);

    T visit(JoinRoomOptionsResponse joinRoomOptionsResponse);

    T visit(DrawTileResponse drawTileResponse);

    T visit(PlaceTileResponse placeTileResponse);

    T visit(DrawTileRequest drawTileRequest);

    T visit(ShipUpdate shipUpdate);

    T visit(SERVER_INFO serverInfo);

    T visit(CheckShipStatusResponse checkShipStatusResponse);

    T visit(CheckShipStatusRequest checkShipStatusRequest);

    T visit(DiscardTileRequest discardTileRequest);

    T visit(FinishBuildingRequest finishBuildingRequest);

    T visit(PlayerLostUpdate playerLostUpdate);

    T visit(PlayerKickedUpdate playerKickedUpdate);

    T visit(TileDiscardedUpdate tileDiscardedUpdate);

    T visit(PlaceTileRequest placeTileRequest);

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

    T visit(ActivateComponentRequest activateComponentRequest);

    T visit(ActivateComponentResponse activateComponentResponse);

    T visit(SelectPlanetRequest selectPlanetRequest);

    T visit(SelectPlanetResponse selectPlanetResponse);

    T visit(SelectedPlanetUpdate selectedPlanetUpdate);

    T visit(DiscardCrewMembersRequest discardCrewMembersRequest);

    T visit(DiscardCrewMembersResponse discardCrewMembersResponse);

    T visit(GameMessage gameMessage);

    T visit(HeartbeatRequest heartbeatRequest);

    T visit(DrawAdventureCardRequest drawAdventureCardRequest);

    T visit(EndTurnUpdate endTurnUpdate);

    T visit(EarlyLandingRequest earlyLandingRequest);

    T visit(ReadyTurnRequest readyTurnRequest);

    T visit(CollectRewardsRequest collectRewardsRequest);

    T visit(CollectRewardsResponse collectRewardsResponse);

    T visit(AskTrunkRequest askTrunkRequest);

    T visit(AskTrunkResponse askTrunkResponse);

    T visit(GameEndUpdate gameEndUpdate);

    T visit(AskTimerInfoRequest askTimerInfoRequest);

    T visit(TimerInfoResponse timerInfoResponse);

    T visit(FlipTimerRequest flipTimerRequest);
}
