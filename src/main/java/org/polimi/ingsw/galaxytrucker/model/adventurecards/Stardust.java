package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;

import java.io.Serial;
import java.util.ArrayList;

public class Stardust extends AdventureCard {

    @Serial
    private static final long serialVersionUID = 76767545L;

    @JsonCreator
    public Stardust(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight )
    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = true;
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard, GameController gameController){
        aca.activateStardust(this, p, flightBoard, gameController);
    }

    @Override
    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> player, FlightBoard flightBoard) {

    }

    public String[] accept(AdventureCardPrintVisitorInterface visitor){
        return visitor.visit(this);
    }
}
