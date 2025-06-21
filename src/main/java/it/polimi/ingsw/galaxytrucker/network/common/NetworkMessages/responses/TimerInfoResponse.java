package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Timer info response.
 */
public class TimerInfoResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Unique serialization ID

    private ArrayList<TimerInfo> timerInfoList; // List of TimerInfo objects

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Instantiates a new Timer info response.
     */
// Default constructor
    public TimerInfoResponse() {
        super(0); // Default ID
        this.timerInfoList = new ArrayList<>();
    }

    /**
     * Instantiates a new Timer info response.
     *
     * @param id the id
     */
// Parameterized constructor
    public TimerInfoResponse(int id) {
        super(id); // Call parent constructor
        this.timerInfoList = new ArrayList<>(); // Initialize the list
    }

    /**
     * Gets timer info list.
     *
     * @return the timer info list
     */
// Getter for TimerInfo list
    public ArrayList<TimerInfo> getTimerInfoList() {
        return timerInfoList;
    }

    /**
     * Sets timer info list.
     *
     * @param timerInfoList the timer info list
     */
// Setter for TimerInfo list
    public void setTimerInfoList(ArrayList<TimerInfo> timerInfoList) {
        this.timerInfoList = timerInfoList;
    }
}
