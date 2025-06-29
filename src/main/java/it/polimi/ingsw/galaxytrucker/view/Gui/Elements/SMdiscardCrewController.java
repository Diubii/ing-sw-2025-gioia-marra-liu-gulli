package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ArrayList;


/**
 * Handles GUI logic for discarding crew members during flight phase events.
 */
public class SMdiscardCrewController {

    private int total;
    private int selNumber;
    private ClientController clientController;
    private FlightController flightController;
    private HBox container;
    ArrayList<Position> housingPositions;

    @FXML private Label lblConteggio;


    /**
     * Initializes the controller with context and required discard count.
     *
     * @param clientController  client-side controller
     * @param flightController  flight phase controller
     * @param container         UI container to clear when done
     * @param total             number of crew to discard
     */
    public void initialize(ClientController clientController, FlightController flightController, HBox container, int total) {
        this.clientController = clientController;
        this.container = container;
        this.total = total;
        this.flightController = flightController;
        housingPositions = new ArrayList<>();
        lblConteggio.setText("0/"+Integer.toString(total));
    }

    /**
     * Adds the position of the ship where the user chose to discard a crew memeber to a collection,
     * if the required amount of discarded crew is reached sends the server a message with the positions
     * of the discarded crew.
     * @param position
     */
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
