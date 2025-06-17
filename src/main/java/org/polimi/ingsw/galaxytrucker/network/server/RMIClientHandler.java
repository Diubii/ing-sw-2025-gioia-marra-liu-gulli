package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.AskPositionUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RMIClientHandler implements ClientHandler, Serializable {

    private final UUID clientID;
    private final ClientInterfaceRMI remoteClient;

    public RMIClientHandler(ClientInterfaceRMI remoteClient, ServerController serverController) {
        clientID = UUID.randomUUID();
        this.remoteClient = remoteClient;
        serverController.startNewHeartbeat(this);
    }

    @Override
    public UUID getClientID() {
        return clientID;
    }

    @Override
    public void sendMessage(NetworkMessage message) {
        try {
            remoteClient.receiveMessage(message);

            NetworkMessageType type = message.accept(new NetworkMessageNameVisitor());
            if (type != NetworkMessageType.HeartbeatRequest) {
                System.out.println(TuiColor.BG_BLUE.toString() + TuiColor.BLACK + "RESPONSE SENT " + type + TuiColor.RESET);
            }
            if (type == NetworkMessageType.PhaseUpdate) {
                System.out.println("HAS STATE: " + ((PhaseUpdate) message).getState());
            }

            if (type == NetworkMessageType.AskPositionUpdate) {
                AskPositionUpdate mess = (AskPositionUpdate) message;
                System.out.println("ASKING TO " + mess.nickname + "ID" + mess.getID());
            }
        } catch (PlayerAlreadyExistsException | TooManyPlayersException | IOException | ExecutionException | InvalidTilePosition e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "RMI client"; //TODO: Possibile ottenere l'indirizzo?
    }
}
