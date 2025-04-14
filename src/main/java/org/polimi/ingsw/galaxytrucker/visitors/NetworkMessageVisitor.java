package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DrawTileResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.PlaceTileResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameStartedUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipViewUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.TileDrawnUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.TileDiscardedUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

public abstract class NetworkMessageVisitor {
    //INIT AND LOBBY
    public static void visit(SERVER_INFO serverInfo, ServerController serverController, ClientHandler clientHandler){}
    public static void visit(NicknameRequest nicknameRequest, ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException{}
    public static void visit(NicknameResponse nicknameResponse, ServerController serverController, ClientHandler clientHandler){}
    public static void visit(CreateRoomRequest createRoomRequest, ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException{
        serverController.handleCreateRoomRequest(createRoomRequest, clientHandler);
    }
    public static void visit(JoiniRoomOptionsRequest joiniRoomOptionsRequest, ServerController serverController, ClientHandler clientHandler){
        serverController.handleJoinRoomOptionsRequest(joiniRoomOptionsRequest, clientHandler);
    }
    public static void visit(JoinRoomRequest joinRoomRequest, ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException{
        serverController.handleJoinRoomRequest(joinRoomRequest, clientHandler);
    }
    public static void visit(GameStartedUpdate gameStartedUpdate, ServerController serverController, ClientHandler clientHandler){}


    //BUILDING

    //DrawTile
    public static void visit(DrawTileRequest drawTileRequest,  ServerController serverController, ClientHandler clientHandler){
        serverController.handleDrawTileRequest(drawTileRequest, clientHandler);
    }
    public static void visit(DrawTileResponse drawTileResponse, ServerController serverController, ClientHandler clientHandler){

    }
    public static void visit(TileDrawnUpdate tileDrawnUpdate, ServerController serverController, ClientHandler clientHandler){

    }


    //PlaceTile
    public static void visit(PlaceTileRequest placeTileRequest, ServerController serverController, ClientHandler clientHandler){

    }
    public static void visit(PlaceTileResponse placeTileResponse, ServerController serverController, ClientHandler clientHandler){

    }

    public static void visit(FetchShipStatusRequest fetchShipStatusRequest, ServerController serverController, ClientHandler clientHandler){

    }

    //DiscardTile
    public static void visit(DiscardTileRequest discardTileRequest, ServerController serverController, ClientHandler clientHandler){

    }
    public static void visit(TileDiscardedUpdate tileDiscardedUpdate, ServerController serverController, ClientHandler clientHandler){

    }

    public static void visit(ViewAdventureDecksRequest viewAdventureDecksRequest, ServerController serverController, ClientHandler clientHandler){

    }
    public static void visit(FinishBuildingRequest finishBuildingRequest, ServerController serverController, ClientHandler clientHandler){

    }
    public static void visit(ShipViewUpdate shipViewUpdate, ServerController serverController, ClientHandler clientHandler){

    }

}
