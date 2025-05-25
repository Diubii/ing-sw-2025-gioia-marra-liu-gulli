package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

import java.util.ArrayList;

public class GenericCargoHolds extends Component {

    private int nMaxContainers;
    private Boolean special;
    private ArrayList<Good> Goods;

    @JsonCreator
    public GenericCargoHolds(@JsonProperty("special") Boolean special, @JsonProperty("nMaxContainers") int nMaxContainers, @JsonProperty("goods") ArrayList<Good> Goods) {
        super(false);
        this.special = special;
        this.nMaxContainers = nMaxContainers;
        this.Goods = new ArrayList<>(Goods);

    }

    public GenericCargoHolds(Boolean special, int nMaxContainers) {
        super(false);
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

    public boolean hasGood(Color color) {
        return getGoods().stream().anyMatch(g -> g.getColor() == color);
    }

    public void loadGood(Good g) {

        if (Goods == null) {
            Goods = new ArrayList<>();
            Goods.add(g);
        }

        else {
            for (int i = 0; i < Goods.size(); i++) {
                if (Goods.get(i).getColor().equals(g.getColor())) {
                    Goods.add(i + 1, new Good(g.getColor()));
                    break;
                } else if (i == Goods.size() - 1) Goods.add(new Good(g.getColor()));

            }
        }
    }

    public void playerLoadGood(Good g) {
        if (Goods == null) {
            Goods = new ArrayList<>();
        }
        if (Goods.size() < nMaxContainers)
            Goods.add(g);


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

    public void removeGood(Color c){
        if (c != null) {
            for (int i = 0; i < Goods.size(); i++) {
                if (Goods.get(i).getColor().equals(c)) {
                    Goods.remove(i);
                    break;
                }
            }
        }
    }


    public boolean isFull() {
        return Goods.size() >= nMaxContainers;
    }

    public boolean isEmpty() {
        return Goods.isEmpty();
    }

    @Override
    public GenericCargoHolds clone() {
        GenericCargoHolds copy = (GenericCargoHolds) super.clone();
        copy.Goods = new ArrayList<>(Goods);
        return copy;
    }

    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

    public Boolean isSpecial() {

        return special;
    }


}
