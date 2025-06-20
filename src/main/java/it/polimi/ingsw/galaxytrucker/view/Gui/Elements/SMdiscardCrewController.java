package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class SMdiscardCrewController {

    private int total;
    private int selNumber;
    private ClientController clientController;
    private FlightController flightController;
    private HBox container;
    ArrayList<Position> housingPositions;

    @FXML private Label lblConteggio;


    public void initialize(ClientController clientController, FlightController flightController, HBox container, int total) {
        this.clientController = clientController;
        this.container = container;
        this.total = total;
        this.flightController = flightController;
        housingPositions = new ArrayList<>();
        lblConteggio.setText("0/"+Integer.toString(total));
    }

    public void add(Position position) {
        //se uguali termina e manda update
        housingPositions.add(position);
        System.out.println("StampoLista");
        for(Position p : housingPositions){
            System.out.println("Posizione: "+p.getX()+" "+p.getY());
        }
        selNumber++;
        lblConteggio.setText(Integer.toString(selNumber)+"/"+Integer.toString(total));
        if(housingPositions.size() == total){
            //flightcontroller finisce fase e manda l'update qui
            flightController.endDiscardCrew();
            clientController.handleDiscardCrewMembersResponse(housingPositions);
            container.getChildren().clear();
        }
    }
}
