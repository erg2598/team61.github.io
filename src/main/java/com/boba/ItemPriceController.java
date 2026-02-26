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

public class ItemPriceController {

    @FXML private TableView<ItemRow> itemTable;
    @FXML private TableColumn<ItemRow, Integer> idCol;
    @FXML private TableColumn<ItemRow, String>  nameCol;
    @FXML private TableColumn<ItemRow, Double>  priceCol;
    @FXML private TableColumn<ItemRow, String>  sizeCol;

    public static class ItemRow {
        public final IntegerProperty itemId;
        public final StringProperty  name;
        public final DoubleProperty  price;
        public final StringProperty  size;

        public ItemRow(int id, String name, double price, String size) {
            this.itemId = new SimpleIntegerProperty(id);
            this.name   = new SimpleStringProperty(name);
            this.price  = new SimpleDoubleProperty(price);
            this.size   = new SimpleStringProperty(size);
        }
    }

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c    -> c.getValue().itemId.asObject());
        nameCol.setCellValueFactory(c  -> c.getValue().name);
        priceCol.setCellValueFactory(c -> c.getValue().price.asObject());
        sizeCol.setCellValueFactory(c  -> c.getValue().size);
        loadTable(null);
    }

    @FXML
    public void loadTable(ActionEvent event) {
        ObservableList<ItemRow> rows = FXCollections.observableArrayList();
        String sql = "SELECT \"itemId\", name, \"basePrice\", size FROM public.\"Item\" ORDER BY \"itemId\"";
        try (Connection conn = MainApp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new ItemRow(
                        rs.getInt("itemId"),
                        rs.getString("name"),
                        rs.getDouble("basePrice"),
                        rs.getString("size")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemTable.setItems(rows);
    }

    @FXML
    void addMenuItem(ActionEvent event) {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Menu Item");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Item Name:");
        String name = nameDialog.showAndWait().orElse(null);
        if (name == null || name.isBlank()) return;

        TextInputDialog priceDialog = new TextInputDialog("0.00");
        priceDialog.setTitle("Add Menu Item");
        priceDialog.setHeaderText(null);
        priceDialog.setContentText("Base Price:");
        String priceStr = priceDialog.showAndWait().orElse(null);
        if (priceStr == null) return;

        ChoiceDialog<String> sizeDialog = new ChoiceDialog<>("Normal", "Normal", "Large");
        sizeDialog.setTitle("Add Menu Item");
        sizeDialog.setHeaderText(null);
        sizeDialog.setContentText("Size:");
        String size = sizeDialog.showAndWait().orElse(null);
        if (size == null) return;

        try {
            double price = Double.parseDouble(priceStr);
            String sql = "INSERT INTO public.\"Item\" (name, \"basePrice\", size) VALUES (?, ?, ?)";
            try (Connection conn = MainApp.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setString(3, size);
                ps.executeUpdate();
            }
            loadTable(null);
            MainApp.loadItemCache();
            showAlert("Success", "Menu item added.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void updateMenuItem(ActionEvent event) {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Update Item Price");
        idDialog.setHeaderText(null);
        idDialog.setContentText("Enter Item ID to update:");
        String idStr = idDialog.showAndWait().orElse(null);
        if (idStr == null || idStr.isBlank()) return;

        TextInputDialog priceDialog = new TextInputDialog("0.00");
        priceDialog.setTitle("Update Item Price");
        priceDialog.setHeaderText(null);
        priceDialog.setContentText("New Base Price:");
        String priceStr = priceDialog.showAndWait().orElse(null);
        if (priceStr == null) return;

        try {
            int    id    = Integer.parseInt(idStr);
            double price = Double.parseDouble(priceStr);
            String sql = "UPDATE public.\"Item\" SET \"basePrice\" = ? WHERE \"itemId\" = ?";
            try (Connection conn = MainApp.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, price);
                ps.setInt(2, id);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    showAlert("Not Found", "No item found with ID " + id);
                } else {
                    loadTable(null);
                    MainApp.loadItemCache();
                    showAlert("Success", "Price updated.");
                }
            }
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