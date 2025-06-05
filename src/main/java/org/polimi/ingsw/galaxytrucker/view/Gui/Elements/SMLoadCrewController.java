package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;

public class SMLoadCrewController {

    private ClientController clientController;
    private GuiJavaFx guiJavaFx;

    public void initialize(ClientController clientController , GuiJavaFx guiJavaFx) {
        this.guiJavaFx = guiJavaFx;
        this.clientController = clientController;
    }


    //Solo pulsante che manda a controller della view principale
    public void confermaCrew(){
        guiJavaFx.confirmCrew();
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
    }

}
