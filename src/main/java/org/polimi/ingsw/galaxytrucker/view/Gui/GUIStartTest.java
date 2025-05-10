package org.polimi.ingsw.galaxytrucker.view.Gui;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class GUIStartTest extends Application {

//    @Override
//    public void start(Stage primaryStage) {
//        ClientController controller = new ClientController(null, true); // true = socket, false = RMI
//        GuiJavaFx gui = new GuiJavaFx(primaryStage, controller);
//        controller.setView(gui);
//
//        primaryStage.setTitle("Galaxy Trucker - GUI");
//        gui.askServerInfo(); // Prima scena da mostrare
//        primaryStage.show();
//    }

    public static void main(String[] args) {

        launch(args); // NECESSARIO per avviare JavaFX static method
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 600,600,Color.BLUE);
        Text text = new Text("Hello World");
        text.setX(100);
        text.setY(100);
        text.setFont(Font.font("Arial", 20));
        text.setFill(Color.LIME);

        Line line = new Line();
        line.setStartX(200);
        line.setStartY(200);
        line.setEndX(500);
        line.setEndY(200);
        line.setStrokeWidth(5);
        line.setStroke(Color.RED);
        line.setOpacity(0.2);

        line.setRotate(45);

        Rectangle rect = new Rectangle();
        rect.setX(100);
        rect.setY(100);
        rect.setWidth(100);
        rect.setHeight(100);
        rect.setFill(Color.GREEN);
        rect.setStrokeWidth(5);
        rect.setStroke(Color.RED);

        Polygon triangle = new Polygon();
        triangle.getPoints().setAll(
                200.0,200.0,
                300.0,300.0,
                200.0,300.0
        );
        triangle.setFill(Color.GREEN);






        root.getChildren().add(text);
        root.getChildren().add(line);
        root.getChildren().add(rect);
        root.getChildren().add(triangle) ;

//        String path = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/galaxy_trucker_icon.png";
//        var url = getClass().getResource(path);
//
//
//        System.out.println("path: " + url);
//
//        if (url == null) {
//            throw new IllegalArgumentException("❌ 图标找不到: " + path);
//        }
//
//        Image image = new Image(url.toExternalForm());
//        stage.getIcons().add(image);

        stage.setTitle("Galaxy Trucker");
//        stage.setWidth(500);
//        stage.setHeight(500);
//        stage.setResizable(false);   //no resizable
//        stage.setX(50);
//        stage.setY(50);
//         stage.setFullScreen(true);
//         stage.setFullScreenExitHint("YOU CAN'T ESCAPE FROM FULL SCREEN MODE unless you press q");
//         stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));


        stage.setScene(scene);
        stage.show();
    }

}



