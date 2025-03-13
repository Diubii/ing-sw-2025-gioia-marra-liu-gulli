package org.polimi.ingsw.galaxytruckers.model.units.components;

import org.polimi.ingsw.galaxytruckers.model.units.Component;
import org.polimi.ingsw.galaxytruckers.model.units.Good;

import java.util.ArrayList;

public class GenericCargoHolds extends Component {

    private int nMaxContainers;
    private Boolean special;
    private ArrayList<Good> Goods;


    public GenericCargoHolds(String name, Boolean special, int nMaxContainers) {
        super(name);
        this.special = special;
        this.nMaxContainers = nMaxContainers;

    }

    public int getnMaxContainers() {
        return nMaxContainers;
    }

    public ArrayList<Good> getGoods() {
        return new ArrayList<>(Goods);
    }


    public void loadGood(Good g) {
        for (int i = 0; i < Goods.size(); i++) {
            if (Goods.get(i).getColor().equals(g.getColor())) {
                Goods.add(i + 1, new Good(g.getColor()));
                break;
            } else if (i == Goods.size() - 1) Goods.add(new Good(g.getColor()));

        }
    }

    public void removeGood(Good g) {
        for (int i = 0; i < Goods.size(); i++) {
            if (Goods.get(i).getColor().equals(g.getColor())) {
                Goods.remove(i);
                break;
            }
        }
    }

}
