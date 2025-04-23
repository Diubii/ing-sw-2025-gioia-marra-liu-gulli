package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

public class NetworkMessageNameVisitor implements NetworkMessageVisitorsInterface<NetworkMessageType> {

    @Override
    public NetworkMessageType visit(CreateRoomRequest request) {
        return NetworkMessageType.CreateRoomRequest;
    }

    @Override
    public NetworkMessageType visit(JoinRoomRequest request) {
        return NetworkMessageType.JoinRoomRequest;
    }

    @Override
    public NetworkMessageType visit(JoiniRoomOptionsRequest request) {
        return NetworkMessageType.JoinRoomOptionsRequest;
    }

    @Override
    public NetworkMessageType visit(NicknameRequest request) {
        return NetworkMessageType.NicknameRequest;
    }

    @Override
    public NetworkMessageType visit(AdventureCardExampleResponse response) {
        return NetworkMessageType.AdventureCardExampleResponse;
    }

    @Override
    public NetworkMessageType visit(JoinRoomResponse response) {
        return NetworkMessageType.JoinRoomResponse;
    }

    @Override
    public NetworkMessageType visit(NicknameResponse response) {
        return NetworkMessageType.NicknameResponse;
    }

    @Override
    public NetworkMessageType visit(JoinRoomOptionsResponse response) {
        return NetworkMessageType.JoinRoomOptionsResponse;
    }

    @Override
    public NetworkMessageType visit(DrawTileResponse drawTileResponse) {
        return NetworkMessageType.DrawTileResponse;
    }

    @Override
    public NetworkMessageType visit(PlaceTileResponse placeTileResponse) {
        return NetworkMessageType.PlaceTileResponse;
    }

    @Override
    public NetworkMessageType visit(DrawTileRequest drawTileRequest) {
        return NetworkMessageType.DrawTileRequest;
    }

    @Override
    public NetworkMessageType visit(FetchShipRequest fetchShipRequest) {
        return NetworkMessageType.FetchShipRequest;
    }

    @Override
    public NetworkMessageType visit(ShipUpdate shipUpdate) {
        return NetworkMessageType.ShipUpdate;
    }


    @Override
    public NetworkMessageType visit(SERVER_INFO serverInfo) {
        return NetworkMessageType.ServerInfo;
    }

    @NeedsToBeCompleted
    @Override
    public NetworkMessageType visit(NUM_PLAYERS_REQUEST numPlayersRequest) {
        return null;
    }

    @Override
    public NetworkMessageType visit(CheckShipStatusResponse checkShipStatusResponse) {
        return NetworkMessageType.CheckShipStatusResponse;
    }

    @Override
    public NetworkMessageType visit(CheckShipStatusRequest checkShipStatusRequest) {
        return NetworkMessageType.CheckShipStatusRequest;
    }

    @Override
    public NetworkMessageType visit(DiscardTileRequest discardTileRequest) {
        return NetworkMessageType.DiscardTileRequest;
    }

    @Override
    public NetworkMessageType visit(FinishBuildingRequest finishBuildingRequest) {
        return NetworkMessageType.FinishBuildingRequest;
    }



    @Override
    public NetworkMessageType visit(ViewAdventureDecksRequest viewAdventureDecksRequest) {
        return NetworkMessageType.ViewAdventureDeckRequest;
    }


    @Override
    public NetworkMessageType visit(PlaceTileRequest placeTileRequest) {
        return NetworkMessageType.PlaceTileRequest;
    }

    @Override
    public NetworkMessageType visit(PlayerJoinedUpdate playerJoinedUpdate) {
        return NetworkMessageType.PlayerJoinedUpdate;
    }

    @Override
    public NetworkMessageType visit(CrewInitUpdate crewInitUpdate) {
        return  NetworkMessageType.CrewInitUpdate;
    }

    @Override
    public NetworkMessageType visit(AskPositionUpdate askPositionUpdate) {
        return NetworkMessageType.AskPositionUpdate;
    }

    @Override
    public NetworkMessageType visit(AskPositionResponse askPositionResponse) {
        return NetworkMessageType.AskPositionResponse;
    }

    @Override
    public NetworkMessageType visit(MatchInfoUpdate matchInfoUpdate) {
        return NetworkMessageType.MatchInfoUpdate;
    }

    @Override
    public NetworkMessageType visit(TileDiscardedUpdate tileDiscardedUpdate) {
        return NetworkMessageType.TileDiscardedUpdate;
    }

    @Override
    public NetworkMessageType visit(TileDrawnUpdate tileDrawnUpdate) {
        return NetworkMessageType.TileDrawnUpdate;
    }

    @Override
    public NetworkMessageType visit(EndTimerUpdate endTimerUpdate) {
        return NetworkMessageType.EndTimer;
    }

    @Override
    public NetworkMessageType visit(PhaseUpdate phaseUpdate) {
        return NetworkMessageType.PhaseUpdate;
    }



    @Override
    public NetworkMessageType visit(FetchShipResponse fetchShipResponse) {
        return NetworkMessageType.FetchShipResponse;
    }
}
