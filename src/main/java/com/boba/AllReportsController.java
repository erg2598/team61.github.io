package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AllReportsController {

    @FXML
    private TextField endDateProductUsage;

    @FXML
    private TextField endDateSalesReport;

    @FXML
    private TextField endTImeSalesReport;

    @FXML
    private TextField endTimeProductUsage;

    @FXML
    private Button generateProductUsage;

    @FXML
    private Button generateSalesReport;

    @FXML
    private Button generateXReport;

    @FXML
    private Button generateZReport;

    @FXML
    private TableColumn<?, ?> hourXReport;

    @FXML
    private TableColumn<?, ?> itemIdProductUsage;

    @FXML
    private TableColumn<?, ?> itemIdSalesReport;

    @FXML
    private TableColumn<?, ?> nameProductUsage;

    @FXML
    private TableColumn<?, ?> nameSalesReport;

    @FXML
    private TableView<?> productUsageTable;

    @FXML
    private TableView<?> productUsageTable1;

    @FXML
    private TableColumn<?, ?> salesXReport;

    @FXML
    private TextField startDateProductUsage;

    @FXML
    private TextField startDateSalesReport;

    @FXML
    private TextField startTimeProductUsage;

    @FXML
    private TextField startTimeSalesReport;

    @FXML
    private TableColumn<?, ?> totalItemsUsedZReport;

    @FXML
    private TableColumn<?, ?> totalOrdersZReport;

    @FXML
    private TableColumn<?, ?> totalSalesZReport;

    @FXML
    private TableColumn<?, ?> totalSold;

    @FXML
    private TableColumn<?, ?> totalUsedProductUsage;

    @FXML
    private TableView<?> xReportTable;

    @FXML
    private TableView<?> zReportTable;

    @FXML
    void generateProductUsage(ActionEvent event) {

    }

    @FXML
    void generateSalesReport(ActionEvent event) {

    }

    @FXML
    void generateXReport(ActionEvent event) {

    }

    @FXML
    void generateZReport(ActionEvent event) {

    }

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
        stage.setMaximized(true);
    }

}
