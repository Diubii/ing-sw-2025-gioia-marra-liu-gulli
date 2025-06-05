package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.event.ActionEvent;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;

public class SMCheckShipController {

    private ClientController clientController;

    public void initialize(ClientController clientController) {
        this.clientController = clientController;
    }

    public void checkShip(ActionEvent actionEvent) {
        clientController.handleCheckShipChoice("c");
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
    }

}
