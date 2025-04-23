package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.ClientController2;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class ClientNetworkMessageVisitor implements  NetworkMessageVisitorsInterface<Void> {

    ClientController2 clientController;

    public ClientNetworkMessageVisitor(ClientController2 clientController) {
        this.clientController = clientController;
    }

    @Override
    public Void visit(CreateRoomRequest createRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException {
        try {
            clientController.getClient().sendMessage(createRoomRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the createRoomRequest " + e.getMessage());
        }

        return null;
    }

    @Override
    public Void visit(JoinRoomRequest joinRoomRequest) throws TooManyPlayersException, PlayerAlreadyExistsException {
        try {
            clientController.getClient().sendMessage(joinRoomRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.err.println("Error occurred while sending the JoinRoomRequest: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Void visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest) throws ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(joiniRoomOptionsRequest);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the JoinRoomOptionsRequest: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Void visit(NicknameRequest nicknameRequest) throws TooManyPlayersException, PlayerAlreadyExistsException, ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(nicknameRequest);

        } catch (IOException e) {
            System.err.println("rror occurred while sending the NicknameRequest: " + e.getMessage());
        }
        return null;
    }
    @Override
    public Void visit(DrawTileRequest drawTileRequest) throws ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(drawTileRequest);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the DrawTileRequest " + e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(FetchShipRequest fetchShipRequest) throws ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(fetchShipRequest);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the fetchshipRequest: " + e.getMessage());
        }

        return null;
    }
    @Override
    public Void visit(CheckShipStatusRequest checkShipStatusRequest) throws ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(checkShipStatusRequest);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the checkShipStatusRequest: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(DiscardTileRequest discardTileRequest) throws ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(discardTileRequest);
            clientController.setCurrentTileInHand(null);
            clientController.setCurrentTilePosition(null);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the discardRequest: " + e.getMessage());
        }
        return null;

    }

    @Override
    public Void visit(FinishBuildingRequest finishBuildingRequest) throws ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(finishBuildingRequest);
            clientController.setCurrentTileInHand(null);
            clientController.setCurrentTilePosition(null);
            clientController.setCurrentShip(null);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the FinishBuildingRequest: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(PlaceTileRequest placeTileRequest) throws ExecutionException, InterruptedException {
        try {
            clientController.getClient().sendMessage(placeTileRequest);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the placeTileRequest: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(FetchShipResponse fetchShipResponse) {
        try {
            clientController.getClient().sendMessage(fetchShipResponse);
        } catch (IOException e) {
            System.err.println("Error occurred while sending the FetchShipRequest: " + e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
//    @Override
//    public Void visit(myShipUpdate myShipUpdate) {
//        try {
//            clientController.getClient().sendMessage(myShipUpdate);
//        } catch (IOException e) {
//            System.err.println("Error occurred while sending the myShipUpdate: " + e.getMessage());
//        }
//        return null;
//    }

    @Override
    public Void visit(AdventureCardExampleResponse adventureCardExampleResponse) {
        return null;
    }

    @Override
    public Void visit(JoinRoomResponse joinRoomResponse) {
        clientController.handleJoinRoomResponse(joinRoomResponse);
        return null;
    }

    @Override
    public Void visit(NicknameResponse nicknameResponse) {
        clientController.handleNicknameResponse(nicknameResponse);
        return null;
    }

    @Override
    public Void visit(JoinRoomOptionsResponse joinRoomOptionsResponse) {
        clientController.handleJoinRoomOptionsResponse(joinRoomOptionsResponse);
        return null;
    }

    @Override
    public Void visit(DrawTileResponse drawTileResponse) {
        return null;
    }

    @Override
    public Void visit(PlaceTileResponse placeTileResponse) {
        return null;
    }



    @Override
    public Void visit(ShipUpdate shipUpdate) {
        return null;
    }


    @Override
    public Void visit(SERVER_INFO serverInfo) {
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
        clientController.handleTileDrawnUpdate(tileDrawnUpdate);
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
    public Void visit(PhaseUpdate message) {
        clientController.handlePhaseUpdate(message);
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


    //    @Override
//    public Void visit(DecksUpdate decksUpdate) {
//
//        return null;
//    }


}