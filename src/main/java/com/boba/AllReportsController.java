package com.boba;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AllReportsController {

    public class SalesReportRow {
    private final SimpleIntegerProperty itemId;
    private final SimpleStringProperty itemName;
    private final SimpleIntegerProperty total;

    public SalesReportRow(int itemId, String itemName, int total) {
        this.itemId = new SimpleIntegerProperty(itemId);
        this.itemName = new SimpleStringProperty(itemName);
        this.total = new SimpleIntegerProperty(total);
    }

    public int getItemId() { return itemId.get(); }
    public SimpleIntegerProperty itemIdProperty() { return itemId; }

    public String getItemName() { return itemName.get(); }
    public SimpleStringProperty itemNameProperty() { return itemName; }

    public int getTotal() { return total.get(); }
    public SimpleIntegerProperty totalProperty() { return total; }

    
    }
    public class XReportRow {
    private final SimpleIntegerProperty hour;
    private final SimpleDoubleProperty revenue;

    public XReportRow(int hour, double revenue) {
        this.hour = new SimpleIntegerProperty(hour);
        this.revenue = new SimpleDoubleProperty(revenue);
    }

    public int getHour() { return hour.get(); }
    public SimpleIntegerProperty hourProperty() { return hour; }

    public double getRevenue() { return revenue.get(); }
    public SimpleDoubleProperty revenueProperty() { return revenue; }
    }

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
    @FXML private TableView<SalesReportRow> salesReportTable;
    @FXML private TableColumn<SalesReportRow, Integer> itemIdSalesReport;
    @FXML private TableColumn<SalesReportRow, String> nameSalesReport;
    @FXML private TableColumn<SalesReportRow, Integer> totalSoldSalesReport;

    @FXML
    public void initialize() {
        itemIdSalesReport.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        nameSalesReport.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        totalSoldSalesReport.setCellValueFactory(new PropertyValueFactory<>("total"));

        hourXReport.setCellValueFactory(new PropertyValueFactory<>("hour"));
        salesXReport.setCellValueFactory(new PropertyValueFactory<>("revenue"));
    }

    private ObservableList<SalesReportRow> fetchSalesReport(Timestamp start, Timestamp end) {
    ObservableList<SalesReportRow> rows = FXCollections.observableArrayList();

    String sql = """
        SELECT i."itemId", i."name", SUM(oli."quantity") AS total
        FROM "Order" o
        JOIN "OrderLineItem" oli ON o."orderId" = oli."orderId"
        JOIN "Item" i ON oli."itemId" = i."itemId"
        WHERE o."orderDate" BETWEEN ? AND ?
        GROUP BY i."itemId", i."name"
        ORDER BY total DESC
        """;

    try (Connection conn = MainApp.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setTimestamp(1, start);
        stmt.setTimestamp(2, end);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            rows.add(new SalesReportRow(
                rs.getInt("itemId"),
                rs.getString("name"),
                rs.getInt("total")
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return rows;
}
    
    @FXML
    void generateSalesReport(ActionEvent event) {
        try {
            String startTimestamp = startDateSalesReport.getText().trim() + " " + startTimeSalesReport.getText().trim();
            String endTimestamp   = endDateSalesReport.getText().trim()   + " " + endTImeSalesReport.getText().trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Timestamp start = Timestamp.valueOf(LocalDateTime.parse(startTimestamp, formatter));
            Timestamp end   = Timestamp.valueOf(LocalDateTime.parse(endTimestamp,   formatter));

            salesReportTable.setItems(fetchSalesReport(start, end));

        } catch (DateTimeParseException e) {
            System.err.println("Invalid date/time format. Use yyyy-MM-dd and HH:mm:ss");
        }
    }

    @FXML private Button generateXReport;
    @FXML private TableView<XReportRow> xReportTable;
    @FXML private TableColumn<XReportRow, Integer> hourXReport;
    @FXML private TableColumn<XReportRow, Double> salesXReport;

   private ObservableList<XReportRow> fetchXReport() {
        ObservableList<XReportRow> rows = FXCollections.observableArrayList();

        String sql = """
            SELECT EXTRACT(HOUR FROM "orderDate") AS order_hour, SUM("totalAmount") AS revenue
            FROM "Order"
            WHERE DATE("orderDate") = CURRENT_DATE
            GROUP BY EXTRACT(HOUR FROM "orderDate")
            ORDER BY order_hour
            """;

        try (Connection conn = MainApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(new XReportRow(
                    rs.getInt("order_hour"),
                    rs.getDouble("revenue")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    @FXML
    void generateXReport(ActionEvent event) {
        xReportTable.setItems(fetchXReport());
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
