package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import org.polimi.ingsw.galaxytrucker.view.Gui.zUtils;

import java.io.IOException;
import java.util.ArrayList;

public class SMtronconiController {

    ClientController clientController;
    StackPane container;
    ToggleGroup shipToggleGroup;

    @FXML VBox shipList;

    public void initialize(ClientController clientController, ArrayList<Ship> tronconi, StackPane container) {
        this.container = container;
        this.clientController = clientController;

        //For each ship insert a Hbox with radioButton and "ship view"
        shipToggleGroup=new ToggleGroup();
        int i = 1;
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleShip.fxml"));
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
        }
    }

    public void confirmChoice(){
        if(shipToggleGroup.getSelectedToggle() != null){
            container.getChildren().removeLast();
            int id = (int)shipToggleGroup.getSelectedToggle().getUserData();
            id--;
            clientController.handleTrunkResponse(id);
        }

    }
}
