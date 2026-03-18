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
 * This class contains all of the logic for
 * the manager to be edit and toggle menu items and their prices.
 * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
 */
public class ItemPriceController {
    /**
     * This class does not have a constructor.
     */
    public ItemPriceController(){}
    @FXML private TableView<ItemRow> itemTable;
    @FXML private TableColumn<ItemRow, Integer> idCol;
    @FXML private TableColumn<ItemRow, String>  nameCol;
    @FXML private TableColumn<ItemRow, Double>  priceCol;
    @FXML private TableColumn<ItemRow, String>  sizeCol;
    @FXML private TableColumn<ItemRow, Boolean> enabledCol;  

    /**
     * This is a nested class in the ItemPriceController.
     * It contains the values of each row in the item table.
     * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
     */
    public static class ItemRow {
        private final IntegerProperty itemId;
        private final StringProperty  name;
        private final DoubleProperty  price;
        private final StringProperty  size;
        private final BooleanProperty enabled;  // ← new field
        /**
         * The constructor for each row in the item table.
         * @param id The ID of the item.
         * @param name The name of the item.
         * @param price The price of the item.
         * @param size The size of the item.
         * @param enabled Whether the item is currently enabled.
         */
        public ItemRow(int id, String name, double price, String size, boolean enabled) {
            this.itemId  = new SimpleIntegerProperty(id);
            this.name    = new SimpleStringProperty(name);
            this.price   = new SimpleDoubleProperty(price);
            this.size    = new SimpleStringProperty(size);
            this.enabled = new SimpleBooleanProperty(enabled);  // ← new
        }
    }

    
    /**
     * Initializes the item table for each column and loading the table data.
     */
    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c      -> c.getValue().itemId.asObject());
        nameCol.setCellValueFactory(c    -> c.getValue().name);
        priceCol.setCellValueFactory(c   -> c.getValue().price.asObject());
        sizeCol.setCellValueFactory(c    -> c.getValue().size);
        enabledCol.setCellValueFactory(c -> c.getValue().enabled.asObject());  // ← new
        loadTable(null);
    }

    
    /**
     * Loads all menu items from the database and displays them in the table.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void loadTable(ActionEvent event) {
        ObservableList<ItemRow> rows = FXCollections.observableArrayList();
        String sql = "SELECT \"itemId\", name, \"basePrice\", size, enabled " +
                     "FROM public.\"Item\" ORDER BY \"itemId\"";
        try (Connection conn = MainApp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new ItemRow(
                        rs.getInt("itemId"),
                        rs.getString("name"),
                        rs.getDouble("basePrice"),
                        rs.getString("size"),
                        rs.getBoolean("enabled")));  // ← new
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemTable.setItems(rows);
    }

    
    /**
     * Adds a new menu item to the database.
     * Allows input for a name, base price, and size.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void addMenuItem(ActionEvent event) {
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
            String sql = "INSERT INTO public.\"Item\" (name, \"basePrice\", size, enabled) " +
                         "VALUES (?, ?, ?, true)";
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

    
    /**
     * Updates the base price of an existing item
     * appends the item ID and new price. 
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void updateMenuItem(ActionEvent event) {
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

    
    /**
     * Toggles the enabled status of a menu item in the database.
     * @param event This function triggers when a button is pressed.
     */
    @FXML
    public void toggleMenuItem(ActionEvent event) {
        ItemRow selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Click on a row first, then press Toggle.");
            return;
        }
        boolean newStatus = !selected.enabled.get();
        String sql = "UPDATE public.\"Item\" SET enabled = ? WHERE \"itemId\" = ?";
        try (Connection conn = MainApp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, newStatus);
            ps.setInt(2, selected.itemId.get());
            ps.executeUpdate();
            loadTable(null);
            showAlert("Success", "Item " + selected.name.get() +
                      " is now " + (newStatus ? "ENABLED" : "DISABLED"));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }

    
    /**
     * Returns to the previous page. 
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
        a.showAndWait();
    }
}