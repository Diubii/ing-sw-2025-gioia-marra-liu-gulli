package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.FlightBoard;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class FlightBoardUpdate extends NetworkMessage implements Serializable {

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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
