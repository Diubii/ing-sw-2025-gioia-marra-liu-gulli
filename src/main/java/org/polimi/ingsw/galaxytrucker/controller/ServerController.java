package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.server.MessageManager;

public class ServerController {
    private final GameNetworkModel model;
    private final MessageManager messageManager;
    public ServerController(GameNetworkModel model) {
        this.model = model;
        messageManager = new MessageManager(model, this);
    }


    public synchronized void nicknameRequest(NICKNAME_REQUEST message) throws TooManyPlayersException, PlayerAlreadyExistsException {
        Boolean result = false;
        boolean flag = false;

        //get nickname & check
        String tempNick = message.getNickname();

        if (!model.getRealGame().isNicknameUsed(tempNick)) {
            if (message.getLearningMatch()) flag = true;
            Player player = new Player(message.getNickname(),0, 0, flag);
            model.getRealGame().addPlayer(player);
            System.out.println("[+] ADDED " + player.getNickName());
        }else  System.out.println("[+] NOT ADDED " + message.getNickname());


    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
