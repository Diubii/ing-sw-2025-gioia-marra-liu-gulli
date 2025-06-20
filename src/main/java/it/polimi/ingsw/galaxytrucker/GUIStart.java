package it.polimi.ingsw.galaxytrucker;


import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class GUIStart extends Application {

    Scene primaryScene;
    Parent root;

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader loader;
        try{
            URL url = getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Loading.fxml");
            loader = new FXMLLoader(url);
            root = loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);


        GuiJavaFx gui = new GuiJavaFx(primaryStage,primaryScene);


        primaryStage.setTitle("Galaxy Trucker - GUI");
        Image icon = new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/Seduto.png").toString());
        primaryStage.getIcons().add(icon);
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(900);

        gui.askServerInfo(); // Prima scena da mostrare
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args); // NECESSARIO per avviare JavaFX
    }
}
