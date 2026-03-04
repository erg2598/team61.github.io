package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
/**
 * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
*/

public class ManagerViewController {

    @FXML private Button launchEmployeeBtn; // wire any button for stage reference

    /**
     * @param ActionEvent This function will trigger when a button is clicked.
     * @return Returns nothing.
     * @throws none there is no error handling for this code
     */
    @FXML
    void launchEmployeeView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        MainApp.switchScene(stage, "/fxml/Employee.fxml");
    }

    @FXML
    void launchInventoryView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/Inventory.fxml");
    }

    @FXML
    void launchPricesView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/ItemsAndPrices.fxml");
    }

    @FXML
    void launchReportsView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/AllReports.fxml");
    }

    @FXML
    void goToCashierView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/cashierview.fxml");
    }
}