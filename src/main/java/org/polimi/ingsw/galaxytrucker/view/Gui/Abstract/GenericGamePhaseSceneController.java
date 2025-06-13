package org.polimi.ingsw.galaxytrucker.view.Gui.Abstract;

import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import org.polimi.ingsw.galaxytrucker.view.Gui.MusicManager;

import java.util.ArrayList;

/**
 * Abstract class for all the scenes that make up the GamePhases, essentially Building and Flight
 * that need more standardized method than a normal page.
 */
public abstract class GenericGamePhaseSceneController extends GenericSceneController {

    /**
     * Contains logic to choose where to show the passed ship and corresponding nickname
     * @param targetShipView
     * @param Nickname
     */
    public abstract void showShip(Ship targetShipView, String Nickname);

    public abstract void chooseTroncone(ArrayList<Ship> tronconi);

    public abstract void showWaitOtherPlayers();
}
