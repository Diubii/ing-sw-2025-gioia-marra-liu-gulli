package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;

public class CreateRoomRequest extends NetworkMessage implements Serializable  {

    @Serial
    private static final long serialVersionUID = 53L;

    private final int maxPlayers;
    private final Boolean isLearningMatch;
    private final String nickName;

    public CreateRoomRequest(int maxPlayers, Boolean isLearningMatch, String nickName) {
        this.maxPlayers = maxPlayers;
        this.isLearningMatch = isLearningMatch;
        this.nickName = nickName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getNickName() {
        return nickName;
    }
    public Boolean getIsLearningMatch() {
        return isLearningMatch;
    }

    public void accept(ServerController serverController, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }

}
