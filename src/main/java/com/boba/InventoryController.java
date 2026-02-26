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

public class InventoryController {

    @FXML private TableView<InventoryRow> inventoryTable;
    @FXML private TableColumn<InventoryRow, Integer> idCol;
    @FXML private TableColumn<InventoryRow, String>  nameCol;
    @FXML private TableColumn<InventoryRow, Double>  qtyCol;
    @FXML private TableColumn<InventoryRow, Double>  priceCol;
    @FXML private TableColumn<InventoryRow, Integer> reorderCol;
    @FXML private TableColumn<InventoryRow, String>  typeCol;

    public static class InventoryRow {
        public final IntegerProperty inventoryId;
        public final StringProperty  name;
        public final DoubleProperty  quantity;
        public final DoubleProperty  price;
        public final IntegerProperty reorder;
        public final StringProperty  type;

        public InventoryRow(int id, String name, double qty, double price, int reorder, String type) {
            this.inventoryId = new SimpleIntegerProperty(id);
            this.name        = new SimpleStringProperty(name);
            this.quantity    = new SimpleDoubleProperty(qty);
            this.price       = new SimpleDoubleProperty(price);
            this.reorder     = new SimpleIntegerProperty(reorder);
            this.type        = new SimpleStringProperty(type);
        }
    }

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c      -> c.getValue().inventoryId.asObject());
        nameCol.setCellValueFactory(c    -> c.getValue().name);
        qtyCol.setCellValueFactory(c     -> c.getValue().quantity.asObject());
        priceCol.setCellValueFactory(c   -> c.getValue().price.asObject());
        reorderCol.setCellValueFactory(c -> c.getValue().reorder.asObject());
        typeCol.setCellValueFactory(c    -> c.getValue().type);
        loadTable(null);
    }

    // LOADS the table in the viewer
    @FXML
    public void loadTable(ActionEvent event) {
        ObservableList<InventoryRow> rows = FXCollections.observableArrayList();
        String sql = "SELECT \"inventoryId\", name, \"quantityOnHand\", \"pricePerUnit\", " +
                     "\"reorderThreshold\", \"type\" FROM public.\"Inventory\" ORDER BY \"inventoryId\"";
        try (Connection conn = MainApp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new InventoryRow(
                        rs.getInt("inventoryId"),
                        rs.getString("name"),
                        rs.getDouble("quantityOnHand"),
                        rs.getDouble("pricePerUnit"),
                        rs.getInt("reorderThreshold"),
                        rs.getString("type")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        inventoryTable.setItems(rows);
    }


    // ADDS inventory item with the necessary data
    @FXML
    void addInventoryItem(ActionEvent event) {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Inventory Item");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Item Name:");
        String name = nameDialog.showAndWait().orElse(null);
        if (name == null || name.isBlank()) return;

        TextInputDialog qtyDialog = new TextInputDialog("0");
        qtyDialog.setTitle("Add Inventory Item");
        qtyDialog.setHeaderText(null);
        qtyDialog.setContentText("Quantity On Hand:");
        String qtyStr = qtyDialog.showAndWait().orElse(null);
        if (qtyStr == null) return;

        TextInputDialog priceDialog = new TextInputDialog("0.00");
        priceDialog.setTitle("Add Inventory Item");
        priceDialog.setHeaderText(null);
        priceDialog.setContentText("Price Per Unit:");
        String priceStr = priceDialog.showAndWait().orElse(null);
        if (priceStr == null) return;

        TextInputDialog reorderDialog = new TextInputDialog("0");
        reorderDialog.setTitle("Add Inventory Item");
        reorderDialog.setHeaderText(null);
        reorderDialog.setContentText("Reorder Threshold:");
        String reorderStr = reorderDialog.showAndWait().orElse(null);
        if (reorderStr == null) return;

        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("Other", "Base", "Flavor", "Topping", "Other");
        typeDialog.setTitle("Add Inventory Item");
        typeDialog.setHeaderText(null);
        typeDialog.setContentText("Type:");
        String type = typeDialog.showAndWait().orElse(null);
        if (type == null) return;

        try {
            double qty     = Double.parseDouble(qtyStr);
            double price   = Double.parseDouble(priceStr);
            int    reorder = Integer.parseInt(reorderStr);
            String sql = "INSERT INTO public.\"Inventory\" " +
                         "(name, \"quantityOnHand\", \"pricePerUnit\", \"reorderThreshold\", \"type\") " +
                         "VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = MainApp.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setDouble(2, qty);
                ps.setDouble(3, price);
                ps.setInt(4, reorder);
                ps.setString(5, type);
                ps.executeUpdate();
            }
            loadTable(null);
            showAlert("Success", "Inventory item added.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void returnHome(ActionEvent event) {
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