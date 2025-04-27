package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;

public class AdventureCardPrintVisitor implements AdventureCardPrintVisitorInterface{

    //18 *
    public String[] visit(AbandonedShip card){
        String[] result = new String[]{
                "┌----------------┐",
                "|    Abb. Ship   |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(AbandonedStation card){
        String[] result = new String[]{
                "┌----------------┐",
                "|  Abb. Station  |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(CombatZone card){
        String[] result = new String[]{
                "┌----------------┐",
                "|   Combat zone  |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(Epidemic card){
        String[] result = new String[]{
                "┌----------------┐",
                "|    Epidemic    |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(MeteorSwarm card){
        String[] result = new String[]{
                "┌----------------┐",
                "|     Meteors    |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(OpenSpace card){
        String[] result = new String[]{
                "┌----------------┐",
                "|   Open Space   |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(Pirates card){
        String[] result = new String[]{
                "┌----------------┐",
                "|     Pirates    |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(Planets card){
        String[] result = new String[]{
                "┌----------------┐",
                "|     Planets    |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(Slavers card){
        String[] result = new String[]{
                "┌----------------┐",
                "|    Slavers     |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(Smugglers card){
        String[] result = new String[]{
                "┌----------------┐",
                "|    Smugglers   |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

    public String[] visit(Stardust card){
        String[] result = new String[]{
                "┌----------------┐",
                "|    Stardust    |",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘",
                " └        ┘"
        };
        return result;
    }

}
