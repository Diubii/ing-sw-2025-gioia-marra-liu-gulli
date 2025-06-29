package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.scene.layout.HBox;

/**
 * Handles the UI logic for advancing the turn or landing early during the flight phase.
 */
public class SMavanzaTurnoController {

    private  ClientController clientController;
    private HBox container;
    private FlightController flightController;

    /**
     * Initializes the controller with the client controller and UI container.
     *
     * @param clientController reference to the client controller
     * @param container        the HBox container holding the buttons
     * @param flightController reference to the flight board controller
     */
    public void initialize(ClientController clientController, HBox container, FlightController flightController){
        this.clientController = clientController;
        this.container = container;
        this.flightController = flightController;
    }

    /**
     * Tells the client controller to signal the server that the player wants to land early
     */
    public void landEarly(){
      clientController.handleEarlyLandingRequest();
      GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
      container.getChildren().clear();
      flightController.setReadyForNextTurn();
    }

    /**
     * Tells the client controller to signal the server that the player is ready for the next turn
     */
    public void nextTurn(){
        clientController.handleReadyTurnRequest();
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        container.getChildren().clear();
        flightController.setReadyForNextTurn();
    }
}
