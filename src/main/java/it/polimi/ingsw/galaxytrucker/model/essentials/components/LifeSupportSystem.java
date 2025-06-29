package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

/**
 * Represents a life support system that supports aliens of a specific color.
 */
public class LifeSupportSystem extends Component {

    private AlienColor color;

    @JsonCreator
    public LifeSupportSystem(@JsonProperty("color") AlienColor color) {
        super(false);
        this.color = color;
    }

    public AlienColor getColor() {
        return color;
    }

    @Override
    public LifeSupportSystem clone() {
        LifeSupportSystem copy = (LifeSupportSystem) super.clone();
        copy.color = this.color;
        return copy;
    }

    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

}
