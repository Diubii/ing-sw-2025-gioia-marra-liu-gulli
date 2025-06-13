package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;

import java.io.IOException;

public class SMLoadCrewController {

    private ClientController clientController;
    private GuiJavaFx guiJavaFx;
    private StackPane container;

    public void initialize(ClientController clientController , GuiJavaFx guiJavaFx, StackPane container) {
        this.guiJavaFx = guiJavaFx;
        this.clientController = clientController;
        this.container=container;

    }


    //Solo pulsante che manda a controller della view principale
    public void confermaCrew(){
        guiJavaFx.confirmCrew();
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
