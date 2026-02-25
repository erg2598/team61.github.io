package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class InventoryController {

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/ManagerView.fxml");
    }

    @FXML
    void addItem(ActionEvent event) {
    }

    @FXML
    void saveChanges(ActionEvent event) {
    }
}