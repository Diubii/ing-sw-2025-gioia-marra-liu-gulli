package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ClientNetworkMessageVisitor implements  NetworkMessageVisitorsInterface<Void> {

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
        try {
            clientController.getClient().sendMessage(drawTileRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the DrawTileRequest " + e.getMessage());
        }
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
        try {
            clientController.getClient().sendMessage(fetchShipRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the fetchshipRequest: " + e.getMessage());
        }

        return null;
    }
    @Override
    public Void visit(CheckShipStatusRequest checkShipStatusRequest) {
        try {
            clientController.getClient().sendMessage(checkShipStatusRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the checkShipStatusRequest: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(DiscardTileRequest discardTileRequest) {
        try {
            clientController.getClient().sendMessage(discardTileRequest);
//            clientController.setCurrentTileInHand(null);
//            clientController.setCurrentTilePosition(null);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the discardRequest: " + e.getMessage());
        }
        return null;

    }

    @Override
    public Void visit(FinishBuildingRequest finishBuildingRequest) {
        try {
            clientController.getClient().sendMessage(finishBuildingRequest);
//            clientController.setCurrentTileInHand(null);
//            clientController.setCurrentTilePosition(null);
//            clientController.setCurrentShip(null);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the FinishBuildingRequest: " + e.getMessage());
        }
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
    public Void visit(NUM_PLAYERS_REQUEST numPlayersRequest) {
        return null;
    }

    @Override
    public Void visit(CheckShipStatusResponse checkShipStatusResponse) {
        return null;
    }

    @Override
    public Void visit(TileDrawnUpdate tileDrawnUpdate) {
//        clientController.handleTileDrawnUpdate(tileDrawnUpdate);
        return null;
    }

    @Override
    public Void visit(TileDiscardedUpdate tileDiscardedUpdate) {
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
        return null;
    }

    @Override
    public Void visit(AskPositionUpdate askPositionUpdate) {
        return null;
    }

    @Override
    public Void visit(AskPositionResponse askPositionResponse) {
        return null;
    }

    @Override
    public Void visit(MatchInfoUpdate matchInfoUpdate) {
        return null;
    }


}