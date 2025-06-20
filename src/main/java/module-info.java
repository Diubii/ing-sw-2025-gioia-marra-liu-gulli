module it.polimi.ingsw.galaxytrucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.smartcardio;
    requires jdk.httpserver;
    requires jdk.accessibility;
    requires java.sql;

    opens it.polimi.ingsw.galaxytrucker.view.Gui.Elements to javafx.fxml;
    opens it.polimi.ingsw.galaxytrucker.model.essentials to com.fasterxml.jackson.databind;
    // opens it.polimi.ingsw.galaxytrucker.model.essentials to javafx.fxml;
    opens it.polimi.ingsw.galaxytrucker.model.essentials.components to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.galaxytrucker.model.adventurecards to com.fasterxml.jackson.databind;
    //opens it.polimi.ingsw.galaxytrucker.model.adventurecards to javafx.fxml;
    opens it.polimi.ingsw.galaxytrucker.model to com.fasterxml.jackson.databind;
    //opens it.polimi.ingsw.galaxytrucker.model to javafx.fxml;
    opens it.polimi.ingsw.galaxytrucker.model.utils to javafx.fxml;

    opens it.polimi.ingsw.galaxytrucker to javafx.fxml;
    exports it.polimi.ingsw.galaxytrucker;
    exports it.polimi.ingsw.galaxytrucker.model.essentials;

    exports it.polimi.ingsw.galaxytrucker.model.game;
    opens it.polimi.ingsw.galaxytrucker.model.game to javafx.fxml;
    exports it.polimi.ingsw.galaxytrucker.model;

    exports it.polimi.ingsw.galaxytrucker.model.adventurecards;

    exports it.polimi.ingsw.galaxytrucker.model.utils;

    exports it.polimi.ingsw.galaxytrucker.view.Gui;
    opens it.polimi.ingsw.galaxytrucker.view.Gui to javafx.fxml;
    exports it.polimi.ingsw.galaxytrucker.network.common;
    exports it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;
    exports it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;
    exports it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;
    exports it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;
    exports it.polimi.ingsw.galaxytrucker.network.server;
    exports it.polimi.ingsw.galaxytrucker.network.client.rmi;
    exports it.polimi.ingsw.galaxytrucker.enums;
    exports it.polimi.ingsw.galaxytrucker.observer;
    exports it.polimi.ingsw.galaxytrucker.controller;
    exports it.polimi.ingsw.galaxytrucker.exceptions;
    exports it.polimi.ingsw.galaxytrucker.model.essentials.components;
    exports it.polimi.ingsw.galaxytrucker.visitors.Network;
    exports it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement;
    exports it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;
    opens it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;
    opens it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects to com.fasterxml.jackson.databind;
    exports it.polimi.ingsw.galaxytrucker.visitors.adventurecards;
    exports it.polimi.ingsw.galaxytrucker.visitors.components;
    exports it.polimi.ingsw.galaxytrucker.view.Gui.Dialogs;
    opens it.polimi.ingsw.galaxytrucker.view.Gui.Dialogs to javafx.fxml;
    exports it.polimi.ingsw.galaxytrucker.view.Gui.Abstract;
    opens it.polimi.ingsw.galaxytrucker.view.Gui.Abstract to javafx.fxml;
    exports it.polimi.ingsw.galaxytrucker.network.client;
    exports it.polimi.ingsw.galaxytrucker.view;
}