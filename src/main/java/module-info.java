module org.polimi.ingsw.galaxytrucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.polimi.ingsw.galaxytrucker to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker;
    exports org.polimi.ingsw.galaxytrucker.model.essentials;
    opens org.polimi.ingsw.galaxytrucker.model.essentials to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.model.game;
    opens org.polimi.ingsw.galaxytrucker.model.game to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.model;
    opens org.polimi.ingsw.galaxytrucker.model to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.model.adventurecards;
    opens org.polimi.ingsw.galaxytrucker.model.adventurecards to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.model.utils;
    opens org.polimi.ingsw.galaxytrucker.model.utils to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.view.Gui;
    opens org.polimi.ingsw.galaxytrucker.view.Gui to javafx.fxml;
}