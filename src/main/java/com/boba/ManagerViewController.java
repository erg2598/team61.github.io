package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;

/**
 * This class contains all of the logic for
 * the manager view where so that the manager can navigate to different screens.
 * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
 */
public class ManagerViewController {
    /**
     * This class does not have a constructor.
     */
    public ManagerViewController(){}

    @FXML private Button launchEmployeeBtn; 

    /**
     * goes to employee management view.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void launchEmployeeView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(true);
        MainApp.switchScene(stage, "/fxml/Employee.fxml");
    }

    /**
    * goes to the inventory management view.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void launchInventoryView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/Inventory.fxml");
    }


    /**
     * goes to the items and prices management view.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void launchPricesView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/ItemsAndPrices.fxml");
    }

    /**
     * goes to the reports view where the manager is able to generate reports.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void launchReportsView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/AllReports.fxml");
    }


    /**
     * Returns the manager back to the cashier view.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void goToCashierView(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/cashierview.fxml");
    }
}