package org.polimi.ingsw.galaxytrucker.visitors.Network;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

public class NetworkMessageCouplingVisitor implements NetworkMessageVisitorsInterface<NetworkMessageType> {
    @Override
    public NetworkMessageType visit(CreateRoomRequest createRoomRequest) {
        return NetworkMessageType.CreateRoomResponse;
    }

    @Override
    public NetworkMessageType visit(JoinRoomRequest joinRoomRequest) {
        return NetworkMessageType.JoinRoomResponse;
    }

    @Override
    public NetworkMessageType visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest) {
        return NetworkMessageType.JoinRoomOptionsResponse;
    }

    @Override
    public NetworkMessageType visit(NicknameRequest nicknameRequest) {
        return NetworkMessageType.NicknameResponse;
    }

    @Override
    public NetworkMessageType visit(AdventureCardExampleResponse adventureCardExampleResponse) {
        return null;
    }

    @Override
    public NetworkMessageType visit(JoinRoomResponse joinRoomResponse) {
        return NetworkMessageType.JoinRoomRequest;
    }

    @Override
    public NetworkMessageType visit(NicknameResponse nicknameResponse) {
        return NetworkMessageType.NicknameRequest;
    }

    @Override
    public NetworkMessageType visit(JoinRoomOptionsResponse joinRoomOptionsResponse) {
        return NetworkMessageType.JoinRoomOptionsRequest;
    }

    @Override
    public NetworkMessageType visit(DrawTileResponse drawTileResponse) {
        return NetworkMessageType.DrawTileRequest;
    }

    @Override
    public NetworkMessageType visit(PlaceTileResponse placeTileResponse) {
        return NetworkMessageType.PlaceTileRequest;
    }

    @Override
    public NetworkMessageType visit(DrawTileRequest drawTileRequest) {
        return NetworkMessageType.DrawTileResponse;
    }

    @Override
    public NetworkMessageType visit(FetchShipRequest fetchShipRequest) {
        return NetworkMessageType.FetchShipResponse;
    }

    @Override
    public NetworkMessageType visit(ShipUpdate shipUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(SERVER_INFO serverInfo) {
        return null;
    }

    @Override
    public NetworkMessageType visit(CheckShipStatusResponse checkShipStatusResponse) {
        return NetworkMessageType.CheckShipStatusRequest;
    }

    @Override
    public NetworkMessageType visit(CheckShipStatusRequest checkShipStatusRequest) {
        return NetworkMessageType.CheckShipStatusResponse;
    }

    @Override
    public NetworkMessageType visit(DiscardTileRequest discardTileRequest) {
        return NetworkMessageType.DiscardTileResponse;
    }

    @Override
    public NetworkMessageType visit(FinishBuildingRequest finishBuildingRequest) {
        return NetworkMessageType.FinishBuildingResponse;
    }

    @Override
    public NetworkMessageType visit(PlayerLostUpdate playerLostUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(PlayerKickedUpdate playerKickedUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(TileDrawnUpdate tileDrawnUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(TileDiscardedUpdate tileDiscardedUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(PlaceTileRequest placeTileRequest) {
        return NetworkMessageType.PlaceTileResponse;
    }

    @Override
    public NetworkMessageType visit(FetchShipResponse fetchShipResponse) {
        return NetworkMessageType.FetchShipRequest;
    }

    @Override
    public NetworkMessageType visit(ViewAdventureDecksRequest viewAdventureDecksRequest) {
        return NetworkMessageType.ViewAdventureDeckResponse;
    }

    @Override
    public NetworkMessageType visit(EndTimerUpdate endTimerUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(PhaseUpdate phaseUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(PlayerJoinedUpdate playerJoinedUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(CrewInitUpdate crewInitUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(AskPositionUpdate askPositionUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(AskPositionResponse askPositionResponse) {
        return null;
    }

    @Override
    public NetworkMessageType visit(MatchInfoUpdate matchInfoUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(DecksUpdate decksUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(FlightBoardUpdate flightBoardUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(FaceUpTileUpdate faceUpTileUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(DrawnAdventureCardUpdate drawnAdventureCardUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(ActivateAdventureCardRequest activateAdventureCardRequest) {
        return NetworkMessageType.ActivateAdventureCardResponse;
    }

    @Override
    public NetworkMessageType visit(ActivateAdventureCardResponse activateAdventureCardResponse) {
        return NetworkMessageType.ActivateAdventureCardRequest;
    }

    @Override
    public NetworkMessageType visit(ActivateComponentRequest activateComponentRequest) {
        return NetworkMessageType.ActivateComponentResponse;
    }

    @Override
    public NetworkMessageType visit(ActivateComponentResponse activateComponentResponse) {
        return NetworkMessageType.ActivateComponentRequest;
    }

    @Override
    public NetworkMessageType visit(SelectPlanetRequest selectPlanetRequest) {
        return NetworkMessageType.SelectPlanetResponse;
    }

    @Override
    public NetworkMessageType visit(SelectPlanetResponse selectPlanetResponse) {
        return NetworkMessageType.SelectPlanetRequest;
    }

    @Override
    public NetworkMessageType visit(SelectedPlanetUpdate selectedPlanetUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(DiscardCrewMembersRequest discardCrewMembersRequest) {
        return NetworkMessageType.DiscardCrewMembersResponse;
    }

    @Override
    public NetworkMessageType visit(DiscardCrewMembersResponse discardCrewMembersResponse) {
        return NetworkMessageType.DiscardCrewMembersRequest;
    }

    @Override
    public NetworkMessageType visit(SellGoodsRequest sellGoodsRequest) {
        return NetworkMessageType.SellGoodsResponse;
    }

    @Override
    public NetworkMessageType visit(SellGoodsResponse sellGoodsResponse) {
        return NetworkMessageType.SellGoodsRequest;
    }

    @Override
    public NetworkMessageType visit(GameMessage gameMessage) {
        return null;
    }

    @Override
    public NetworkMessageType visit(HeartbeatRequest heartbeatRequest) {
        return NetworkMessageType.HeartbeatResponse;
    }

    @Override
    public NetworkMessageType visit(HeartbeatResponse heartbeatResponse) {
        return NetworkMessageType.HeartbeatRequest;
    }

    @Override
    public NetworkMessageType visit(DrawAdventureCardRequest drawAdventureCardRequest) {
        return NetworkMessageType.DrawAdventureCardResponse;
    }

    @Override
    public NetworkMessageType visit(EndTurnUpdate endTurnUpdate) {
        return null;
    }

    @Override
    public NetworkMessageType visit(AskDrawAdventureCardRequest askDrawAdventureCardRequest) {
        return null;
    }

    @Override
    public NetworkMessageType visit(EarlyLandingRequest earlyLandingRequest) {
        return null;
    }
    @Override
    public NetworkMessageType visit(ReadyTurnRequest readyTurnRequest) {
        return NetworkMessageType.ReadyTurnRequest;
    }

    @Override
    public NetworkMessageType visit(CollectRewardsRequest collectRewardsRequest) {
        return NetworkMessageType.CollectRewardsResponse;
    }
    @Override
    public NetworkMessageType visit(CollectRewardsResponse collectRewardsResponse) {
        return NetworkMessageType.CollectRewardsRequest;
    }

    @Override
    public NetworkMessageType visit(AskTrunkRequest askTrunkRequest) {
        return NetworkMessageType.AskTrunkResponse;
    }

    @Override
    public NetworkMessageType visit(AskTrunkResponse askTrunkResponse) {
        return NetworkMessageType.AskTrunkRequest;
    }
}
