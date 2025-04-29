package org.polimi.ingsw.galaxytrucker.visitors;


import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;

public interface AdventureCardPrintVisitorInterface {

    public String[] visit(AbandonedShip card);

    public String[] visit(AbandonedStation card);

    public String[] visit(CombatZone card);

    public String[] visit(Epidemic card);

    public String[] visit(MeteorSwarm card);

    public String[] visit(OpenSpace card);

    public String[] visit(Pirates card);

    public String[] visit(Planets card);

    public String[] visit(Slavers card);

    public String[] visit(Smugglers card);

    public String[] visit(Stardust card);

}