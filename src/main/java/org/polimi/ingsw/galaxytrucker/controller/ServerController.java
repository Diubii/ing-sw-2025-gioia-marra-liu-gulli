package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.server.MessageManager;

import java.net.Socket;
import java.util.ArrayList;

public class ServerController {
    private final GameNetworkModel model;
    private final MessageManager messageManager;
    private ArrayList<Socket> clientSockets = new ArrayList<>();


    public ServerController(GameNetworkModel model) {
        this.model = model;
        messageManager = new MessageManager(model, this);
        model.setRealGame(new Game(4, false));
    }

    public GameNetworkModel getModel() {
        return model;
    }

    public void addSocket(Socket socket) {
        clientSockets.add(socket);
    }

    public void removeSocket(Socket socket) {
        clientSockets.remove(socket);
    }

    public ArrayList<Socket> getClientSockets() {
        return clientSockets;
    }


    public synchronized void SocketNicknameRequest(NICKNAME_REQUEST message) throws TooManyPlayersException, PlayerAlreadyExistsException {
        Boolean result = false;
        boolean flag = false;

        //get nickname & check
        String tempNick = message.getNickname();

//        if (model.getRealGame().getNumPlayers() == 0){
//            Player player = new Player(message.getNickname(),0, 0, flag);
//            model.getRealGame().addPlayer(player);
//            System.out.println("[+] ADDED " + player.getNickName());
//        }
        if (!model.getRealGame().isNicknameUsed(tempNick)) {

            if (model.getRealGame().getNumPlayers() == 0) {
                model.getRealGame().setLearningMatch(true);
                if (message.getLearningMatch()) model.getRealGame().initFlightBoard(true);
                //controlli per gli input successivamente
//                model.getRealGame().setnMaxPlayer(message.);

            }
//            model.setRealGame(new Game(4, flag));
            Player player = new Player(message.getNickname(),0, 0, flag);
            model.getRealGame().addPlayer(player);
            System.out.println("[+] ADDED " + player.getNickName());

        }else  System.out.println("[+] NOT ADDED " + message.getNickname());


    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
