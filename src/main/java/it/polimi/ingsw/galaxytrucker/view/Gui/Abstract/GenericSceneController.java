package it.polimi.ingsw.galaxytrucker.view.Gui.Abstract;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import it.polimi.ingsw.galaxytrucker.view.Gui.MusicManager;
import javafx.stage.Stage;

/**
 * Abstract class for ALL the controllers of "scenes". Scenes intended as main roots od the current page, not real
 * scenes of JavaFx.
 */
public abstract class GenericSceneController {

    /**
     * Does all the procedures to setup the initial aspect of the page, from showing ships to preparing grids or other elements.
     *
     * @param mainViewController
     * @param clientController
     * @param mymodel
     * @param primaryStage
     * @param musicManager
     */
    public abstract void initialSetup(GuiJavaFx mainViewController, ClientController clientController, ClientModel mymodel, Stage primaryStage, MusicManager musicManager);

    /**
     * Shows somewhere in the page a generic message used for errors and comunications
     * @param message
     */
    public abstract void ShowGenericMessage(String message);

    /**
     * Returns the name of the page
     * @return
     */
    public abstract String pageName();


}
