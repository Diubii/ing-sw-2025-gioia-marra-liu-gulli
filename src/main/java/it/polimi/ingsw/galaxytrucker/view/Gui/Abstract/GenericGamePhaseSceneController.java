package it.polimi.ingsw.galaxytrucker.view.Gui.Abstract;

import it.polimi.ingsw.galaxytrucker.model.Ship;

import java.util.ArrayList;

/**
 * Abstract class for all the scenes that make up the GamePhases, such as the Building and Flight phases.
 * This class provides a standardized interface for handling common interactions and visualizations
 * that occur during these game phases.
 */
public abstract class GenericGamePhaseSceneController extends GenericSceneController {

    /**
     * Displays the specified ship on the GUI, associating it with the provided player's nickname.
     * This method determines the appropriate position and context to show the ship in the current game scene.
     *
     * @param targetShipView the ship to be displayed
     * @param Nickname the nickname of the player to whom the ship belongs
     */
    public abstract void showShip(Ship targetShipView, String Nickname);

    /**
     * Prompts the user to choose one of the available ship fragments ("tronconi")
     * when a decision is required (e.g., after damage or during recovery).
     *
     * @param tronconi a list of ship fragments from which the user must select
     */
    public abstract void chooseTroncone(ArrayList<Ship> tronconi);

    /**
     * Updates the GUI to show a waiting screen or status while other players finish their actions.
     * If {@code clearLast} is true, any previously shown wait messages or animations are cleared first.
     *
     * @param clearLast whether to clear previously displayed waiting messages
     */
    public abstract void showWaitOtherPlayers(Boolean clearLast);
}
