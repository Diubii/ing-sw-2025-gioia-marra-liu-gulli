package org.polimi.ingsw.galaxytrucker.visitors.Network;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.ServerControllerHandles;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.IOException;
import java.rmi.RemoteException;

public class NetworkMessageVisitor implements NetworkMessageVisitorsInterface<Void> {
    private final ServerControllerHandles serverControllerHandles;
    private final ClientHandler clientHandler;

    public NetworkMessageVisitor(ServerControllerHandles serverControllerHandles, ClientHandler clientHandler) {
        this.serverControllerHandles = serverControllerHandles;
        this.clientHandler = clientHandler;
    }

    //INIT AND LOBBY

    @NeedsToBeCompleted
    @Override
    public Void visit(SERVER_INFO serverInfo) {
        return null;
    }

    @Override
    public Void visit(FlipTimerRequest flipTimerRequest) {
        // Logic for handling FlipTimerRequest should go here
        try {
            serverControllerHandles.handleFlipTimerRequest(flipTimerRequest, clientHandler);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    @Override
    public Void visit(NicknameRequest nicknameRequest) {
        try {
            serverControllerHandles.handleNicknameRequest(nicknameRequest, clientHandler);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public Void visit(NicknameResponse nicknameResponse) {
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(CreateRoomRequest createRoomRequest) {
        try {
            serverControllerHandles.handleCreateRoomRequest(createRoomRequest, clientHandler);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest) {
        try {
            serverControllerHandles.handleJoinRoomOptionsRequest(joiniRoomOptionsRequest, clientHandler);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(JoinRoomRequest joinRoomRequest) {
        try {
            serverControllerHandles.handleJoinRoomRequest(joinRoomRequest, clientHandler);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
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
        try {
            serverControllerHandles.handleDrawTileRequest(drawTileRequest, clientHandler);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
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
    public Void visit(PlaceTileRequest placeTileRequest) {
        try {
            serverControllerHandles.handlePlaceTileRequest(placeTileRequest, clientHandler);
        } catch (InvalidTilePosition | RemoteException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(PlaceTileResponse placeTileResponse) {
        return null;
    }

    @Override
    public Void visit(FetchShipRequest fetchShipRequest) {
        try {
            serverControllerHandles.handleFetchShipRequest(fetchShipRequest, clientHandler);
        }catch (RemoteException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(FetchShipResponse fetchShipResponse) {
        return null;
    }

    //Discard
    @Override
    public Void visit(DiscardTileRequest discardTileRequest) {
        try {
            serverControllerHandles.handleDiscardTileRequest(discardTileRequest, clientHandler);
        }
        catch (RemoteException e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Void visit(TileDiscardedUpdate tileDiscardedUpdate) {
        return null;
    }

    @Override
    public Void visit(ViewAdventureDecksRequest viewAdventureDecksRequest) {
        try {
            serverControllerHandles.handleViewAdventureDecksRequest(viewAdventureDecksRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
    public Void visit(FinishBuildingRequest finishBuildingRequest) {
        try {
            serverControllerHandles.handleFinishBuildingRequest(finishBuildingRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public Void visit(ShipUpdate shipUpdate) {
        try {
            serverControllerHandles.handleShipUpdate(shipUpdate, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public Void visit(AdventureCardExampleResponse adventureCardExampleResponse) {
        return null;
        //controllo se c'e una pending di questo tipo
    }


    @Override
    public Void visit(CheckShipStatusRequest checkShipStatusRequest) {
        try {
            serverControllerHandles.handleCheckShipStatusRequest(checkShipStatusRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(CheckShipStatusResponse checkShipStatusResponse) {
        return null;
    }

    @Override
    public Void visit(CrewInitUpdate crewInitUpdate) {
        try {
            serverControllerHandles.handleCrewInitUpdate(crewInitUpdate, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(AskPositionUpdate askPositionUpdate) {
        return null;
    }

    @Override
    public Void visit(AskPositionResponse askPositionResponse) {
        try {
            serverControllerHandles.handleAskPositionResponse(askPositionResponse, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
    public Void visit(ActivateAdventureCardRequest activateAdventureCardRequest) {
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(ActivateAdventureCardResponse activateAdventureCardResponse) {
        try {
            serverControllerHandles.handleActivateAdventureCardResponse(activateAdventureCardResponse, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(ActivateComponentRequest activateDoubleEnginesRequest) {
        return null;
    }

    @Override
    public Void visit(ActivateComponentResponse activateComponentResponse) {
        try {
            serverControllerHandles.handleActivateComponentResponse(activateComponentResponse, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(SelectPlanetRequest selectPlanetRequest) {
        return null;
    }

    @NeedsToBeCompleted
    @Override
    public Void visit(SelectPlanetResponse selectPlanetResponse) {
        try {
            serverControllerHandles.handleSelectPlanetResponse(selectPlanetResponse, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
        try {
            serverControllerHandles.handleDiscardCrewMembersResponse(discardCrewMembersResponse, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
        try {
            serverControllerHandles.handleDrawAdventureCardRequest(drawAdventureCardRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
        try {
            serverControllerHandles.handleHeartbeatRequest(heartbeatRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(EarlyLandingRequest earlyLandingRequest) {
        try {
            serverControllerHandles.handleEarlyLandingRequest(earlyLandingRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(ReadyTurnRequest readyTurnRequest) {
        try {
            serverControllerHandles.handleReadyTurnRequest(readyTurnRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(CollectRewardsRequest collectRewardsRequest) {
        return null;
    }

    @Override
    public Void visit(CollectRewardsResponse collectRewardsResponse) {
        try {
            serverControllerHandles.handleCollectRewardsResponse(collectRewardsResponse, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(AskTrunkRequest askTrunkRequest) {

        return null;
    }

    @Override
    public Void visit(AskTrunkResponse askTrunkResponse) {
        try {
            serverControllerHandles.handleAskTrunkResponse(askTrunkResponse, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(GameEndUpdate gameEndUpdate) {
        return null;
    }

    @Override
    public Void visit(AskTimerInfoRequest askTimerInfoRequest) {
        try {
            serverControllerHandles.handleAskTimerInfoRequest(askTimerInfoRequest, clientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Void visit(TimerInfoResponse timerInfoResponse) {
        return null;
    }
}
