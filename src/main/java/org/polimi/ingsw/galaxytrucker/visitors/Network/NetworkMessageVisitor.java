package org.polimi.ingsw.galaxytrucker.visitors.Network;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class NetworkMessageVisitor implements NetworkMessageVisitorsInterface<Void> {
    private final ServerController serverController;
    private final ClientHandler clientHandler;

    public NetworkMessageVisitor(ServerController serverController, ClientHandler clientHandler) {
        this.serverController = serverController;
        this.clientHandler = clientHandler;
    }

    //INIT AND LOBBY

    @NeedsToBeCompleted
    @Override
    public Void visit(SERVER_INFO serverInfo) {
        return null;
    }



    @Override
    public Void visit(NicknameRequest nicknameRequest) throws TooManyPlayersException, PlayerAlreadyExistsException {
        serverController.handleNicknameRequest(nicknameRequest, clientHandler);
        return null;
    }

    public Void visit(NicknameResponse nicknameResponse) {
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(CreateRoomRequest createRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition {
        serverController.handleCreateRoomRequest(createRoomRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest) {
        serverController.handleJoinRoomOptionsRequest(joiniRoomOptionsRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(JoinRoomRequest joinRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException, IOException, InvalidTilePosition {
        serverController.handleJoinRoomRequest(joinRoomRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(JoinRoomResponse joinRoomResponse) {
        return null;
    }

    @Override
    public Void visit(JoinRoomOptionsResponse joinRoomOptionsResponse) {
        return null;
    }




    //BUILDING

    //DrawTile

    @Override
    public Void visit(DrawTileRequest drawTileRequest) {
        serverController.handleDrawTileRequest(drawTileRequest, clientHandler);
        return null;
    }


    @Override
    public Void visit(DrawTileResponse drawTileResponse) {
        return null;
    }


    @Override
    public Void visit(TileDrawnUpdate tileDrawnUpdate) {
        return null;
    }


    //PlaceTile

    @Override
    public Void visit(PlaceTileRequest placeTileRequest) throws InvalidTilePosition {
        serverController.handlePlaceTileRequest(placeTileRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(PlaceTileResponse placeTileResponse) {
        return null;
    }

    @Override
    public Void visit(FetchShipRequest fetchShipRequest) {
        serverController.handleFetchShipRequest(fetchShipRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(FetchShipResponse fetchShipResponse) {
        return null;
    }

    //Discard
    @Override
    public Void visit(DiscardTileRequest discardTileRequest) {
        serverController.handleDiscardTileRequest(discardTileRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(TileDiscardedUpdate tileDiscardedUpdate) {
        return null;
    }

    @Override
    public Void visit(ViewAdventureDecksRequest viewAdventureDecksRequest) {
        serverController.handleViewAdventureDecksRequest(viewAdventureDecksRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(EndTimerUpdate endTimerUpdate) {
        return null;
    }

    @Override
    public Void visit(PhaseUpdate phaseUpdate) {
        return null;
    }

    @Override
    public Void visit(PlayerJoinedUpdate playerJoinedUpdate) {
        return null;
    }



    @Override
    public Void visit(FinishBuildingRequest finishBuildingRequest) throws ExecutionException, InterruptedException {
        serverController.handleFinishBuildingRequest(finishBuildingRequest, clientHandler);
        return null;
    }



    @Override
    public Void visit(ShipUpdate shipUpdate) {
        return serverController.handleShipUpdate(shipUpdate, clientHandler);
    }



    @Override
    public Void visit(AdventureCardExampleResponse adventureCardExampleResponse) {
        return null;
        //controllo se c'e una pending di questo tipo
    }


    @Override
    public Void visit(CheckShipStatusRequest checkShipStatusRequest) {
        serverController.handleCheckShipStatusRequest(checkShipStatusRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(CheckShipStatusResponse checkShipStatusResponse) {
        return null;
    }

    @Override
    public Void visit(CrewInitUpdate crewInitUpdate){
        serverController.handleCrewInitUpdate(crewInitUpdate, clientHandler);
        return null;
    }

    @Override
    public Void visit(AskPositionUpdate askPositionUpdate) {
        return null;
    }

    @Override
    public Void visit(AskPositionResponse askPositionResponse) {
        serverController.handleAskPositionResponse(askPositionResponse, clientHandler);
        return null;
    }

    @Override
    public Void visit(MatchInfoUpdate matchInfoUpdate) {
        return null;
    }

    @Override
    public Void visit(DecksUpdate decksUpdate) {
        return null;
    }

    @Override
    public Void visit(FlightBoardUpdate flightBoardUpdate) {
        return null;
    }


    @Override
    public Void visit(FaceUpTileUpdate faceUpTileUpdate) {
        return null;
    }

    @Override
    public Void visit(DrawnAdventureCardUpdate drawnAdventureCardUpdate) {
        return null;
    }

    @Override
    public Void visit(ActivateAdventureCardRequest activateAdventureCardRequest){
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(ActivateAdventureCardResponse activateAdventureCardResponse){
        return null;
    }

    @Override
    public Void visit(ActivateComponentRequest activateDoubleEnginesRequest) {
        return null;
    }

    @Override
    public Void visit(ActivateComponentResponse activateComponentResponse){
        serverController.handleActivateDoubleEnginesResponse(activateComponentResponse, clientHandler);
        return null;
    }

    @Override
    public Void visit(SelectPlanetRequest selectPlanetRequest) {
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(SelectPlanetResponse selectPlanetResponse) {
        return null;
    }

    @Override
    public Void visit(SelectedPlanetUpdate selectedPlanetUpdate) {
        return null;
    }

    @Override
    public Void visit(DiscardCrewMembersRequest discardCrewMembersRequest) {
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(DiscardCrewMembersResponse discardCrewMembersResponse) {
        return null;
    }

    @Override
    public Void visit(SellGoodsRequest sellGoodsRequest) {
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(SellGoodsResponse sellGoodsResponse) {
        return null;
    }

    @Override
    public Void visit(GameMessage gameMessage) {
        return null;
    }

    @Override
    public Void visit(DrawAdventureCardRequest drawAdventureCardRequest) {
        serverController.handleDrawAdventureCardRequest(drawAdventureCardRequest, clientHandler);
        return null;
    }

    @Override
    public Void visit(EndTurnUpdate endTurnUpdate) {
        return null;
    }

    @Override
    public Void visit(AskDrawAdventureCardRequest askDrawAdventureCardRequest) {
        return null;
    }

    @Override
    public Void visit(PlayerLostUpdate playerLostUpdate) {
        return null;
    }

    @Override
    public Void visit(PlayerKickedUpdate playerKickedUpdate) {
        return null;
    }

    @Override
    public Void visit(HeartbeatRequest heartbeatRequest) {
        return null;
    }

    @Override
    public Void visit(HeartbeatResponse heartbeatResponse){
        serverController.handleHeartbeatResponse(heartbeatResponse, clientHandler);
        return null;
    }

    @Override
    public Void visit(EarlyLandingRequest earlyLandingRequest) {
        serverController.handleEarlyLandingRequest(earlyLandingRequest, clientHandler);
        return null;
    }
}
