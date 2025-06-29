package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.event.ActionEvent;

/**
 * Handles the ship check request during the building phase.
 */
public class SMCheckShipController {

    private ClientController clientController;


    /**
     * Initializes the controller with the client controller.
     *
     * @param clientController reference to the client-side controller
     */
    public void initialize(ClientController clientController) {
        this.clientController = clientController;

    }

    /**
     * Tells the client controller to send the server a request to Check the ship,
     * the server will check the current ship in the remote model and respond.
     * @param actionEvent
     */
    public void checkShip(ActionEvent actionEvent) {
        clientController.handleCheckShipChoice("c");
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");

    }

}
