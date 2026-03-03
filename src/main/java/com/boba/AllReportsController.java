package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AllReportsController {

    @FXML private TextField endDateProductUsage;
    @FXML private TextField endTimeProductUsage;
    @FXML private TextField startDateProductUsage;
    @FXML private TextField startTimeProductUsage;
    @FXML private Button generateProductUsage;
    @FXML private TableView<?> productUsageTable;
    @FXML private TableColumn<?, ?> totalUsedProductUsage;
    @FXML private TableColumn<?, ?> itemIdProductUsage;
    @FXML private TableColumn<?, ?> nameProductUsage;

    @FXML
    void generateProductUsage(ActionEvent event) {

    }

    @FXML private TextField startDateSalesReport;
    @FXML private TextField endDateSalesReport;
    @FXML private TextField startTimeSalesReport;
    @FXML private TextField endTImeSalesReport;
    @FXML private Button generateSalesReport;
    @FXML private TableView<?> salesReportTable;
    @FXML private TableColumn<?, ?> itemIdSalesReport;
    @FXML private TableColumn<?, ?> nameSalesReport;
    @FXML private TableColumn<?, ?> totalSoldSalesReport;
    
    @FXML
    void generateSalesReport(ActionEvent event) {

    }

    @FXML private Button generateXReport;
    @FXML private TableView<?> xReportTable;
    @FXML private TableColumn<?, ?> hourXReport;
    @FXML private TableColumn<?, ?> salesXReport;

    @FXML
    void generateXReport(ActionEvent event) {

    }

    @FXML private Button generateZReport;
    @FXML private TableColumn<?, ?> totalItemsUsedZReport;
    @FXML private TableColumn<?, ?> totalOrdersZReport;
    @FXML private TableColumn<?, ?> totalSalesZReport;
    @FXML private TableView<?> zReportTable;

    @FXML
    void generateZReport(ActionEvent event) {

    }

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }

}
