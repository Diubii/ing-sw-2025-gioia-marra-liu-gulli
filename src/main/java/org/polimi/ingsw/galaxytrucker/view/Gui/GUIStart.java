package org.polimi.ingsw.galaxytrucker.view.Gui;


import javafx.application.Application;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.view.Gui.GUIStart;

public class GUIStart extends Application {

    @Override
    public void start(Stage primaryStage) {
        ClientController controller = new ClientController(null, true); // true = socket, false = RMI
        GuiJavaFx gui = new GuiJavaFx(primaryStage, controller);
        controller.setView(gui);

        primaryStage.setTitle("Galaxy Trucker - GUI");
        gui.askServerInfo(); // Prima scena da mostrare
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // NECESSARIO per avviare JavaFX
    }
}
