package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.util.ArrayList;

public class MessageManager {

    ArrayList<GameNetworkModel> GameModels = new ArrayList<>();
    private final ServerController serverController;
    public MessageManager(ArrayList<GameNetworkModel> model, ServerController serverController) {
        this.GameModels = model;
        this.serverController = serverController;
    }
    //logica

    public void handle(NetworkMessage message, ClientHandler clientHandler){
        try {
            message.accept(serverController, clientHandler);
        }catch (TooManyPlayersException | PlayerAlreadyExistsException e){
            System.out.println(e.getMessage());
        }
    }
}
