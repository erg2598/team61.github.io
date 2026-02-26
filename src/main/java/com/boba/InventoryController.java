package com.boba;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;

public class InventoryController {

    @FXML
    private Button addInventoryItem;

    @FXML
    private Button editInventoryItem;

    @FXML
    private TextField ingredientType;

    @FXML
    private TextField ingredientType1;

    @FXML
    private TextField itemID;

    @FXML
    private TextField itemID1;

    @FXML
    private TextField itemName;

    @FXML
    private TextField itemName1;

    @FXML
    private TextField itemPrice;

    @FXML
    private TextField itemPrice1;

    @FXML
    private TextField itemQuantity;

    @FXML
    private TextField itemQuantity1;

    @FXML
    private TextField reorderThreshold;

    @FXML
    private TextField reorderThreshold1;

    //Connection to Database
    private static final HikariDataSource ds;
    static {
        Dotenv dotenv = Dotenv.load();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" +
                dotenv.get("DB_HOST") + ":5432/" +
                dotenv.get("DB_NAME"));
        config.setUsername(dotenv.get("DB_USER"));
        config.setPassword(dotenv.get("DB_PASSWORD"));
        config.setMaximumPoolSize(10);
        ds = new HikariDataSource(config);
    }
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

        //Builds Alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void addInventoryItem(ActionEvent event) {
        // Complete
        String name = itemName.getText();
        String ID = itemID.getText();
        String quantity = itemQuantity.getText();
        String price = itemPrice.getText();
        String type = ingredientType.getText();
        String threshold = reorderThreshold.getText();

        String sql = "INSERT INTO public.\"Inventory\"(\"inventoryId\",\"name\",\"quantityOnHand\",\"reorderThreshold\",\"pricePerUnit\",\"type\") VALUES (?,?,?,?,?,?);";
         try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(ID));
            stmt.setString(2, name);
            stmt.setInt(3, Integer.parseInt(quantity));
            stmt.setDouble(5, Double.parseDouble(price));
            stmt.setInt(4, Integer.parseInt(threshold));
            stmt.setString(6, type);
            stmt.executeUpdate();
            
            itemName.clear();
            itemID.clear();
            itemQuantity.clear();
            itemPrice.clear();
            ingredientType.clear();
            reorderThreshold.clear();

            // loadEmployees();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Ingredient added successfully!");
        } catch (SQLException ex){
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Base Error", ex.getMessage());
        }
    }

    @FXML
    void editInventoryItem(ActionEvent event) {
        //TODO
    }

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }

}
