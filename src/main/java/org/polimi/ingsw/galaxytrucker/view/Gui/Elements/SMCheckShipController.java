package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.view.Gui.BuildingController;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;

import java.io.IOException;

public class SMCheckShipController {

    private ClientController clientController;
    private StackPane container;

    public void initialize(ClientController clientController,StackPane container) {
        this.clientController = clientController;
        this.container = container;
    }

    public void checkShip(ActionEvent actionEvent) {
        clientController.handleCheckShipChoice("c");
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");

        container.getChildren().removeLast();
        VBox root = null;
        FXMLLoader loader;
        try {
            //1-Prima caricare FXML
            loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/WaitOverlay.fxml"));
            root = loader.load();

            root.setMaxWidth(Double.MAX_VALUE);
            root.setMaxHeight(Double.MAX_VALUE);

        } catch (IOException e) {
            e.printStackTrace();
        }
        container.getChildren().add(root);
    }

}
