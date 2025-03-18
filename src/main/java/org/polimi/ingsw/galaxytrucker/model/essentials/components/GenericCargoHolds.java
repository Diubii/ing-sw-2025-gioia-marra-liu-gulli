package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

import java.util.ArrayList;

public class GenericCargoHolds extends Component {

    private int nMaxContainers;
    private Boolean special;
    private ArrayList<Good> Goods;


    public GenericCargoHolds(String name, Boolean special, int nMaxContainers) {
        super(name, false);
        this.special = special;
        this.nMaxContainers = nMaxContainers;
        this.Goods = new ArrayList<>();

    }

    public int getnMaxContainers() {
        return nMaxContainers;
    }

    public ArrayList<Good> getGoods() {
        return new ArrayList<>(Goods);
    }


    public void loadGood(Good g) {

        if (Goods == null) Goods.add(g);
        else {
            for (int i = 0; i < Goods.size(); i++) {
                if (Goods.get(i).getColor().equals(g.getColor())) {
                    Goods.add(i + 1, new Good(g.getColor()));
                    break;
                } else if (i == Goods.size() - 1) Goods.add(new Good(g.getColor()));

            }
        }
    }

    public void removeGood(Good g) {
        if (Goods != null) {
            for (int i = 0; i < Goods.size(); i++) {
                if (Goods.get(i).getColor().equals(g.getColor())) {
                    Goods.remove(i);
                    break;
                }
            }
        }

    }


    @Override
    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this); // this ora è di tipo Cannon!
    }

    public Boolean isSpecial() {
        return special;
    }


}
