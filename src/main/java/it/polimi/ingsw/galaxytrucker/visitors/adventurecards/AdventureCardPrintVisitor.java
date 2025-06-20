package it.polimi.ingsw.galaxytrucker.visitors.adventurecards;

import it.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.*;

import java.util.ArrayList;

import static it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;


public class AdventureCardPrintVisitor implements AdventureCardVisitorsInterface<String[]> {

    //18 *
    @Override
    public String[] visit(AbandonedShip card) {
        String[] result = new String[]{
                "┌----------------┐",
                "|   Abb. Ship    |",
                "|                |",
                "|  -" + card.getRequiredCrewMembers() + " Crew       |",
                "|                |",
                "|  +" + card.getCredits() + " Credit     |",
                "|                |",
                "|                |",
                "└----------------┘"
        };
        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    @Override
    public String[] visit(AbandonedStation card) {
        StringBuilder sb;
        String[] result = new String[]{
                "┌----------------┐",
                "|  Abb. Station  |",
                "|                |",
                "|  Crew: " + card.getRequiredCrewMembers() + "       |",
                "|                |",
                "|  Merci:              |",
                "|                |",
                "|                |",
                "└----------------┘"
        };

        sb = new StringBuilder();
        sb.append("| Merci: ");
        int j = 0;
        for (j = 0; j < card.getGoods().size(); j++) {
            switch (card.getGoods().get(j).getColor()) {
                case RED -> sb.append(RED).append("█").append(RESET);
                case BLUE -> sb.append(BLUE).append("█").append(RESET);
                case GREEN -> sb.append(GREEN).append("█").append(RESET);
                case YELLOW -> sb.append(BRIGHT_YELLOW).append("█").append(RESET);
                case EMPTY -> sb.append(" ");
            }

        }
        int spazi = 18 - 10 - j;
        for (j = 0; j < spazi; j++) {
            sb.append(" ");
        }
        sb.append("|");
        result[5] = sb.toString();
        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    //↑ ↓ → ←
    @Override
    public String[] visit(CombatZone card) {
        //HardCodate perchè complesse distinguo con livello:
        String[] result;
        if (card.getLevel() == 1) {
            result = new String[]{
                    "┌----------------┐",
                    "|  Combat zone  1|",
                    "|                |",
                    "| ↓Crew  -3gg    |",
                    "| ↓EngP  -2Crew  |",
                    "| ↓FirP   • ↑    |",
                    "|         ● ↑    |",
                    "| L              |",
                    "└----------------┘"
            };
        } else {
            result = new String[]{
                    "┌----------------┐",
                    "|  Combat zone  2|",
                    "|                |",
                    "| ↓FirP  -4gg    |",
                    "| ↓EngP  -3Merci |",
                    "| ↓Crew  • ↓ → ← |",
                    "|        ● ↑     |",
                    "|                |",
                    "└----------------┘"
            };
        }

        return result;
    }

    @Override
    public String[] visit(Epidemic card) {
        String[] result = new String[]{
                "┌----------------┐",
                "|    Epidemic    |",
                "|     ╔═════╗    |",
                "|     ║ o X ║    |",
                "|     ╚╦═╦═╦╝    |",
                "|     ╔╩═══╩╗    |",
                "|     ║ o X ║    |",
                "|     ╚═════╝    |",
                "└----------------┘"
        };
        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    @Override
    public String[] visit(MeteorSwarm card) {
        StringBuilder sb;
        StringBuilder sb2;
        String[] result = new String[]{
                "┌----------------┐",
                "|    Meteors     |",
                "|                |",
                "|                |",
                "|                |",
                "|                |",
                "|                |",
                "|                |",
                "└----------------┘"
        };
        int Line = 2, j;
        boolean DoubleLinePrec = false;
        ProjectileDirection PrecKind = null;
        int occupied = 0;
        sb = new StringBuilder();
        sb2 = new StringBuilder();

        for (int i = 0; i < card.getMeteors().size(); i++) {

            if (card.getMeteors().get(i).getDirection() == ProjectileDirection.UP
                    || card.getMeteors().get(i).getDirection() == ProjectileDirection.DOWN) {
                if (i == 0) {
                    sb.append("|      ");
                    sb2.append("|      ");
                    occupied = 8;
                }
                //Se è cambiato tipo nuova linea
                //Se era doppia avanti di 2
                if (PrecKind != null && PrecKind != card.getMeteors().get(i).getDirection()) {
                    int spazi = 18 - occupied;
                    for (j = 0; j < spazi; j++) {
                        sb.append(" ");
                    }
                    sb.append("|");
                    if (DoubleLinePrec) {
                        for (j = 0; j < spazi; j++) {
                            sb2.append(" ");
                        }
                        sb2.append("|");
                        result[Line] = sb.toString();
                        result[Line + 1] = sb2.toString();
                        Line = Line + 2;
                    } else {
                        result[Line] = sb.toString();
                        Line++;
                    }


                    sb = new StringBuilder();
                    sb2 = new StringBuilder();
                    sb.append("|      ");
                    sb2.append("|      ");
                    occupied = 8;
                }
                occupied = occupied + 2;

                //è una linea doppia pk Davanti o Dietro
                DoubleLinePrec = true;


                if (card.getMeteors().get(i).getDirection() == ProjectileDirection.UP) {
                    sb.append("↓ ");
                    if (card.getMeteors().get(i).getSize() == ProjectileSize.Big) {
                        sb2.append("● ");
                    } else {
                        sb2.append("• ");
                    }

                } else {
                    sb2.append("↑ ");
                    if (card.getMeteors().get(i).getSize() == ProjectileSize.Big) {
                        sb.append("● ");
                    } else {
                        sb.append("• ");
                    }
                }

                //Tipo Prec
                PrecKind = card.getMeteors().get(i).getDirection();

            } else {
                if (i == 0) {
                    if (card.getMeteors().get(i).getDirection() == ProjectileDirection.LEFT) {
                        sb.append("| ");
                        occupied = 2;
                    } else {
                        sb.append("|           ");
                        occupied = 12;
                    }
                }
                //Sempre nuova linea per Left e Right
                //Se era doppia avanti di 2
                if (PrecKind != null) {
                    //Impostare spazi extra
                    int spazi = 18 - occupied;
                    for (j = 0; j < spazi; j++) {
                        sb.append(" ");
                    }
                    sb.append("|");
                    if (DoubleLinePrec) {
                        for (j = 0; j < spazi; j++) {
                            sb2.append(" ");
                        }
                        sb2.append("|");
                        result[Line] = sb.toString();
                        result[Line + 1] = sb2.toString();
                        Line = Line + 2;
                    } else {
                        result[Line] = sb.toString();
                        Line++;
                    }
                    //Linea Singola
                    sb = new StringBuilder();
                    if (card.getMeteors().get(i).getDirection() == ProjectileDirection.LEFT) {
                        sb.append("| ");
                        occupied = 2;
                    } else {
                        sb.append("|           ");
                        occupied = 12;
                    }

                }
                occupied = occupied + 4;
                // è una linea singola
                DoubleLinePrec = false;

                if (card.getMeteors().get(i).getDirection() == ProjectileDirection.LEFT) {
                    sb.append("→ ");
                    if (card.getMeteors().get(i).getSize() == ProjectileSize.Big) {
                        sb.append("●");
                    } else {
                        sb.append("•");
                    }
                } else {
                    if (card.getMeteors().get(i).getSize() == ProjectileSize.Big) {
                        sb.append("●");
                    } else {
                        sb.append("•");
                    }
                    sb.append(" ←");
                }
            }
        }

        int spazi = 18 - occupied;
        for (j = 0; j < spazi; j++) {
            sb.append(" ");
        }
        sb.append("|");
        if (DoubleLinePrec) {
            for (j = 0; j < spazi; j++) {
                sb2.append(" ");
            }
            sb2.append("|");
            result[Line] = sb.toString();
            result[Line + 1] = sb2.toString();
        } else {
            result[Line] = sb.toString();
        }

        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    @Override
    public String[] visit(OpenSpace card) {
        String[] result = new String[]{
                "┌----------------┐",
                "|   Open Space   |",
                "|                |",
                "|  1EngP = +1gg  |",
                "|      ╔╦╦╗      |",
                "|     ╔║║║║╗     |",
                "|     ██████     |",
                "|      ░░░░      |",
                "└----------------┘"
        };
        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    @Override
    public String[] visit(Pirates card) {
        //HardCodate perchè complesse distinguo con livello:
        String[] result;
        if (card.getLevel() == 1) {
            result = new String[]{
                    "┌----------------┐",
                    "|     Pirati    1|",
                    "|                |",
                    "| FireP:     5   |",
                    "| L:     ↓ ↓ ↓   |",
                    "|        • ● •   |",
                    "| W:   +4 credit |",
                    "| L      Dlost:1 |",
                    "└----------------┘"
            };
        } else {
            result = new String[]{
                    "┌----------------┐",
                    "|     Pirati    2|",
                    "|                |",
                    "| FireP:     6   |",
                    "| L:     ↓ ↓ ↓   |",
                    "|        ● • ●   |",
                    "| W:   +7 credit |",
                    "|        Dlost:2 |",
                    "└----------------┘"
            };
        }

        return result;
    }

    //Faccio planet e vedo quanto grossa serve
    @Override
    public String[] visit(Planets card) {
        StringBuilder sb;
        String[] result = new String[]{
                "┌----------------┐",
                "|    Planets     |",
                "|                |",
                "|                |",
                "|                |",
                "|                |",
                "|                |",
                "|                |",
                "└----------------┘"
        };
        ArrayList<Planet> planets = new ArrayList<>();
        planets = card.getPlanets();

        for (int i = 0; i < planets.size(); i++) {
            sb = new StringBuilder();
            sb.append("|P").append(i + 1).append(": ");
            int j = 0;
            for (j = 0; j < planets.get(i).getGoods().size(); j++) {
                switch (planets.get(i).getGoods().get(j).getColor()) {
                    case RED -> sb.append(RED).append("█").append(RESET);
                    case BLUE -> sb.append(BLUE).append("█").append(RESET);
                    case GREEN -> sb.append(GREEN).append("█").append(RESET);
                    case YELLOW -> sb.append(BRIGHT_YELLOW).append("█").append(RESET);
                    case EMPTY -> sb.append(" ");
                }

            }
            int spazi = 18 - 6 - j;
            for (j = 0; j < spazi; j++) {
                sb.append(" ");
            }
            sb.append("|");
            result[i + 3] = sb.toString();
        }
        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    @Override
    public String[] visit(Slavers card) {

        String[] result = new String[]{
                "┌----------------┐",
                "|    Slavers     |",
                "|                |",
                "| FireP:    " + card.getFirePower() + "    |",
                "| L: -" + card.getPenalty() + " Crew     |",
                "| W: +" + card.getCredits() + " Credit   |",
                "|                |",
                "|                |",
                "└----------------┘"
        };

        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    @Override
    public String[] visit(Smugglers card) {
        StringBuilder sb;
        String[] result = new String[]{
                "┌----------------┐",
                "|   Smugglers    |",
                "|                |",
                "| FireP:  " + card.getFirePower() + "      |",
                "| L: -" + card.getPenalty() + " Merci    |",
                "| W:              |",
                "|                |",
                "|                |",
                "└----------------┘"
        };

        sb = new StringBuilder();
        sb.append("| W: ");
        int j = 0;
        for (j = 0; j < card.getGoods().size(); j++) {
            switch (card.getGoods().get(j).getColor()) {
                case RED -> sb.append(RED).append("█").append(RESET);
                case BLUE -> sb.append(BLUE).append("█").append(RESET);
                case GREEN -> sb.append(GREEN).append("█").append(RESET);
                case YELLOW -> sb.append(BRIGHT_YELLOW).append("█").append(RESET);
                case EMPTY -> sb.append(" ");
            }

        }
        int spazi = 18 - 6 - j;
        for (j = 0; j < spazi; j++) {
            sb.append(" ");
        }
        sb.append("|");
        result[5] = sb.toString();
        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    @Override
    public String[] visit(Stardust card) {
        String[] result = new String[]{
                "┌----------------┐",
                "|    Stardust    |",
                "|                |",
                "| -1gg x expConn |",
                "|     ╔═╦══╗     |",
                "|     ╠╗╚═╗╠═ -1 |",
                "|     ╠╩══╩╣     |",
                "| -1 ═╩════╩═ -1 |",
                "└----------------┘"
        };
        return AddLvLearningDaysLost(result, card.getLevel(), card.isLearningFlight(), card.getDaysLost());
    }

    private String[] AddLvLearningDaysLost(String[] res, int lv, boolean learning, int daysLost) {
        StringBuilder sb;
        //Learning and DaysLost
        sb = new StringBuilder(res[7]);
        if (learning) {
            sb.replace(2, 3, "L");
        }
        if (daysLost != 0) {
            sb.replace(9, 16, "Dlost:" + daysLost);
        }
        res[7] = sb.toString();
        //Level
        sb = new StringBuilder(res[1]);
        sb.replace(16, 17, lv + "");
        res[1] = sb.toString();
        return res;
    }
}