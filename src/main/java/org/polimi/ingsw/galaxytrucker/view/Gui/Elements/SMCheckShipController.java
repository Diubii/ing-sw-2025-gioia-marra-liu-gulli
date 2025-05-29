package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.event.ActionEvent;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.network.client.Client;

public class SMCheckShipController {

    private ClientController clientController;

    public void initialize(ClientController clientController) {
        this.clientController = clientController;
    }

    public void checkShip(ActionEvent actionEvent) {
        //Todo Non va bene che siano hardcodate nel ClientController le scelte della Tui e la gui simuli una Tui praticametne
        clientController.handleCheckShipChoice("c");
    }

}
