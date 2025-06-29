package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.scene.layout.HBox;

/**
 * Handles the activation or passing of an adventure card in the GUI.
 */

public class SMactivateCardController {

    private ClientController clientController;
    private HBox container;

    /**
     * Initializes the controller with the client controller and UI container.
     *
     * @param clientController reference to the client controller
     * @param container        the HBox container holding the buttons
     */
    public void initialize(ClientController clientController, HBox container){
        this.clientController = clientController;
        this.container = container;
    }

    /**
     * Tells the clientController to send to the server a message to activate the card
     */
    public void activate(){
        clientController.sendActivateAdventureCardResponse(true);
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        container.getChildren().clear();
    }

    /**
     * Tells the clientController to send to the server a message to pass the card to the next player
     */
    public void pass(){
        clientController.sendActivateAdventureCardResponse(false);
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        container.getChildren().clear();
    }


}
