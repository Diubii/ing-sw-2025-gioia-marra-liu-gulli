module org.polimi.ingsw.galaxytrucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.management.rmi;

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
    exports org.polimi.ingsw.galaxytrucker.view.Tui.util;
    opens org.polimi.ingsw.galaxytrucker.view.Tui.util to javafx.fxml; // 👈 aggiungi questa riga!
    exports org.polimi.ingsw.galaxytrucker.network.common;
    opens org.polimi.ingsw.galaxytrucker.network.common to java.rmi;
    exports org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;
    exports org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;
    exports org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;
    opens org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages to java.rmi;
}