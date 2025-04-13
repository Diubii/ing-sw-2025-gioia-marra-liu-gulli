package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameStartedUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

public abstract class NetworkMessageVisitor {
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
    public static void visit(DrawTileRequest drawTileRequest,  ServerController serverController, ClientHandler clientHandler){
        serverController.handleDrawTileRequest(drawTileRequest, clientHandler);
    }
    public static void visit(GameStartedUpdate gameStartedUpdate, ServerController serverController, ClientHandler clientHandler){}
}
