package org.polimi.ingsw.galaxytrucker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Engine;

import java.io.IOException;
import java.util.ArrayList;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        ArrayList<Connector> sides;
        sides = new ArrayList<>();
        sides.add(Connector.DOUBLE);
        sides.add(Connector.DOUBLE);
        sides.add(Connector.DOUBLE);
        sides.add(Connector.DOUBLE);
        Engine E = new Engine("Engine",0);


        Ship ship = new Ship(true);
        HelloController.myShip = ship;
        Tile tile = new Tile(1,0, sides,E );
        ship.putTile(tile, new Position(4,4));
        HelloController.messages.push(ship.toString());
        HelloController.messages.push(E.accept(new ComponentNameVisitor()));
        HelloController.messages.push(ship.toString());




    }

    public static void main(String[] args) {
        launch();
    }
}