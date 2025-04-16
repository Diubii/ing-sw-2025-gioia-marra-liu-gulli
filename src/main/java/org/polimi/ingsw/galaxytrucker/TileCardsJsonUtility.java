package org.polimi.ingsw.galaxytrucker;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TileCardsJsonUtility extends Application {

    //Condivisi
    @FXML private StackPane contentPane;  // Area centrale che cambia contenuto


    //Tiles
    private Pane tilePane;
    @FXML private TextField tileId;
    @FXML private TextField extraVal;
    @FXML private ComboBox<String> tileComponentType;
    @FXML private ComboBox<String> comboBoxTopConn;
    @FXML private ComboBox<String> comboBoxRConn;
    @FXML private ComboBox<String> comboBoxBotConn;
    @FXML private ComboBox<String> comboBoxLConn;
    @FXML private ComboBox<String> centralHousingUnitColor;
    @FXML private CheckBox specialCargo;


    @FXML private ImageView tileImageView;
    private Image tileImage;

    //Carte
    private Pane cartePane;
    @FXML private TextField cardId;
    @FXML private ImageView imageViewFronte;
    private File immagineFronte;
    @FXML private ImageView imageViewRetro;
    private File immagineRetro;

    //Lista di Tiles
    ArrayList<Tile> tiles = new ArrayList<>();
    int tileIndex = 0;
    ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();

    //Lista di Carte
    ArrayList<AdventureCard> cards = new ArrayList<>();
    int cardIndex = 0;
    //<editor-fold desc="Condiviso">


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Creazione del layout principale
        BorderPane root = new BorderPane();

        // Creazione della barra menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Seleziona Vista");
        MenuItem tileMenuItem = new MenuItem("Gestisci Tiles");
        MenuItem carteMenuItem = new MenuItem("Gestisci Carte");
        menu.getItems().addAll(tileMenuItem, carteMenuItem);
        menuBar.getMenus().add(menu);
        root.setTop(menuBar);  // Aggiunge la barra menu in alto

        // StackPane per contenere le viste
        contentPane = new StackPane();
        root.setCenter(contentPane);  // Lo posiziona al centro

        // Caricamento dei due pannelli dagli FXML
        try {
            tilePane = FXMLLoader.load(getClass().getResource("JsonUtilityPages/TilePanel.fxml"));
            cartePane = FXMLLoader.load(getClass().getResource("JsonUtilityPages/CardsPanel.fxml"));
        } catch (IOException e) {
            System.err.println("Errore nel caricamento degli FXML: " + e.getMessage());
            e.printStackTrace();
            return;  // Se fallisce, fermiamo l'avvio
        }

        // Mostriamo inizialmente il pannello delle pedine
        contentPane.getChildren().add(tilePane);

        // Azioni per cambiare vista
        tileMenuItem.setOnAction(e -> showPedinePane());
        carteMenuItem.setOnAction(e -> showCartePane());

        // Configurazione della scena e della finestra principale
        primaryStage.setTitle("Gestione Oggetti di Gioco");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();
    }

    @FXML
    private void showPedinePane() {
        contentPane.getChildren().setAll(tilePane);
    }

    @FXML
    private void showCartePane() {
        contentPane.getChildren().setAll(cartePane);
    }

    public static void main(String[] args) {
        launch(args);
    }


    //</editor-fold>

    //<editor-fold desc="Per Tiles">

    public void handleShowTileId(ActionEvent actionEvent) {
        String tileIdVal = tileId.getText();
        showTile( Integer.parseInt(tileIdVal)-1 );
    }

    public void handleNextTile(ActionEvent actionEvent) {

    }

    public void handlePrevTile(ActionEvent actionEvent) {

    }

    public void handleAddTileToList(ActionEvent actionEvent) {

        ArrayList<Connector> connectionsapp= new ArrayList<>();
        Tile tile;
        Component comp;

        String componentString = tileComponentType.getValue(); // Per ComboBox
        String tileIdVal = tileId.getText(); // Per TextField

        // Debug
        System.out.println("Valore ComboBox: " + componentString);
        System.out.println("Valore TextField: " + tileIdVal);


        try {
            connectionsapp.add(Connector.fromString(comboBoxTopConn.getValue()));
            connectionsapp.add(Connector.fromString(comboBoxRConn.getValue()));
            connectionsapp.add(Connector.fromString(comboBoxBotConn.getValue()));
            connectionsapp.add(Connector.fromString(comboBoxLConn.getValue()));
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connectors: " + connectionsapp);

        switch (tileComponentType.getValue()){
            case "BatterySlot":
                 comp = new BatterySlot(Integer.parseInt(extraVal.getText()));
                break;
            case "Cannon":
                comp = new Cannon((float)1.0);
                break;
            case "CentralHousingUnit":
                comp = new CentralHousingUnit(Color.fromString(centralHousingUnitColor.getValue()));
                break;
            case "DoubleCannon":
                comp = new DoubleCannon(false,(float)2.0);
                break;
            case "DoubleEngine":
                comp = new DoubleEngine(false,2);
                break;
            case "Engine":
                comp = new Engine(1);
                break;
            case "GenericCargoHolds":
                comp = new GenericCargoHolds(specialCargo.isSelected(),Integer.parseInt(extraVal.getText()));
                break;
            case "PurpleLifeSupportSystem":
                comp = new LifeSupportSystem(AlienColor.PURPLE);
                break;
            case "BrownLifeSupportSystem":
                comp = new LifeSupportSystem(AlienColor.BROWN);
                break;
            case "ModularHousingUnit":
                comp = new ModularHousingUnit(AlienColor.EMPTY);
                break;
            case "Shield":
                comp = new Shield(false);
                break;

            default:
                comp = new Component();
            break;
        }

        tile = new Tile(Integer.parseInt(tileIdVal),0,connectionsapp,comp);
        tiles.add( Integer.parseInt(tileIdVal)-1 ,tile);
        tile.testPrint();
        System.out.println("tiles: " + tiles);
    }

    @FXML
    /**
     * Loads the image of the tile with the id specified in the TextBox
     */
    private void handleLoadTileImage() {
        String tileIdVal = tileId.getText();
        if(tileIdVal!=null && !tileIdVal.equals("")) {
            String imagePath = "galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
            // Caricare l'immagine dal classpath
            tileImage = new Image(getClass().getResourceAsStream(imagePath));
            tileImageView.setImage(tileImage);
        }
    }

    /**
     * Shows the tile with that id from the tiles ArrayList
     * @param id
     */
    private void showTile(int id){
        //DEBUG
        System.out.println("ShowTile chiamato");
        System.out.println(tiles.get(id).getMyComponent().accept(componentNameVisitor));

        tileComponentType.setValue(tiles.get(id).getMyComponent().accept(componentNameVisitor));
        comboBoxTopConn.setValue(tiles.get(id).getSides().get(0).getConnectorString());
        comboBoxRConn.setValue(tiles.get(id).getSides().get(1).getConnectorString());
        comboBoxBotConn.setValue(tiles.get(id).getSides().get(2).getConnectorString());
        comboBoxLConn.setValue(tiles.get(id).getSides().get(3).getConnectorString());
        handleLoadTileImage();

        switch (tiles.get(id).getMyComponent().accept(componentNameVisitor)){
            case "BatterySlot":
                break;
            case "CentralHousingUnit":
                break;


        }
    }

    //</editor-fold>



    //<editor-fold desc=" Per Carte ">

    public void handleShowCardId(ActionEvent actionEvent) {

    }

    public void handleNextCard(ActionEvent actionEvent) {

    }

    public void handlePrevCard(ActionEvent actionEvent) {

    }

    public void handleAddCardToList(ActionEvent actionEvent) {
    }
    @FXML
    private void handleLoadFront() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            immagineFronte = file;
            imageViewFronte.setImage(new Image(file.toURI().toString()));
        }
    }
    @FXML
    private void handleLoadBack() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            immagineRetro = file;
            imageViewRetro.setImage(new Image(file.toURI().toString()));
        }
    }
    //</editor-fold>
}

