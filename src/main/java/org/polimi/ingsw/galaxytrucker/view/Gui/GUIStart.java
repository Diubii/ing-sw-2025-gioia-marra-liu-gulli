package org.polimi.ingsw.galaxytrucker.view.Gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class GUIStart extends Application {

    Scene primaryScene;
    Parent root;

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader loader;
        try{
            loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Loading.fxml"));
            root = loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);

        ClientController controller = new ClientController(null, true); // true = socket, false = RMI
        GuiJavaFx gui = new GuiJavaFx(primaryStage, controller,primaryScene);
        controller.setView(gui);


        primaryStage.setTitle("Galaxy Trucker - GUI");
        Image icon = new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/Seduto.png").toString());
        primaryStage.getIcons().add(icon);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(500);
        //primaryStage.resizableProperty().setValue(Boolean.FALSE);

        gui.askServerInfo(); // Prima scena da mostrare
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // NECESSARIO per avviare JavaFX
    }
}
