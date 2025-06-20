package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.scene.layout.HBox;

public class SMactivateCardController {

    private ClientController clientController;
    private HBox container;

    public void initialize(ClientController clientController, HBox container){
        this.clientController = clientController;
        this.container = container;
    }

    public void activate(){
        clientController.sendActivateAdventureCardResponse(true);
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        container.getChildren().clear();
    }

    public void pass(){
        clientController.sendActivateAdventureCardResponse(false);
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        container.getChildren().clear();
    }


}
