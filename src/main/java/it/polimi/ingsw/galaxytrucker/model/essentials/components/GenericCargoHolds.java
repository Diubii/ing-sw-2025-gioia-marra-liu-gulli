package it.polimi.ingsw.galaxytrucker.model.essentials.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentVisitorInterface;

import java.util.ArrayList;

/**
 * The GenericCargoHolds class represents a cargo hold component that can store goods of various colors.
 * It provides functionality for managing the loading, unloading, and tracking of goods.
 */
public class GenericCargoHolds extends Component {

    private int nMaxContainers;
    private Boolean special;
    private ArrayList<Good> Goods;

    /**
     * Constructs a new GenericCargoHolds instance with specified properties.
     *
     * @param special        Indicates if this cargo hold has special properties.
     * @param nMaxContainers Maximum number of goods containers it can hold.
     * @param Goods          Initial list of goods stored in the cargo hold.
     */
    @JsonCreator
    public GenericCargoHolds(@JsonProperty("special") Boolean special, @JsonProperty("nMaxContainers") int nMaxContainers, @JsonProperty("goods") ArrayList<Good> Goods) {
        super(false);
        this.special = special;
        this.nMaxContainers = nMaxContainers;
        this.Goods = new ArrayList<>(Goods);
    }

    /**
     * Constructs a new empty GenericCargoHolds instance.
     *
     * @param special        Indicates if this cargo hold has special properties.
     * @param nMaxContainers Maximum number of goods containers it can hold.
     */
    public GenericCargoHolds(Boolean special, int nMaxContainers) {
        super(false);
        this.special = special;
        this.nMaxContainers = nMaxContainers;
        this.Goods = new ArrayList<>();
    }

    /**
     * Gets the maximum capacity of containers this cargo hold can store.
     *
     * @return The maximum container count.
     */
    public int getnMaxContainers() {
        return nMaxContainers;
    }

    /**
     * Gets a copy of the goods currently stored in this cargo hold.
     *
     * @return An ArrayList containing all stored goods.
     */
    public ArrayList<Good> getGoods() {
        return new ArrayList<>(Goods);
    }

    /**
     * Checks if the cargo hold contains at least one good of the specified color.
     *
     * @param color The color to check for.
     * @return true if there's at least one good of the specified color, false otherwise.
     */
    public boolean hasGood(Color color) {
        return getGoods().stream().anyMatch(g -> g.getColor() == color);
    }

    /**
     * Adds a good to the cargo hold, placing it next to an existing good of the same color.
     *
     * @param g The good to add to the cargo hold.
     */
    public void playerLoadGood(Good g) {
        if (Goods == null) {
            Goods = new ArrayList<>();

        }
        if (Goods.size() >= nMaxContainers) {
            System.out.println("Good list contains more than " + nMaxContainers);
            return;
        }

        boolean inserted = false;
        for (int i = 0; i < Goods.size(); i++) {
            if (Goods.get(i).getColor().equals(g.getColor())) {
                Goods.add(i + 1, new Good(g.getColor()));
                inserted = true;
                break;
            }
        }

        if (!inserted) { Goods.add(new Good(g.getColor()));

        }
    }

    /**
     * Removes a specific good from the cargo hold.
     *
     * @param g The good to remove.
     */
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

    /**
     * Removes all goods of a specific color from the cargo hold.
     *
     * @param c The color of goods to remove.
     */
    public void removeGood(Color c) {
        if (c != null) {
            for (int i = 0; i < Goods.size(); i++) {
                if (Goods.get(i).getColor().equals(c)) {
                    Goods.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * Checks if the cargo hold has reached its maximum capacity.
     *
     * @return true if the cargo hold is full, false otherwise.
     */
    public boolean isFull() {
        return Goods.size() >= nMaxContainers;
    }

    /**
     * Checks if the cargo hold contains no goods.
     *
     * @return true if the cargo hold is empty, false otherwise.
     */
    public boolean isEmpty() {
        return Goods.isEmpty();
    }

    /**
     * Creates a deep copy of this GenericCargoHolds instance.
     *
     * @return A new GenericCargoHolds object with the same properties as this instance.
     */
    @Override
    public GenericCargoHolds clone() {
        GenericCargoHolds copy = (GenericCargoHolds) super.clone();
        copy.Goods = new ArrayList<>(Goods);
        return copy;
    }

    /**
     * Accepts a visitor to process this component.
     *
     * @param visitor The visitor to accept.
     * @return The result of the visit.
     */
    @Override
    public <T> T accept(ComponentVisitorInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Checks if this cargo hold has special properties.
     *
     * @return true if this cargo hold is special, false otherwise.
     */
    public Boolean isSpecial() {
        return special;
    }
}
