package it.polimi.ingsw.galaxytrucker.model.essentials.components;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * Class representing a double cannon in the Galaxy Trucker game.
 * Extends the {@link Cannon} class and implements additional logic to manage the "charged" state.
 */
public class DoubleCannon extends Cannon {
    private Boolean charged;
    /**
     * Constructor for the DoubleCannon class.
     *
     * @param charged   Specifies if the cannon is charged at the time of creation.
     * @param firePower The initial value of the firepower.
     */
    @JsonCreator
    public DoubleCannon(@JsonProperty("charged") Boolean charged, @JsonProperty("firePower") Float firePower) {
        super(firePower);
        this.charged = charged;
    }
    /**
     * Gets the current firepower of the cannon.
     * The firepower is recalculated if necessary before returning the value.
     *
     * @return The current firepower.
     */
    @Override
    public Float getFirePower() {
        calculateFP();
        return FirePower;
    }
    /**
     * Private method to calculate the cannon's firepower.
     * Applies specific logic based on the cannon's orientation and the charged state.
     */
    private void calculateFP() {
        if (getRotation() == 0) {
            //FirePower =2
            if (charged == false) {
                FirePower = 1.0F;
            }
            else{
                FirePower = 2.0F;
                charged = false;
            }
            //Altrimenti rimane uguale
        } else {
            if (charged==false) {
                FirePower = 0.5F;
            } else { //Altrimenti rimane uguale
                charged = false;
                FirePower = 1.0F;
            }
        }
    }
    /**
     * Checks if the cannon is charged.
     *
     * @return {@code true} if the cannon is charged, {@code false} otherwise.
     */
    public Boolean isCharged() {
        return charged;
    }
    /**
     * Sets the charged state of the cannon.
     *
     * @param charged The new charged state of the cannon.
     */
    public void setCharged(Boolean charged) {
        this.charged = charged;
    }
    /**
     * Creates a copy of this DoubleCannon instance.
     * The method performs a deep clone of the "charged" state.
     *
     * @return A new instance of {@link DoubleCannon} with the same values as the original.
     */
    @Override
    public DoubleCannon clone() {
        DoubleCannon copy = (DoubleCannon) super.clone();
        copy.charged = this.charged;
        return copy;
    }
    /**
     * Method that accepts a visitor following the Visitor pattern.
     * Allows specific operations to be performed on the DoubleCannon object.
     *
     * @param visitor The visitor instance that will perform the operation.
     * @param <T>     The return type of the operation performed by the visitor.
     * @return The result of the operation performed by the visitor.
     */
    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }
}
