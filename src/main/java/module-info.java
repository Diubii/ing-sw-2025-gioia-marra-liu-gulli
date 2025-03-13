module org.polimi.ingsw.galaxytruckers {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.polimi.ingsw.galaxytruckers to javafx.fxml;
    exports org.polimi.ingsw.galaxytruckers;
    exports org.polimi.ingsw.galaxytruckers.model.units;
    opens org.polimi.ingsw.galaxytruckers.model.units to javafx.fxml;
}