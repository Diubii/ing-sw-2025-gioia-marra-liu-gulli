package org.polimi.ingsw.galaxytrucker.view.Gui.Abstract;

import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import org.polimi.ingsw.galaxytrucker.view.Gui.MusicManager;

public abstract class GenericSceneController {

    public abstract void initialSetup(GuiJavaFx mainViewController, ClientController clientController, ClientModel mymodel, Stage primaryStage, MusicManager musicManager);

    public abstract void ShowGenericMessage(String message);

}
