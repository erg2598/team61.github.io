package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InventoryController {

    @FXML
    private Button addInventoryItem;

    @FXML
    private Button editInventoryItem;

    @FXML
    private TextField ingredientType;

    @FXML
    private TextField ingredientType1;

    @FXML
    private TextField itemID;

    @FXML
    private TextField itemID1;

    @FXML
    private TextField itemName;

    @FXML
    private TextField itemName1;

    @FXML
    private TextField itemPrice;

    @FXML
    private TextField itemPrice1;

    @FXML
    private TextField itemQuantity;

    @FXML
    private TextField itemQuantity1;

    @FXML
    private TextField reorderThreshold;

    @FXML
    private TextField reorderThreshold1;

    @FXML
    void addInventoryItem(ActionEvent event) {

    }

    @FXML
    void editInventoryItem(ActionEvent event) {

    }

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }

}
