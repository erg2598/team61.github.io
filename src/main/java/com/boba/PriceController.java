package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class PriceController {

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/ManagerView.fxml");
    }

    @FXML
    void changePrice(ActionEvent event) {
        // TODO
    }

    @FXML
    void saveChanges(ActionEvent event) {
        // TODO
    }
}