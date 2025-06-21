package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * The DoubleEngine class represents a specialized engine component that can provide increased power when charged.
 * It extends the basic Engine class and introduces charging functionality.
 */
public class DoubleEngine extends Engine {

    private Boolean charged;

    /**
     * Constructs a new DoubleEngine instance with specified charge status and engine power.
     *
     * @param charged    Indicates whether the engine is initially charged.
     * @param enginePower The base power value for this engine.
     */
    @JsonCreator
    public DoubleEngine(@JsonProperty("charged") Boolean charged, @JsonProperty("enginePower") int enginePower) {
        super(enginePower);
        this.charged = charged;
    }

    /**
     * Gets the current effective power of this engine.
     * This method triggers a recalculation of the engine power based on its state.
     *
     * @return The current engine power.
     */
    @Override
    public int getEnginePower() {
        calculatePower();
        return enginePower;
    }

    /**
     * Calculates the engine power based on its rotation and charge status.
     * When rotated 0 degrees, a charged engine provides 2 power units and discharges.
     * An uncharged engine or one with non-zero rotation provides no power.
     */
    private void calculatePower() {
        if (getRotation() == 0) {
            if (charged) {
                enginePower = 2;
                charged = false;
            } else {
                enginePower = 0;
            }
        } else {
            enginePower = 0;
        }
    }

    /**
     * Checks if this engine is currently charged.
     *
     * @return true if the engine is charged, false otherwise.
     */
    @Override
    public Boolean isCharged() {
        return charged;
    }

    /**
     * Sets the charge status of this engine.
     *
     * @param charged The new charge status to set.
     */
    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

    /**
     * Creates a deep copy of this DoubleEngine instance.
     *
     * @return A new DoubleEngine object with the same properties as this instance.
     */
    @Override
    public DoubleEngine clone() {
        DoubleEngine copy = (DoubleEngine) super.clone();
        copy.charged = this.charged;
        return copy;
    }

    /**
     * Accepts a visitor to process this component.
     *
     * @param visitor The visitor to accept.
     * @return The result of the visit.
     */
    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }


}
