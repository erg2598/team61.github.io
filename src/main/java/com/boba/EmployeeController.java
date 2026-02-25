package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EmployeeController {

    @FXML private Button returnHomeBtn;

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }

    @FXML
    void addEmployee(ActionEvent event) {
        // TODO
    }

    @FXML
    void saveChanges(ActionEvent event) {
        // TODO
    }

    @FXML
    void removeEmployee(ActionEvent event) {
        // TODO
    }
}