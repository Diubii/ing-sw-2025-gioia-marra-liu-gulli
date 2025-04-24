package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class FlightBoardUpdate extends NetworkMessage  implements Serializable {

    @Serial
    private static final long serialVersionUID = 568549530L;

    private final FlightBoard flightBoard;
    private int id = -1;

    public FlightBoardUpdate(FlightBoard flightBoard, int id) {
        this.flightBoard = flightBoard;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public FlightBoardUpdate(FlightBoard flightBoard) {
        this.flightBoard = flightBoard;
    }
    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException, IOException {
        return visitor.visit(this);
    }
}
