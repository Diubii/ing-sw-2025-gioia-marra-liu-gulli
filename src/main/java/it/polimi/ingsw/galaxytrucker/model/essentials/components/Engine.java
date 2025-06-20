package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * Represents an engine component in a system. The engine extends the behavior of a generic component
 * by introducing the concept of engine power. This power can vary depending on the state of the component,
 * such as its rotation value.
 */
/**
 * Represents an engine component in a system.
 * The {@code Engine} class extends the behavior of the {@link Component} class
 * by introducing the concept of engine power. This allows the engine to have a power
 * value that can vary in response to the component's state, such as its rotation value.
 * <p>
 * The engine power is determined dynamically using the {@link #calculatePower()} method,
 * which sets the engine power to 0 if the component is in motion (i.e., its rotation value is not 0).
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 *     Engine engine = new Engine(100); // Create an engine with power 100
 *     System.out.println(engine.getEnginePower()); // Returns the calculated engine power
 * </pre>
 *
 * @see Component
 * @see ComponentVisitorInterface
 */
public class Engine extends Component {

    /**
     * Represents the power of the engine.
     * The power can be dynamically calculated based on the engine's rotation value.
     */
    protected int enginePower;

    /**
     * Constructs an {@code Engine} object with the specified engine power.
     *
     * @param enginePower the initial engine power
     */
    @JsonCreator
    public Engine(@JsonProperty("enginePower") int enginePower) {
        super(false); // Constructs the engine as an inactive component by default.
        this.enginePower = enginePower;
    }

    /**
     * Returns the current engine power. Before returning the value,
     * it calculates the power dynamically by checking the rotation value.
     * If the rotation is 0, the original engine power is retained;
     * otherwise, it is set to 0.
     *
     * @return the current engine power
     */
    public int getEnginePower() {
        calculatePower();
        return enginePower;
    }

    /**
     * Dynamically calculates the engine's power. If the component's
     * {@link #getRotation()} value is not 0, the engine power is set to 0.
     * Otherwise, the engine retains its original power.
     */
    private void calculatePower() {
        if (getRotation() != 0) {
            enginePower = 0;
        }
    }

    /**
     * Creates and returns a copy of the current {@code Engine} object.
     * All properties of the engine, including engine power,
     * are duplicated into the new instance.
     *
     * @return a cloned {@code Engine} object
     */
    @Override
    public Engine clone() {
        Engine copy = (Engine) super.clone();
        copy.enginePower = this.enginePower;
        return copy;
    }

    /**
     * Accepts a {@link ComponentVisitorInterface} to implement the visitor pattern.
     * This method allows operations to be performed on the {@code Engine}
     * without modifying the class.
     *
     * @param <T>      the return type of the visitor's operation
     * @param visitor  the visitor object performing an operation on this component
     * @return the result of the visitor's operation
     * @see ComponentVisitorInterface
     */
    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }
}
