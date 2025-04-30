
package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;

import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;

/**
 * Returns the Rows inside the Tile based on the present Component.
 * This Rows will then be composed in the TilePrintUtils.
 * 9 columns, 3 rows
 */
public class ComponentPrintVisitor implements ComponentVisitorInterface<String[]> {
    @Override
    public String[] visit(Component component) {
        String[] result = new String[]{
                "         ",
                "   Str   ",
                "         "
        };
        return AddRotationIndicator(result,component.getRotation());
    }

    @Override
    public String[] visit(BatterySlot component) {
        String[] result = new String[]{
                "         ",
                "   Bat   ",
                component.getBatteriesLeft()+"        "
        };
        return AddRotationIndicator(result,component.getRotation());
    }

    @Override
    public String[] visit(Cannon component) {
        String[] result = new String[]{
                "         ",
                "   Can   ",
                "         "
        };
        return AddRotationIndicator(result,component.getRotation());

    }

    @Override
    public String[] visit(CentralHousingUnit component) {
        StringBuilder sb;
        String[] result = new String[]{
                "         ",
                "   CCab  ",
                component.getHumanCrewNumber()+"        "
        };
        //Aggiunere lettera colore
        sb = new StringBuilder(result[1]);
        switch (component.getColor()){
            case RED ->sb.replace(0,8,BRIGHT_RED+"  CCab  "+RESET);
            case BLUE ->sb.replace(0,8,BRIGHT_BLUE+"  CCab  "+RESET);
            case GREEN ->sb.replace(0,8,BRIGHT_GREEN+"  CCab  "+RESET);
            case YELLOW ->sb.replace(0,8,BRIGHT_YELLOW+"  CCab  "+RESET);
            default ->sb.setCharAt(2, '-');
        }
        result[1] = sb.toString();
        return AddRotationIndicator(result,component.getRotation());
    }

    @Override
    public String[] visit(DoubleCannon component) {
        String[] result = new String[]{
                "         ",
                "  DCan   ",
                "         "
        };
        //Aggiungere se attivo
        result = AddActiveIndicator(result, component.isCharged());
        return AddRotationIndicator(result,component.getRotation());
    }

    @Override
    public String[] visit(DoubleEngine component) {
        String[] result = new String[]{
                "         ",
                "  DEng   ",
                "         "
        };
        result = AddActiveIndicator(result, component.getCharged());
        return AddEngineFire(result,component.getRotation());
    }

    @Override
    public String[] visit(Engine component) {
        String[] result = new String[]{
                "         ",
                "   Eng   ",
                "         "
        };
        return AddEngineFire(result,component.getRotation());
    }

    @Override
    public String[] visit(GenericCargoHolds component) {
        StringBuilder sb;
        String[] result = new String[]{
                "         ",
                "   Sto   ",
                "         "
        };
        sb = new StringBuilder(result[1]);
        if(component.isSpecial()){
            sb.setCharAt(2, 'S');
        }
        else {
            sb.setCharAt(2, 'N');
        }
        result[1] = sb.toString();
        sb = new StringBuilder();
        sb.append(component.getnMaxContainers()).append(" ");
        int j=0;
        for( j=0; j < component.getGoods().size(); j++){
            switch (component.getGoods().get(j).getColor()){
                case RED -> sb.append(RED).append("█").append(RESET);
                case BLUE -> sb.append(BLUE).append("█").append(RESET);
                case GREEN -> sb.append(GREEN).append("█").append(RESET);
                case YELLOW -> sb.append(BRIGHT_YELLOW).append("█").append(RESET);
            }

        }
        int spazi= 9-2-j;
        for(j=0; j < spazi; j++){
            sb.append(" ");
        }
        result[2] = sb.toString();
        return AddRotationIndicator(result,component.getRotation());
    }

    @Override
    public String[] visit(LifeSupportSystem component) {
        StringBuilder sb;
        String[] result = new String[]{
                "         ",
                "   Lss   ",
                "         "
        };
        sb = new StringBuilder(result[1]);
        if(component.getColor()== AlienColor.PURPLE){
            sb.setCharAt(2, 'P');
        }
        else{
            sb.setCharAt(2, 'B');
        }
        result[1] = sb.toString();
        return AddRotationIndicator(result,component.getRotation());
    }

