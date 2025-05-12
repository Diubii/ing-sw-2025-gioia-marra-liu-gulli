package org.polimi.ingsw.galaxytrucker.visitors.Network;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

public interface NetworkMessageVisitorsInterface<T> {
    T visit(CreateRoomRequest createRoomRequest);

    T visit(JoinRoomRequest joinRoomRequest);

    T visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest);

    T visit(NicknameRequest nicknameRequest);

    T visit(AdventureCardExampleResponse adventureCardExampleResponse);

    T visit(JoinRoomResponse joinRoomResponse);

    T visit(NicknameResponse nicknameResponse);

    T visit(JoinRoomOptionsResponse joinRoomOptionsResponse);

    T visit(DrawTileResponse drawTileResponse);

    T visit(PlaceTileResponse placeTileResponse);

    T visit(DrawTileRequest drawTileRequest);

    T visit(FetchShipRequest fetchShipRequest);

    T visit(ShipUpdate shipUpdate);

    T visit(SERVER_INFO serverInfo);


    T visit(CheckShipStatusResponse checkShipStatusResponse);

    T visit(CheckShipStatusRequest checkShipStatusRequest);

    T visit(DiscardTileRequest discardTileRequest);

    T visit(FinishBuildingRequest finishBuildingRequest);

    T visit(PlayerLostUpdate playerLostUpdate);

    T visit(PlayerKickedUpdate playerKickedUpdate);


    T visit(TileDrawnUpdate tileDrawnUpdate);

    T visit(TileDiscardedUpdate tileDiscardedUpdate);

    T visit(PlaceTileRequest placeTileRequest);

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

    T visit(ActivateComponentRequest activateComponentRequest);

    T visit(ActivateComponentResponse activateComponentResponse);

    T visit(SelectPlanetRequest selectPlanetRequest);

    T visit(SelectPlanetResponse selectPlanetResponse);

    T visit(SelectedPlanetUpdate selectedPlanetUpdate);

    T visit(DiscardCrewMembersRequest discardCrewMembersRequest);

    T visit(DiscardCrewMembersResponse discardCrewMembersResponse);

    T visit(SellGoodsRequest sellGoodsRequest);

    T visit(SellGoodsResponse sellGoodsResponse);

    T visit(GameMessage gameMessage);

    T visit(HeartbeatRequest heartbeatRequest);

    T visit(HeartbeatResponse heartbeatResponse);

    T visit(DrawAdventureCardRequest drawAdventureCardRequest);

    T visit(EndTurnUpdate endTurnUpdate);

    T visit(AskDrawAdventureCardRequest askDrawAdventureCardRequest);

    T visit(EarlyLandingRequest earlyLandingRequest);

    T visit(ReadyTurnRequest readyTurnRequest);

    T visit(CollectRewardsRequest collectRewardsRequest);
    T visit(CollectRewardsResponse collectRewardsResponse);

    T visit(AskTrunkRequest askTrunkRequest);
    T visit(AskTrunkResponse askTrunkResponse);

    T visit(GameEndUpdate gameEndUpdate);


}
