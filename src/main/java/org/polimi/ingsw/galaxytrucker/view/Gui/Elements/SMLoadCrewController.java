package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.view.Gui.BuildingController;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;

import java.io.IOException;

public class SMLoadCrewController {

    private ClientController clientController;
    private GuiJavaFx guiJavaFx;
    private StackPane container;
    private BuildingController buildingController;

    public void initialize(ClientController clientController , GuiJavaFx guiJavaFx, StackPane container, BuildingController buildingController) {
        this.guiJavaFx = guiJavaFx;
        this.clientController = clientController;
        this.container=container;
        this.buildingController = buildingController;

    }


    //Solo pulsante che manda a controller della view principale
    public void confermaCrew(){
        guiJavaFx.confirmCrew();
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        buildingController.showWaitOtherPlayers(true);
    }

}
