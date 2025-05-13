package org.polimi.ingsw.galaxytrucker.view.Gui;


import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class GUIStart extends Application {

    @Override
    public void start(Stage primaryStage) {
        ClientController controller = new ClientController(null, true); // true = socket, false = RMI
        GuiJavaFx gui = new GuiJavaFx(primaryStage, controller);
        controller.setView(gui);

        primaryStage.setTitle("Galaxy Trucker - GUI");
        Image icon = new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/Seduto.png").toString());
        primaryStage.getIcons().add(icon);
        //primaryStage.resizableProperty().setValue(Boolean.FALSE);

        gui.askServerInfo(); // Prima scena da mostrare
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // NECESSARIO per avviare JavaFX
    }
}
