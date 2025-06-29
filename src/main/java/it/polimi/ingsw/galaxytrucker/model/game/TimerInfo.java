package it.polimi.ingsw.galaxytrucker.model.game;

import it.polimi.ingsw.galaxytrucker.enums.TimerStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents information about a timer used during gameplay.
 */
public class TimerInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Unique serialization ID

    private int index;   // Numeric index
    private int value;   // Integer value
    private boolean flipped; // Boolean status
    private TimerStatus timerStatus = TimerStatus.OFF;


    /**
     * Default constructor (index 0, value 0, not flipped).
     */
    public TimerInfo() {
        this(0, 0, false);
    }

    /**
     * Constructs a timer with given index, value, and flip status.
     */
    public TimerInfo(int index, int value, boolean flipped) {
        this.index = index;
        this.value = value;
        this.flipped = flipped;
    }
    // Getters and Setters
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    // Overriding toString() for readable representation
    @Override
    public String toString() {
        return "TimerInfo{" +
                "index=" + index +
                ", value=" + value +
                ", status=" + flipped +
                '}';
    }

    public TimerStatus getTimerStatus() {
        return timerStatus;
    }

    public void setTimerStatus(TimerStatus timerStatus) {
        this.timerStatus = timerStatus;
    }
}
