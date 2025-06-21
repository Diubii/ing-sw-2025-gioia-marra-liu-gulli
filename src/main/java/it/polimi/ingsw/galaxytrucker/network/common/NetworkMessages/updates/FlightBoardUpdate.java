package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.FlightBoard;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Flight board update.
 */
public class FlightBoardUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 568549530L;

    private final FlightBoard flightBoard;
    private int id = -1;

    /**
     * Instantiates a new Flight board update.
     *
     * @param flightBoard the flight board
     * @param id          the id
     */
    public FlightBoardUpdate(FlightBoard flightBoard, int id) {
        this.flightBoard = flightBoard;
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Instantiates a new Flight board update.
     *
     * @param flightBoard the flight board
     */
    public FlightBoardUpdate(FlightBoard flightBoard) {
        this.flightBoard = flightBoard;
    }

    /**
     * Gets flight board.
     *
     * @return the flight board
     */
    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
