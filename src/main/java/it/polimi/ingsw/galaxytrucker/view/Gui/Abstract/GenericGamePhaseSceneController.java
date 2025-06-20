package it.polimi.ingsw.galaxytrucker.view.Gui.Abstract;

import it.polimi.ingsw.galaxytrucker.model.Ship;

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

    public abstract void showWaitOtherPlayers(Boolean clearLast);
}
