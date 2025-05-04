package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class ClientNetworkMessageVisitor implements NetworkMessageVisitorsInterface<Void> {

    ClientController clientController;

    public ClientNetworkMessageVisitor(ClientController clientController) {
        this.clientController = clientController;
    }
    @Override
    public Void visit(SERVER_INFO serverInfo) {

            clientController.handleServerInfo(serverInfo);


        return null;
    }



    @Override
    public Void visit(NicknameRequest nicknameRequest) throws TooManyPlayersException, PlayerAlreadyExistsException {

        return null;
    }

    @Override
    public Void visit(DrawTileRequest drawTileRequest) {
        return null;
    }

    @Override
    public Void visit(CreateRoomRequest createRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException {

        return null;
    }

    @Override
    public Void visit(JoinRoomRequest joinRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException {

        return null;
    }

    @Override
    public Void visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest) {

        return null;
    }



    @Override
    public Void visit(FetchShipRequest fetchShipRequest) {

        return null;
    }
    @Override
    public Void visit(CheckShipStatusRequest checkShipStatusRequest) {

        return null;
    }

    @Override
    public Void visit(DiscardTileRequest discardTileRequest) {

        return null;

    }

    @Override
    public Void visit(FinishBuildingRequest finishBuildingRequest) {

        return null;
    }

    @Override
    public Void visit(PlaceTileRequest placeTileRequest) {

        return null;
    }

    @Override
    public Void visit(FetchShipResponse fetchShipResponse) {
        clientController.completeFuture(fetchShipResponse);
        return null;
    }

    @Override
    public Void visit(ViewAdventureDecksRequest viewAdventureDecksRequest) {
        try {
            clientController.getClient().sendMessage(viewAdventureDecksRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the viewAdventureDecksRequest: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Void visit(AdventureCardExampleResponse adventureCardExampleResponse) {
        return null;
    }

    @Override
    public Void visit(JoinRoomResponse joinRoomResponse) {
        clientController.completeFuture(joinRoomResponse);
        return null;
    }

    @Override
    public Void visit(NicknameResponse nicknameResponse) {
        clientController.completeFuture(nicknameResponse);
        return null;
    }

    @Override
    public Void visit(JoinRoomOptionsResponse joinRoomOptionsResponse) {
        clientController.completeFuture(joinRoomOptionsResponse);
        return null;
    }

    @Override
    public Void visit(DrawTileResponse drawTileResponse) {
        clientController.completeFuture(drawTileResponse);
        return null;
    }

    @Override
    public Void visit(PlaceTileResponse placeTileResponse) {
        clientController.completeFuture(placeTileResponse);
        return null;
    }



    @Override
    public Void visit(ShipUpdate shipUpdate) {
        clientController.handleShipUpdate(shipUpdate);
        return null;
    }





    @Override
    public Void visit(CheckShipStatusResponse checkShipStatusResponse) {
        clientController.completeFuture(checkShipStatusResponse);
        return null;
    }

    @Override
    public Void visit(TileDrawnUpdate tileDrawnUpdate) {
//        clientController.handleTileDrawnUpdate(tileDrawnUpdate);
        return null;
    }

    @Override
    public Void visit(TileDiscardedUpdate tileDiscardedUpdate) {
        clientController.handleTileDiscardUpdate(tileDiscardedUpdate);
        return null;
    }


    @Override
    public Void visit(EndTimerUpdate endTimerUpdate) {
        return null;
    }

    @Override
    public Void visit(PhaseUpdate phaseUpdate) {
        clientController.handlePhaseUpdate(phaseUpdate);
        return null;
    }

    @Override
    public Void visit(PlayerJoinedUpdate playerJoinedUpdate) {
        clientController.handlePlayerJoinedUpdate(playerJoinedUpdate);
        return null;
    }

    @Override
    public Void visit(CrewInitUpdate crewInitUpdate) {

        try {
            clientController.handleCrewInitUpdate(crewInitUpdate);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(AskPositionUpdate askPositionUpdate) {

        clientController.handleAskPositionUpdate(askPositionUpdate);
        return null;
    }

    @Override
    public Void visit(AskPositionResponse askPositionResponse) {
        return null;
    }

    @Override
    public Void visit(MatchInfoUpdate matchInfoUpdate) {
        clientController.handleMatchInfoUpdate(matchInfoUpdate);
        return null;
    }

    @Override
    public Void visit(DecksUpdate decksUpdate) {

        clientController.handleDecksUpdate(decksUpdate);
        return null;
    }

    @Override
    public Void visit(FlightBoardUpdate flightBoardUpdate) {

        clientController.handleFlightBoardUpdate(flightBoardUpdate);
        return null;
    }




    @Override
    public Void visit(FaceUpTileUpdate faceUpTileUpdate) {
        clientController.handleFaceUpTileUpdate(faceUpTileUpdate);
        return null;
    }

    @Override
    public Void visit(DrawnAdventureCardUpdate drawnAdventureCardUpdate){
        clientController.handleDrawnAdventureCardUpdate(drawnAdventureCardUpdate);
        return null;
    }

    @Override
    public Void visit(ActivateAdventureCardRequest activateAdventureCardRequest) {
        clientController.handleActivateAdventureCardRequest(activateAdventureCardRequest);
        return null;
    }

    @Override
    public Void visit(ActivateAdventureCardResponse activateAdventureCardResponse) {
        return null;
    }

    @Override
    public Void visit(ActivateComponentRequest activateComponentRequest){
        clientController.handleActivateComponentRequest(activateComponentRequest);
        return null;
    }

    @Override
    public Void visit(ActivateComponentResponse activateComponentResponse) {
        return null;
    }

    @Override
    public Void visit(SelectPlanetRequest selectPlanetRequest) {
        clientController.handleSelectPlanetRequest(selectPlanetRequest);
        return null;
    }

    @Override
    public Void visit(SelectPlanetResponse selectPlanetResponse) {
        return null;
    }

    @Override
    public Void visit(SelectedPlanetUpdate selectedPlanetUpdate) {
        clientController.handleSelectPlanetUpdate(selectedPlanetUpdate);
        return null;
    }

    @Override
    public Void visit(DiscardCrewMembersRequest discardCrewMembersRequest) {
        clientController.hardleDiscardCrewMembersRequest(discardCrewMembersRequest);
        return null;
    }

    @Override
    public Void visit(DiscardCrewMembersResponse discardCrewMembersResponse) {
        return null;
    }

    @Override
    public Void visit(SellGoodsRequest sellGoodsRequest) {
        return null;
    }

    @Override
    public Void visit(SellGoodsResponse sellGoodsResponse) {
        return null;
    }

    @Override
    public Void visit(GameMessage gameMessage) {
        clientController.handleGameMessage(gameMessage);
        return null;
    }

    @Override
    public Void visit(DrawAdventureCardRequest drawAdventureCardRequest) {
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
    public Void visit(PlayerRemovedUpdate removedPlayerUpdate) {
        clientController.handlePlayerRemovedUpdate(removedPlayerUpdate);
        return null;
    }
}