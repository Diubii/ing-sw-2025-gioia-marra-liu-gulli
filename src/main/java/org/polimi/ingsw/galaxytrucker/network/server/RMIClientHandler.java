package org.polimi.ingsw.galaxytrucker.network.server;

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
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

public class RMIClientHandler implements ClientHandler {

    private final ClientInterfaceRMI remoteClient;

    public RMIClientHandler(ClientInterfaceRMI remoteClient) {
        this.remoteClient = remoteClient;
    }

    @Override
    public void sendMessage(NetworkMessage message) {
        try {
                remoteClient.receiveMessage(message);

            System.out.println(TuiColor.BG_BLUE + "RESPONSE SENT " +  message.accept(new NetworkMessageNameVisitor()) + TuiColor.RESET);
            if (message.accept(new NetworkMessageNameVisitor()).equals(NetworkMessageType.PhaseUpdate)){
                System.out.println("HAS STATE: " + ((PhaseUpdate)message).getState());
            }

            if (message.accept(new NetworkMessageNameVisitor()).equals(NetworkMessageType.AskPositionUpdate)){
                AskPositionUpdate mess = (AskPositionUpdate) message;
                System.out.println("ASKING TO " + mess.nickname);
            }
        } catch (RemoteException e) {
            System.err.println("Errore nella comunicazione con il client RMI: " + e.getMessage());
        } catch (IOException | ExecutionException | InvalidTilePosition | InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (PlayerAlreadyExistsException | TooManyPlayersException e){
            System.err.println(e.getMessage());
        }
    }


}
