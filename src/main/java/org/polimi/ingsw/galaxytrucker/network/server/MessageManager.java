package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CreateRoomRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.JoinRoomRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.JoiniRoomOptionsRequest;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;

import java.util.ArrayList;

public class MessageManager {

    ArrayList<GameNetworkModel> GameModels = new ArrayList<>();
    private final ServerController controller;
    public MessageManager(ArrayList<GameNetworkModel> model, ServerController controller) {
        this.GameModels = model;
        this.controller = controller;
    }
    //logica

    public  void handle(NetworkMessage message, ClientHandler clientHandler) {


        if (message.accept(new ComponentNameVisitor()).equals(NetworkMessageType.NicknameRequest)) {
            try {
                controller.handleNicknameRequest((NicknameRequest) message,clientHandler);
            } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
            ;
        }

        if (message.accept(new ComponentNameVisitor()).equals(NetworkMessageType.CreateRoomRequest)){

            controller.handleCreateRoomRequest((CreateRoomRequest) message, clientHandler);
        }

        if (message.accept(new ComponentNameVisitor()).equals(NetworkMessageType.JoinRoomRequest)){
            controller.handleJoinRoomRequest((JoinRoomRequest) message, clientHandler);
        }

        if (message.accept(new ComponentNameVisitor()).equals(NetworkMessageType.JoinRoomOptionsRequest)){
            controller.handleJoinRoomOptionsRequest((JoiniRoomOptionsRequest) message, clientHandler);
        }




    }



}
