package com.boba;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * This class contains all of the logic for
 *actions by cashiers.
 * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
 */
public class CashierController {
    @FXML private FlowPane menuGrid;
    @FXML private Button orderButton;
    @FXML
    public void initialize() {
        buildMenuButtons();
    }
    public void buildMenuButtons() {
        menuGrid.getChildren().clear();
        menuGrid.setHgap(8);
        menuGrid.setVgap(8);
        menuGrid.setPadding(new Insets(10));
        for (MainApp.Item item : MainApp.itemCache.values()) {
            if (!item.enabled) continue;
            Button btn = new Button(item.name + "\n$" + String.format("%.2f", item.basePrice));
            btn.setWrapText(true);
            btn.setPrefWidth(140);
            btn.setPrefHeight(70);
            btn.getStyleClass().add("menu-item-button");
            final int id = item.itemId;
            btn.setOnAction(e -> goToToggleMenu(id));
            menuGrid.getChildren().add(btn);
        }
    }
    private void goToToggleMenu(int itemId) {
        MainApp.selectedItem = MainApp.itemCache.get(itemId);
        Stage stage = (Stage) orderButton.getScene().getWindow();
        
        MainApp.switchScene(stage, "/fxml/togglemenu.fxml");
    }
    @FXML
    void submitOrder(ActionEvent e) {
        if (MainApp.cart.isEmpty()) {
            alert("Empty Order", "Add at least one drink first.");
            return;
        }
        try {
            int orderId = MainApp.submitCartToDB(MainApp.currentCustomerName);
            alert("Order Submitted",
                    "Order #" + orderId + " submitted.\nTotal: $" + String.format("%.2f", MainApp.cartTotal));
            MainApp.clearCart();
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("DB Error", ex.getMessage());
        }
    }
    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
    private void goToManagerView(ActionEvent e) {
        Stage stage = (Stage) orderButton.getScene().getWindow();
        stage.setTitle("Manager View");
        MainApp.switchScene(stage, "/fxml/ManagerView.fxml");
    }
    @FXML
    private void goToCustomItemMenu(ActionEvent e) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Not implemented");
        a.setHeaderText(null);
        a.setContentText("Custom Item screen not implemented yet.");
        a.showAndWait();
    }
} 