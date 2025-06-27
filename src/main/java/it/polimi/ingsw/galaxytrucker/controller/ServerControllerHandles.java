package it.polimi.ingsw.galaxytrucker.controller;

import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerControllerHandles extends Remote {
    void handleNicknameRequest(NicknameRequest nicknameRequest, ClientHandler clientHandler) throws RemoteException;
    void handleCreateRoomRequest(CreateRoomRequest createRoomRequest, ClientHandler clientHandler) throws RemoteException;
    void handleJoinRoomOptionsRequest(JoiniRoomOptionsRequest joiniRoomOptionsRequest, ClientHandler clientHandler) throws RemoteException;
    void handleJoinRoomRequest(JoinRoomRequest joinRoomRequest, ClientHandler clientHandler) throws RemoteException;
    void handleDrawTileRequest(DrawTileRequest drawTileRequest, ClientHandler clientHandler) throws RemoteException;
    void handleCheckShipStatusRequest(CheckShipStatusRequest message, ClientHandler clientHandler) throws RemoteException;
    void handleAskPositionResponse(AskPositionResponse askPositionResponse, ClientHandler clientHandler) throws RemoteException;
    void handleSelectPlanetResponse(SelectPlanetResponse selectPlanetResponse, ClientHandler clientHandler) throws RemoteException;
    void handleFinishBuildingRequest(FinishBuildingRequest finishBuildingRequest, ClientHandler clientHandler) throws RemoteException;
    void handleFinishBuildingRequest2(AskPositionResponse askPositionResponse, ClientHandler clientHandler) throws RemoteException;
    void handlePlaceTileRequest(PlaceTileRequest placeTileRequest, ClientHandler clientHandler) throws InvalidTilePosition, RemoteException;
    void handleDiscardTileRequest(DiscardTileRequest discardTileRequest, ClientHandler clientHandler) throws RemoteException;
    void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate, ClientHandler clientHandler) throws RemoteException;
    void handleActivateAdventureCardResponse(ActivateAdventureCardResponse activateAdventureCardResponse, ClientHandler clientHandler) throws RemoteException;
    void handleActivateComponentResponse(ActivateComponentResponse activateComponentResponse, ClientHandler clientHandler) throws RemoteException;
    void handleHeartbeatRequest(HeartbeatRequest ignoredHeartbeatRequest, ClientHandler clientHandler) throws RemoteException;
    void handleShipUpdate(ShipUpdate shipUpdate, ClientHandler clientHandler) throws RemoteException;
    void handleDiscardCrewMembersResponse(DiscardCrewMembersResponse discardCrewMembersResponse, ClientHandler clientHandler) throws RemoteException;
    void handleCollectRewardsResponse(CollectRewardsResponse collectRewardsResponse, ClientHandler clientHandler) throws RemoteException;
    void handleDrawAdventureCardRequest(DrawAdventureCardRequest drawAdventureCardRequest, ClientHandler clientHandler) throws RemoteException;
    void handleReadyTurnRequest(ReadyTurnRequest readyTurnRequest, ClientHandler clientHandler) throws RemoteException;
    void handleEarlyLandingRequest(EarlyLandingRequest earlyLandingRequest, ClientHandler clientHandler) throws RemoteException;
    void handleAskTimerInfoRequest(AskTimerInfoRequest askTimerInfoRequest, ClientHandler clientHandler) throws RemoteException;
    void handleFlipTimerRequest(FlipTimerRequest flipTimerRequest, ClientHandler clientHandler) throws RemoteException;
    void handleAskTrunkResponse(AskTrunkResponse askTrunkResponse,ClientHandler clientHandler) throws RemoteException;
}
