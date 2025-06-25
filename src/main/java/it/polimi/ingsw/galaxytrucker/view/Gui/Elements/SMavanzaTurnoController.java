package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.scene.layout.HBox;

public class SMavanzaTurnoController {

    private  ClientController clientController;
    private HBox container;
    private FlightController flightController;

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
