package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Cannon;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TilePrintUtils;

import java.util.ArrayList;


class TileTest {

    private Component component;
    private Tile tile;
    private Slot slot;

    @BeforeEach
    void setUp() {
        component = new Component(false);
        ArrayList<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.SINGLE);
        connectors.add(Connector.DOUBLE);
        connectors.add(Connector.EMPTY);
        tile = new Tile(1, 0, connectors, component);
        slot = new Slot(new Position(0, 0));
        TilePrintUtils.printTile(tile);

    }


    @Test
    void setMyComponent() {

        Cannon cannon = new Cannon(1F);
        tile.setMyComponent(cannon);

        assertEquals(cannon, tile.getMyComponent());
    }


    @Test
    void rotate() {
        tile.rotate(360);
        assertEquals(0, tile.getRotation());
        assertEquals(0, tile.getMyComponent().getRotation());
    }

}