    @Override
    public String[] visit(ModularHousingUnit component) {
        StringBuilder sb;
        String[] result = new String[]{
                "         ",
                "   Cab   ",
                component.getHumanCrewNumber()+"        "
        };
        //Aggiungere indicatore di alieno
        sb = new StringBuilder(result[2]);
        if(component.getNBrownAlien()==1){
            sb.setCharAt(8, 'B');
        }
        else if(component.getNPurpleAlien()==1){
            sb.setCharAt(8, 'P');
        }
        else{
            sb.setCharAt(8, '-');
        }
        result[2] = sb.toString();
        return AddRotationIndicator(result,component.getRotation());
    }

    @Override
    public String[] visit(Shield component) {
        String[] result = new String[]{
                "         ",
                "   Shl   ",
                "         "
        };
        result = AddActiveIndicator(result, component.isCharged());
        StringBuilder sb;
        switch (component.getRotation()){
            case 90:
                sb = new StringBuilder(result[1]);
                sb.replace(7,8, GREEN+"█"+RESET);
                result[1] = sb.toString();

                sb = new StringBuilder(result[2]);
                sb.replace(4,5, GREEN+"█"+RESET);
                result[2] = sb.toString();
                break;
            case 180:
                sb = new StringBuilder(result[2]);
                sb.replace(4,5, GREEN+"█"+RESET);
                result[2] = sb.toString();

                sb = new StringBuilder(result[1]);
                sb.replace(0,1, GREEN+"█"+RESET);
                result[1] = sb.toString();
                break;
            case 270:
                sb = new StringBuilder(result[1]);
                sb.replace(0,1, GREEN+"█"+RESET);
                result[1] = sb.toString();

                sb = new StringBuilder(result[0]);
                sb.replace(4,5, GREEN+"█"+RESET);
                result[0] = sb.toString();
                break;
            default:
                //Protetto Sopra
                sb = new StringBuilder(result[0]);
                sb.replace(4,5, GREEN+"█"+RESET);
                result[0] = sb.toString();
                //Protetto a DX
                sb = new StringBuilder(result[1]);
                sb.replace(7,8, GREEN+"█"+RESET);
                result[1] = sb.toString();
                break;

        }
        return result;
    }

    public String[] AddActiveIndicator(String[] res, boolean active) {
        StringBuilder sb= new StringBuilder(res[2]);
        if (active) {
            sb.setCharAt(0, 'O');
            sb.setCharAt(1, 'N');
        }
        else{
            sb.setCharAt(0, 'O');
            sb.setCharAt(1, 'F');
            sb.setCharAt(2, 'F');
        }
        res[2] = sb.toString();
        return res;
    }
    public String[] AddEngineFire(String[] res, int rot){
        StringBuilder sb;

        switch (rot){
            case 90:
                sb = new StringBuilder(res[1]);
                sb.replace(0,1 , ORANGE+"◀"+RESET);
                res[1] = sb.toString();
                break;
            case 180:
                sb = new StringBuilder(res[2]);
                sb.replace(4,5 , ORANGE+"▼"+RESET);
                res[0] = sb.toString();
                break;
            case 270:
                sb = new StringBuilder(res[1]);
                sb.replace(7,8 , ORANGE+"▶"+RESET);
                res[1] = sb.toString();
                break;
            default:
                sb = new StringBuilder(res[2]);
                sb.replace(4,5 , ORANGE+"▲"+RESET);
                res[2] = sb.toString();
                break;

        }
        return res;
    }

    public String[] AddRotationIndicator(String[] res, int rot){
        StringBuilder sb;

        switch (rot){
            case 90:
                sb = new StringBuilder(res[1]);
                sb.setCharAt(8, '*');
                res[1] = sb.toString();
                break;
            case 180:
                sb = new StringBuilder(res[2]);
                sb.setCharAt(4, '*');
                res[2] = sb.toString();
                break;
            case 270:
                sb = new StringBuilder(res[1]);
                sb.setCharAt(0, '*');
                res[1] = sb.toString();
                break;
            default:
                sb = new StringBuilder(res[0]);
                sb.setCharAt(4, '*');
                res[0] = sb.toString();
                break;

        }
        return res;
    }
}