package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.view.Gui.FlightController;

import java.util.ArrayList;

public class SMactivateComponentController {

    @FXML private Label lblSpiegazione;

    private ClientController clientController;
    private FlightController flightController;
    private HBox container;
    private ActivatableComponent activatableComponent;
    private ArrayList<Position> componentPositions;
    private ArrayList<Position> batteryPositions;

    public void initialize(ActivatableComponent activatableComponent , HBox continer , ClientController clientController , FlightController flightController) {
        //Il tipo di component per poi creare la lista per fare poi l'update.
        this.container = continer;
        this.clientController = clientController;
        this.activatableComponent = activatableComponent;
        this.flightController = flightController;

        lblSpiegazione.setText("Puoi attivate i componenti di tipo: "+activatableComponent.name());
        componentPositions = new ArrayList<>();
        batteryPositions = new ArrayList<>();
    }

    public void addComponentPosition(Position position){
        componentPositions.add(position);
    }


    public void addBatteryPosition(Position position){
        batteryPositions.add(position);
    }

    public void sendPositions(){
        clientController.handleActivateComponentResponse(activatableComponent, componentPositions, batteryPositions);
        flightController.resetInHandBattery();
        container.getChildren().clear();
    }

}
