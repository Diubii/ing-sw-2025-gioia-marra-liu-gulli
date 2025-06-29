package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Gui.BuildingController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.scene.layout.StackPane;


/**
 * Handles crew confirmation during the building phase.
 */
public class SMLoadCrewController {

    private ClientController clientController;
    private GuiJavaFx guiJavaFx;
    private StackPane container;
    private BuildingController buildingController;

    /**
     * Initializes the controller with required references.
     *
     * @param clientController     the client-side controller
     * @param guiJavaFx            reference to the main GUI handler
     * @param container            the UI container
     * @param buildingController   reference to the building phase controller
     */
    public void initialize(ClientController clientController , GuiJavaFx guiJavaFx, StackPane container, BuildingController buildingController) {
        this.guiJavaFx = guiJavaFx;
        this.clientController = clientController;
        this.container=container;
        this.buildingController = buildingController;

    }


    /**
     * Triggers the GuiJavaFx to prepare the crewInit update
     * and ask the client controller to send it to the server.
     */
    public void confermaCrew(){
        guiJavaFx.confirmCrew();
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        buildingController.showWaitOtherPlayers(true);
    }

}
