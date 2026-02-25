package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
public class CashierController {
    @FXML private Button classicPearlMilkTea, honeyPearlMilkTea;
    @FXML private Button coffeeCreama, coffeeMilkTeaWCoffeeJelly;
    @FXML private Button hokkaidoMilkTea, thaiPearlMilkTea;
    @FXML private Button taroPearlMilkTea, mangoGreenMilkTea;
    @FXML private Button coconutPearlMilkTea, classicTea;
    @FXML private Button honeyTea, mangoGreenTea;
    @FXML private Button berryLycheeJelly, peachTeaWHoneyJelly;
    @FXML private Button mangoNPassionFruitTea, honeyLemonade;
    @FXML private Button strawberryCoconut, haloHalo;
    @FXML private Button wintermelonTea, wintermelonMilkTea;
    @FXML private Button brownSugarPearlMilkTea, brownSugarCoffeeMilkTea;
    @FXML private Button brownSugarHokkaidoMilkTea, brownSugarThaiPearlMilkTea;
    @FXML private Button brownSugarTaroPearlMilkTea, brownSugarMangoGreenMilkTea;
    @FXML private Button brownSugarCoconutPearlMilkTea;
    @FXML private Button orderButton;
    @FXML
    public void initialize() {
        setLabel(classicPearlMilkTea,            1);
        setLabel(honeyPearlMilkTea,              2);
        setLabel(coffeeCreama,                   3);
        setLabel(coffeeMilkTeaWCoffeeJelly,      4);
        setLabel(hokkaidoMilkTea,                5);
        setLabel(thaiPearlMilkTea,               6);
        setLabel(taroPearlMilkTea,               7);
        setLabel(mangoGreenMilkTea,              8);
        setLabel(coconutPearlMilkTea,            9);
        setLabel(classicTea,                    10);
        setLabel(honeyTea,                      11);
        setLabel(mangoGreenTea,                 12);
        setLabel(berryLycheeJelly,              13);
        setLabel(peachTeaWHoneyJelly,           14);
        setLabel(mangoNPassionFruitTea,         15);
        setLabel(honeyLemonade,                 16);
        setLabel(strawberryCoconut,             17);
        setLabel(haloHalo,                      18);
        setLabel(wintermelonTea,                19);
        setLabel(wintermelonMilkTea,            20);
        setLabel(brownSugarPearlMilkTea,        21);
        setLabel(brownSugarCoffeeMilkTea,       22);
        setLabel(brownSugarHokkaidoMilkTea,     23);
        setLabel(brownSugarThaiPearlMilkTea,    24);
        setLabel(brownSugarTaroPearlMilkTea,    25);
        setLabel(brownSugarMangoGreenMilkTea,   26);
        setLabel(brownSugarCoconutPearlMilkTea, 27);
    }
    private void setLabel(Button btn, int itemId) {
        MainApp.Item item = MainApp.itemCache.get(itemId);
        if (item != null) {
            btn.setText(item.name + "\n$" + String.format("%.2f", item.basePrice));
            btn.setWrapText(true);
        }
    }
    private void goToToggleMenu(int itemId) {
        MainApp.selectedItem = MainApp.itemCache.get(itemId);
        Stage stage = (Stage) orderButton.getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/togglemenu.fxml");
    }

    // Button handlers (must match onAction in FXML)
    @FXML void addClassicPearlMilkTea(ActionEvent e)           { goToToggleMenu(1); }
    @FXML void addHoneyPearlMilkTea(ActionEvent e)             { goToToggleMenu(2); }
    @FXML void addCoffeeCreama(ActionEvent e)                  { goToToggleMenu(3); }
    @FXML void addCoffeeMilkTeaWCoffeeJelly(ActionEvent e)     { goToToggleMenu(4); }
    @FXML void addHokkaidoMilkTea(ActionEvent e)               { goToToggleMenu(5); }
    @FXML void addThaiPearlMilkTea(ActionEvent e)              { goToToggleMenu(6); }
    @FXML void addTaroPearlMilkTea(ActionEvent e)              { goToToggleMenu(7); }
    @FXML void addMangoGreenMilkTea(ActionEvent e)             { goToToggleMenu(8); }
    @FXML void addCoconutPearlMilkTea(ActionEvent e)           { goToToggleMenu(9); }
    @FXML void addClassicTea(ActionEvent e)                    { goToToggleMenu(10); }
    @FXML void addHoneyTea(ActionEvent e)                      { goToToggleMenu(11); }
    @FXML void addMangoGreenTea(ActionEvent e)                 { goToToggleMenu(12); }
    @FXML void addBerryLycheeJelly(ActionEvent e)              { goToToggleMenu(13); }
    @FXML void addPeachTeaWHoneyJelly(ActionEvent e)           { goToToggleMenu(14); }
    @FXML void addMangoPassionFruitTea(ActionEvent e)          { goToToggleMenu(15); }
    @FXML void addHoneyLemonade(ActionEvent e)                 { goToToggleMenu(16); }
    @FXML void addStrawberryCoconut(ActionEvent e)             { goToToggleMenu(17); }
    @FXML void addHaloHalo(ActionEvent e)                      { goToToggleMenu(18); }
    @FXML void addWinterMelonTea(ActionEvent e)                { goToToggleMenu(19); }
    @FXML void addWintermelonMilkTea(ActionEvent e)            { goToToggleMenu(20); }
    @FXML void addBrownSugarPearlMilkTea(ActionEvent e)        { goToToggleMenu(21); }
    @FXML void addBrownSugarCoffeeMilkTea(ActionEvent e)       { goToToggleMenu(22); }
    @FXML void addBrownSugarHokkaidoMilkTea(ActionEvent e)     { goToToggleMenu(23); }
    @FXML void addBrownSugarThaiPearlMilkTea(ActionEvent e)    { goToToggleMenu(24); }
    @FXML void addBrownSugarTaroPearlMilkTea(ActionEvent e)    { goToToggleMenu(25); }
    @FXML void addBrownSugarMangoGreenMilkTea(ActionEvent e)   { goToToggleMenu(26); }
    @FXML void addBrownSugarCoconutPearlMilkTea(ActionEvent e) { goToToggleMenu(27); }
    
    // Submit order from cart to Data Base 
    @FXML
    void submitOrder(ActionEvent e) {
        if (MainApp.cart.isEmpty()) {
            alert("Empty Order", "Add at least one drink first.");
            return;
        }
        try {
            int orderId = MainApp.submitCartToDB("ANONYMOUS");
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
