package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.model.Planet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;


/**
 * Handles planet choice confirmation during the flight phase.
 */
public class SMplanetChoiceController {

    private HBox container;
    private ClientController clientController;

    @FXML private VBox buttonsContainer;

    /**
     * Initializes the controller with required references.
     *
     * @param clientController     the client-side controller
     * @param container            the UI container
     * @param planetChoices        the available planets' choices
     */
    public void initialize(ClientController clientController, HBox container, HashMap<Integer, Planet> planetChoices){
        this.container = container;
        this.clientController = clientController;

        for (Integer i : planetChoices.keySet()) {
            Planet planet = planetChoices.get(i);
            Button planetChoiceButton = new Button("Pianeta " + (i));
            planetChoiceButton.getStyleClass().add("transparent-button");
            // Azione al click: invia il pianeta selezionato
            int finalIndex = i;
            planetChoiceButton.setOnMouseClicked(event -> {
                clientController.sendSelectPlanetResponse(planet, finalIndex);
                container.getChildren().clear();
            });

            buttonsContainer.getChildren().add(planetChoiceButton);
        }
    }

    /**
     * Asks the client controller to send the choice of not stopping to the server
     */
    public void noStop(){
        //Non mi fermo sui pianeti.
        clientController.sendSelectPlanetResponse(null, -1);
        container.getChildren().clear();
    }
}
