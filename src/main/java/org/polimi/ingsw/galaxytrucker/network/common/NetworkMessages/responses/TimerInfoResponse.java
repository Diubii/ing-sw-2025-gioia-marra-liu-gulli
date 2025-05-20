package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class TimerInfoResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Unique serialization ID

    private ArrayList<TimerInfo> timerInfoList; // List of TimerInfo objects

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    // Default constructor
    public TimerInfoResponse() {
        super(0); // Default ID
        this.timerInfoList = new ArrayList<>();
    }

    // Parameterized constructor
    public TimerInfoResponse(int id) {
        super(id); // Call parent constructor
        this.timerInfoList = new ArrayList<>(); // Initialize the list
    }

    // Getter for TimerInfo list
    public ArrayList<TimerInfo> getTimerInfoList() {
        return timerInfoList;
    }

    // Setter for TimerInfo list
    public void setTimerInfoList(ArrayList<TimerInfo> timerInfoList) {
        this.timerInfoList = timerInfoList;
    }
}
