package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.view.Gui.zUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Displays a list of ship fragments ("tronconi") for the player to choose from after damage.
 */
public class SMtronconiController {

    ClientController clientController;
    StackPane container;
    ToggleGroup shipToggleGroup;

    @FXML VBox shipList;

    /**
     * Initializes the view with ship fragments to choose from.
     *
     * @param clientController the client controller
     * @param tronconi         list of ship fragments
     * @param container        the UI container for cleanup
     */
    public void initialize(ClientController clientController, ArrayList<Ship> tronconi, StackPane container) {
        this.container = container;
        this.clientController = clientController;

        //For each ship insert a Hbox with radioButton and "ship view"
        shipToggleGroup=new ToggleGroup();
        int i = 0; //0 based
        for (Ship ship : tronconi) {
            HBox shipBox = new HBox();

            RadioButton radio = new RadioButton();
            radio.setToggleGroup(shipToggleGroup);
            radio.setUserData(i);
            shipBox.getChildren().add(radio);
            StackPane shipContainer = new StackPane();
            shipBox.getChildren().add(shipContainer);

            shipBox.setMinHeight(400.00);
            shipBox.setAlignment(Pos.CENTER);
            shipBox.setSpacing(20.00);
            shipContainer.setMinHeight(400.00);
            shipContainer.setAlignment(Pos.CENTER);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleShip.fxml"));
            Parent shipNode = null;
            try {
                shipNode = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SingleShipController shipController = loader.getController();
            shipContainer.getChildren().add(shipNode);

            shipList.getChildren().add(shipBox);
            shipController.initialize(clientController.getMyModel().getMyInfo().getNickName(),shipContainer);
            zUtils.showShipInGrid(ship, shipController.getShipGrid(), clientController,false,true,null,null);
            i++;
        }
    }

    /**
     * Sends the server the id of the chosen ship
     */
    public void confirmChoice(){
        if(shipToggleGroup.getSelectedToggle() != null){
            container.getChildren().removeLast();
            int id = (int)shipToggleGroup.getSelectedToggle().getUserData();

            clientController.handleTrunkResponse(id);
        }

    }
}
