package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DrawAdventureCardRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SMplanetChoiceController {

    private HBox container;
    private ClientController clientController;

    @FXML private VBox buttonsContainer;

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

    public void noStop(){
        //Non mi fermo sui pianeti.
        clientController.sendSelectPlanetResponse(null, -1);
        container.getChildren().clear();
    }
}
