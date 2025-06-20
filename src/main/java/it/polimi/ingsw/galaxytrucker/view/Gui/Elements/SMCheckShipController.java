package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.event.ActionEvent;

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
