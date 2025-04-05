package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.network.server.SocketClientHandler;
import org.polimi.ingsw.galaxytrucker.network.server.MessageManager;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerController {
    private final GameNetworkModel model;
    private final MessageManager messageManager;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();

    public ServerController(GameNetworkModel model) {
        this.model = model;
        messageManager = new MessageManager(model, this);
        model.setRealGame(new Game(4, false));
    }

    public GameNetworkModel getModel() {
        return model;
    }


    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public ArrayList<ClientHandler> getClients() {
        synchronized (clients) {
            return new ArrayList<>(clients);
        }
    }


    public synchronized void HandleNicknameRequest(NICKNAME_REQUEST message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        Boolean result = false;
        boolean flag = false;

        //get nickname & check
        String tempNick = message.getNickname();
        NICKNAME_RESPONSE nicknameResponse = new NICKNAME_RESPONSE(null);


        synchronized (model) {

            if (!model.getRealGame().isNicknameUsed(tempNick)) {

                if (model.getRealGame().getNumPlayers() == 0) {
                    model.getRealGame().setLearningMatch(true);
                    if (message.getLearningMatch()) model.getRealGame().initFlightBoard(true);
                    //controlli per gli input successivamente
//

                }

                Player player = new Player(message.getNickname(), 0, 0, flag);
                model.getRealGame().addPlayer(player);
                System.out.println("[+] ADDED " + player.getNickName());
                nicknameResponse.setResponse("VALID");

            } else {System.out.println("[+] NOT ADDED " + message.getNickname());
                nicknameResponse.setResponse("INVALID");
            }



        }

        clientHandler.sendMessage(nicknameResponse);
        System.out.println("SENDING RESPONSE\n");

    }

    public MessageManager getMessageManager() {
        return messageManager;
    }


}
