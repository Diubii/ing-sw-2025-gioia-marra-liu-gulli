package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

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

            System.out.println(TuiColor.BG_BLUE + "RESPONSE SENT " + "[fare NetworkMessageNameVisitor se si vuole tenere questa print]" + TuiColor.RESET);
        } catch (RemoteException e) {
            System.err.println("Errore nella comunicazione con il client RMI: " + e.getMessage());
        } catch (IOException | ExecutionException | InvalidTilePosition e) {
            throw new RuntimeException(e);
        }
        catch (PlayerAlreadyExistsException | TooManyPlayersException e){
            System.err.println(e.getMessage());
        }
    }


}
