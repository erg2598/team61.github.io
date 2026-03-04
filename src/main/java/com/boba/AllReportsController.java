package com.boba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
    @FXML private TableView<ProductUsageRow> productUsageTable;
    @FXML private TableColumn<ProductUsageRow, Integer> totalUsedProductUsage;
    @FXML private TableColumn<ProductUsageRow, Integer> itemIdProductUsage;
    @FXML private TableColumn<ProductUsageRow, String> nameProductUsage;
    public boolean zReportRun = false;

    @FXML
    void generateProductUsage(ActionEvent event) {
            
        DateTimeFormatter dateFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalDate startDate = LocalDate.parse(startDateProductUsage.getText(), dateFormatter);
        LocalDate endDate   = LocalDate.parse(endDateProductUsage.getText(), dateFormatter);

        LocalTime startTime = LocalTime.parse(startTimeProductUsage.getText(), timeFormatter);
        LocalTime endTime   = LocalTime.parse(endTimeProductUsage.getText(), timeFormatter);

        LocalDateTime startDT = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDT   = LocalDateTime.of(endDate, endTime);

        try {

            if (startDT.isAfter(endDT)) {
                showAlert("Invalid Range", "Start date/time must be before end date/time.");
                return;
            }

            ObservableList<ProductUsageRow> data = FXCollections.observableArrayList();

            String sql = """
            SELECT inv."inventoryId" AS item_id,
            inv.name AS inventory_name,
            SUM(oli.quantity * ing."quantityUsed") AS total_used
            FROM public."Order" o
            JOIN public."OrderLineItem" oli 
                ON o."orderId" = oli."orderId"
            JOIN public."Ingredients" ing 
                ON oli."itemId" = ing."itemId"
            JOIN public."Inventory" inv 
                ON ing."inventoryId" = inv."inventoryId"
            WHERE o."orderDate" BETWEEN ? AND ?
            GROUP BY inv."inventoryId", inv.name
            ORDER BY total_used DESC""";

            try (Connection conn = MainApp.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setTimestamp(1, Timestamp.valueOf(startDT));
                ps.setTimestamp(2, Timestamp.valueOf(endDT));

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    data.add(new ProductUsageRow(
                        rs.getInt("item_id"),
                        rs.getString("inventory_name"),
                        rs.getInt("total_used")
                    ));
                }
            }

            productUsageTable.setItems(data);

        } catch (DateTimeParseException e) {
            showAlert("Input Error", "Dates must be yyyy-MM-dd and times must be HH:mm.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
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
    @FXML private TableColumn<SalesReportRow, Double> totalRevenueSalesReport;

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

        itemIdProductUsage.setCellValueFactory(new PropertyValueFactory<>("itemId"));

        nameProductUsage.setCellValueFactory(new PropertyValueFactory<>("name"));

        totalUsedProductUsage.setCellValueFactory(new PropertyValueFactory<>("totalUsed"));
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
        String sql;
        if(zReportRun){
            sql = """
            SELECT EXTRACT(HOUR FROM "orderDate") AS order_hour, 0 AS revenue
            FROM "Order"
            WHERE DATE("orderDate") = CURRENT_DATE
            GROUP BY EXTRACT(HOUR FROM "orderDate")
            ORDER BY order_hour
            """;
        } else{
            sql = """
            SELECT EXTRACT(HOUR FROM "orderDate") AS order_hour, SUM("totalAmount") AS revenue
            FROM "Order"
            WHERE DATE("orderDate") = CURRENT_DATE
            GROUP BY EXTRACT(HOUR FROM "orderDate")
            ORDER BY order_hour
            """;
        }

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
        zReportRun = true; 
    }

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public class ProductUsageRow {

        private final SimpleIntegerProperty itemId;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty totalUsed;

        public ProductUsageRow(int itemId, String name, int totalUsed) {
            this.itemId = new SimpleIntegerProperty(itemId);
            this.name = new SimpleStringProperty(name);
            this.totalUsed = new SimpleIntegerProperty(totalUsed);
        }

        public int getItemId() {
            return itemId.get();
        }

        public String getName() {
            return name.get();
        }

        public int getTotalUsed() {
            return totalUsed.get();
        }
    }
}
