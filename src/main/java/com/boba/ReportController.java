package com.boba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
/**
 * This class contains all of the logic for the manager view to be able to view reports.
 * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
 */
public class ReportController {

    /**
     * This class does not have a constructor.
     */
    public ReportController(){}
    @FXML private TableView<OrderRow> reportTable;
    @FXML private TableColumn<OrderRow, Integer> orderIdCol;
    @FXML private TableColumn<OrderRow, String>  customerCol;
    @FXML private TableColumn<OrderRow, String>  statusCol;
    @FXML private TableColumn<OrderRow, Double>  totalCol;
    @FXML private TableColumn<OrderRow, String>  dateCol;

    /**
     * This is a nested class in ReportController
     * It contains the values for the report table.
     * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
     */
    public static class OrderRow {
        private final IntegerProperty orderId;
        private final StringProperty  customer;
        private final StringProperty  status;
        private final DoubleProperty  total;
        private final StringProperty  date;

        /**
         * The constructor for each row in the report table.
         * @param id The ID of the order.
         * @param customer The name of the customer.
         * @param status The status of the order.
         * @param total The total amount of the order.
         * @param date The date of the order.
         */
        public OrderRow(int id, String customer, String status, double total, String date) {
            this.orderId  = new SimpleIntegerProperty(id);
            this.customer = new SimpleStringProperty(customer);
            this.status   = new SimpleStringProperty(status);
            this.total    = new SimpleDoubleProperty(total);
            this.date     = new SimpleStringProperty(date);
        }
    }

    /**
     * Initializes the report table for each column.
     */
    @FXML
    public void initialize() {
        orderIdCol.setCellValueFactory(c->c.getValue().orderId.asObject());
        customerCol.setCellValueFactory(c->c.getValue().customer);
        statusCol.setCellValueFactory(c->c.getValue().status);
        totalCol.setCellValueFactory(c->c.getValue().total.asObject());
        dateCol.setCellValueFactory(c->c.getValue().date);
    }

    /**
     * Generates the daily report by querying the database for all orders on the present day
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void dailyReport(ActionEvent event) {
        ObservableList<OrderRow> rows = FXCollections.observableArrayList();
        double dailyTotal = 0.0;

        String sql = "SELECT \"orderId\", \"customerName\", status, \"totalAmount\", \"orderDate\" " +
                     "FROM public.\"Order\" " +
                     "WHERE DATE(\"orderDate\") = CURRENT_DATE " +
                     "ORDER BY \"orderId\"";

        try (Connection conn = MainApp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double total = rs.getDouble("totalAmount");
                dailyTotal += total;
                rows.add(new OrderRow(
                        rs.getInt("orderId"),
                        rs.getString("customerName"),
                        rs.getString("status"),
                        total,
                        rs.getTimestamp("orderDate").toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
            return;
        }

        reportTable.setItems(rows);

        if (rows.isEmpty()) {
            showAlert("Daily Report", "No orders found for today.");
        } else {
            showAlert("Daily Report",
                    "Orders today: " + rows.size() + "\n" +
                    "Total revenue: $" + String.format("%.2f", dailyTotal));
        }
    }

    /**
     * Returns the page where a manager can select which report to view.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/ManagerView.fxml");
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
