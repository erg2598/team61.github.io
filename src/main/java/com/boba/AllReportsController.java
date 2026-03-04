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
import java.time.LocalDate;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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

    public class ZReportRow {
        private final SimpleDoubleProperty totalSales;
        private final SimpleIntegerProperty totalOrders;
        private final SimpleIntegerProperty totalItems;

        public ZReportRow(double totalSales, int totalOrders, int totalItems) {
            this.totalSales = new SimpleDoubleProperty(totalSales);
            this.totalOrders = new SimpleIntegerProperty(totalOrders);
            this.totalItems = new SimpleIntegerProperty(totalItems);
        }

        public double getTotalSales() { return totalSales.get(); }
        public SimpleDoubleProperty totalSalesProperty() { return totalSales; }

        public int getTotalOrders() { return totalOrders.get(); }
        public SimpleIntegerProperty totalOrdersProperty() { return totalOrders; }

        public int getTotalItems() { return totalItems.get(); }
        public SimpleIntegerProperty totalItemsProperty() { return totalItems; }
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

        totalSalesZReport.setCellValueFactory(new PropertyValueFactory<>("totalSales"));
        totalOrdersZReport.setCellValueFactory(new PropertyValueFactory<>("totalOrders"));
        totalItemsUsedZReport.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
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
    @FXML private TableColumn<ZReportRow, Integer> totalItemsUsedZReport;
    @FXML private TableColumn<ZReportRow, Integer> totalOrdersZReport;
    @FXML private TableColumn<ZReportRow, Double> totalSalesZReport;
    @FXML private TableView<ZReportRow> zReportTable;
    private LocalDate lastZReportDate = null;

    private ObservableList<ZReportRow> fetchZReport() {
        ObservableList<ZReportRow> rows = FXCollections.observableArrayList();

        String sql = """
            SELECT
                (SELECT COUNT("orderId") FROM "Order" WHERE DATE("orderDate") = CURRENT_DATE) AS total_orders,
                (SELECT COALESCE(SUM("totalAmount"), 0) FROM "Order" WHERE DATE("orderDate") = CURRENT_DATE) AS total_sales,
                (SELECT COALESCE(SUM(oli."quantity"), 0)
                 FROM "Order" o
                 JOIN "OrderLineItem" oli ON o."orderId" = oli."orderId"
                 WHERE DATE(o."orderDate") = CURRENT_DATE) AS total_items
            """;

        try (Connection conn = MainApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                rows.add(new ZReportRow(
                    rs.getDouble("total_sales"),
                    rs.getInt("total_orders"),
                    rs.getInt("total_items")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    @FXML
    void generateZReport(ActionEvent event) {
        LocalDate today = LocalDate.now();

        if (today.equals(lastZReportDate)) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Z-Report Restriction");
            alert.setHeaderText("Report Already Generated");
            alert.setContentText("The Z-Report can only be run once per day. You have already generated it for today.");
            alert.showAndWait();
            return; 
        }

        zReportTable.setItems(fetchZReport());
        
        lastZReportDate = today;
        
        // Disable the button so they visually know they can't click it again
        generateZReport.setDisable(true); 
    }

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }

}
