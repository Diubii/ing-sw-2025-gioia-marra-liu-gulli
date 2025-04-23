package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitorInterface;

import java.util.ArrayList;

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
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

    @Override
    public String[] accept(ComponentPrintVisitorInterface visitor) {
        return visitor.visit(this);
    }

}
