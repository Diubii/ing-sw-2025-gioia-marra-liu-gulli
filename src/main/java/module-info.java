module org.polimi.ingsw.galaxytrucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.polimi.ingsw.galaxytrucker to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker;
    exports org.polimi.ingsw.galaxytrucker.enums;
    exports org.polimi.ingsw.galaxytrucker.model.units;
    opens org.polimi.ingsw.galaxytrucker.model.units to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.model.game;
    opens org.polimi.ingsw.galaxytrucker.model.game to javafx.fxml;
}