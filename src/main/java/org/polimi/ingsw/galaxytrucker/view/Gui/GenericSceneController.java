package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;

public abstract class GenericSceneController {



    public abstract void initialSetup(GuiJavaFx mainViewController, ClientController clientController, Stage primaryStage, MusicManager musicManager);

    public abstract void ShowGenericMessage(String message);

}
