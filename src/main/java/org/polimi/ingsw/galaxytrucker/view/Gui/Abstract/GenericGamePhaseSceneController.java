package org.polimi.ingsw.galaxytrucker.view.Gui.Abstract;

import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import org.polimi.ingsw.galaxytrucker.view.Gui.MusicManager;

public abstract class GenericGamePhaseSceneController extends GenericSceneController {

    public abstract void showShip(Ship targetShipView, String Nickname);

}
