module org.polimi.ingsw.galaxytrucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.smartcardio;
    requires jdk.httpserver;
    requires jdk.accessibility;

    opens org.polimi.ingsw.galaxytrucker.view.Gui.Elements to javafx.fxml;
    opens org.polimi.ingsw.galaxytrucker.model.essentials to com.fasterxml.jackson.databind;
    // opens org.polimi.ingsw.galaxytrucker.model.essentials to javafx.fxml;
    opens org.polimi.ingsw.galaxytrucker.model.essentials.components to com.fasterxml.jackson.databind;
    opens org.polimi.ingsw.galaxytrucker.model.adventurecards to com.fasterxml.jackson.databind;
    //opens org.polimi.ingsw.galaxytrucker.model.adventurecards to javafx.fxml;
    opens org.polimi.ingsw.galaxytrucker.model to com.fasterxml.jackson.databind;
    //opens org.polimi.ingsw.galaxytrucker.model to javafx.fxml;
    opens org.polimi.ingsw.galaxytrucker.model.utils to javafx.fxml;

    opens org.polimi.ingsw.galaxytrucker to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker;
    exports org.polimi.ingsw.galaxytrucker.model.essentials;

    exports org.polimi.ingsw.galaxytrucker.model.game;
    opens org.polimi.ingsw.galaxytrucker.model.game to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.model;

    exports org.polimi.ingsw.galaxytrucker.model.adventurecards;

    exports org.polimi.ingsw.galaxytrucker.model.utils;

    exports org.polimi.ingsw.galaxytrucker.view.Gui;
    opens org.polimi.ingsw.galaxytrucker.view.Gui to javafx.fxml;
    exports org.polimi.ingsw.galaxytrucker.network.common;
    exports org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;
    exports org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;
    exports org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;
    exports org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;
    exports org.polimi.ingsw.galaxytrucker.network.server;
    exports org.polimi.ingsw.galaxytrucker.network.client.rmi;
    exports org.polimi.ingsw.galaxytrucker.enums;
    exports org.polimi.ingsw.galaxytrucker.observer;
    exports org.polimi.ingsw.galaxytrucker.controller;
    exports org.polimi.ingsw.galaxytrucker.exceptions;
    exports org.polimi.ingsw.galaxytrucker.model.essentials.components;
    exports org.polimi.ingsw.galaxytrucker.visitors.Network;
    exports org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement;
    exports org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;
    opens org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement to com.fasterxml.jackson.databind;
    exports org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;
    opens org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects to com.fasterxml.jackson.databind;
    exports org.polimi.ingsw.galaxytrucker.visitors.adventurecards;
    exports org.polimi.ingsw.galaxytrucker.visitors.components;
    exports org.polimi.ingsw.galaxytrucker.view.Gui.Dialogs;
    opens org.polimi.ingsw.galaxytrucker.view.Gui.Dialogs to javafx.fxml;
}