package com.boba;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class ToggleMenuController {

    // Bases 
    @FXML private CheckBox milkTea, honey, coffee, thaiMilkTea;
    @FXML private CheckBox taro, mango, coconut, tea;
    @FXML private CheckBox mangoTea, berry, peachTea, passionFruit;
    @FXML private CheckBox lemonade, strawberry, haloHalo, wintermelonTea;

    // Toppings 
    @FXML private CheckBox lychee, pearls, coffeeJelly, pudding;
    @FXML private CheckBox lycheeJelly, honeyJelly, crystalBoba, mangoPoppingBoba;
    @FXML private CheckBox strawbberyPoppingBoba, iceCream, creama;

    // Size
    @FXML private RadioButton regularSize, largeSize;

    // Ice
    @FXML private RadioButton noIce, lessIce, regularIce;

    // Temp
    @FXML private RadioButton cold, hot;

    // Sugar
    @FXML private RadioButton noSugar, sugar25, sugar50, sugar75, regularSugar;

    // Buttons
    @FXML private Button addToCart, backButton;

    private final ToggleGroup sizeGroup  = new ToggleGroup();
    private final ToggleGroup iceGroup   = new ToggleGroup();
    private final ToggleGroup tempGroup  = new ToggleGroup();
    private final ToggleGroup sugarGroup = new ToggleGroup();

    // InventoryId maps
    private final Map<CheckBox, Integer> baseInventoryId    = new LinkedHashMap<>();
    private final Map<CheckBox, Integer> toppingInventoryId = new LinkedHashMap<>();

    private String selectedIce  = "NONE";
    private String selectedTemp = "COLD";
    private int    selectedSugar = 100;

    @FXML
    public void initialize() {
        if (MainApp.selectedItem == null) return;

        // Base correlations to  inventoryIds
        baseInventoryId.put(milkTea,        1);
        baseInventoryId.put(honey,          2);
        baseInventoryId.put(coffee,         3);
        baseInventoryId.put(thaiMilkTea,    4);
        baseInventoryId.put(taro,           5);
        baseInventoryId.put(mango,          6);
        baseInventoryId.put(coconut,        7);
        baseInventoryId.put(tea,            8);
        baseInventoryId.put(mangoTea,       9);
        baseInventoryId.put(berry,         10);
        baseInventoryId.put(peachTea,      11);
        baseInventoryId.put(passionFruit,  12);
        baseInventoryId.put(lemonade,      13);
        baseInventoryId.put(strawberry,    14);
        baseInventoryId.put(haloHalo,      15);
        baseInventoryId.put(wintermelonTea,16);

        // Toppings correlation to inventoryIds
        toppingInventoryId.put(lychee,                 17);
        toppingInventoryId.put(pearls,                 18);
        toppingInventoryId.put(coffeeJelly,            19);
        toppingInventoryId.put(pudding,                20);
        toppingInventoryId.put(lycheeJelly,            21);
        toppingInventoryId.put(honeyJelly,             22);
        toppingInventoryId.put(crystalBoba,            23);
        toppingInventoryId.put(mangoPoppingBoba,       24);
        toppingInventoryId.put(strawbberyPoppingBoba,  25);
        toppingInventoryId.put(iceCream,               26);
        toppingInventoryId.put(creama,                 27);

        // Wire toggle groups
        regularSize.setToggleGroup(sizeGroup);
        largeSize.setToggleGroup(sizeGroup);
        noIce.setToggleGroup(iceGroup);
        lessIce.setToggleGroup(iceGroup);
        regularIce.setToggleGroup(iceGroup);
        cold.setToggleGroup(tempGroup);
        hot.setToggleGroup(tempGroup);
        noSugar.setToggleGroup(sugarGroup);
        sugar25.setToggleGroup(sugarGroup);
        sugar50.setToggleGroup(sugarGroup);
        sugar75.setToggleGroup(sugarGroup);
        regularSugar.setToggleGroup(sugarGroup);

        // Defaults
        regularSize.setSelected(true);
        cold.setSelected(true);
        noIce.setSelected(true);
        regularSugar.setSelected(true);

        // Pre-check bases and toppings based on recipe ingredients
        try {
            List<Integer> ids = MainApp.getIngredientInventoryIdsForItem(MainApp.selectedItem.itemId);
            baseInventoryId.forEach((cb, invId)    -> cb.setSelected(ids.contains(invId)));
            toppingInventoryId.forEach((cb, invId) -> cb.setSelected(ids.contains(invId)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ice toggles
    @FXML private void toggleNoIce(ActionEvent e)      { selectedIce = "NONE"; }
    @FXML private void toggleLessIce(ActionEvent e)    { selectedIce = "LESS"; }
    @FXML private void toggleRegularIce(ActionEvent e) { selectedIce = "REGULAR"; }

    // Temp toggles
    @FXML private void toggleCold(ActionEvent e) { selectedTemp = "COLD"; }
    @FXML private void toggleHot(ActionEvent e)  { selectedTemp = "HOT"; }

    // Sugar toggles
    @FXML private void toggleNoSugar(ActionEvent e)      { selectedSugar = 0; }
    @FXML private void toggleSugar25(ActionEvent e)      { selectedSugar = 25; }
    @FXML private void toggleSugar50(ActionEvent e)      { selectedSugar = 50; }
    @FXML private void toggleSugar75(ActionEvent e)      { selectedSugar = 75; }
    @FXML private void toggleRegularSugar(ActionEvent e) { selectedSugar = 100; }
    // Add current config to cart
    @FXML
    private void addItem(ActionEvent e) {
        if (MainApp.selectedItem == null) return;
        // Collect all checked bases as a comma-separated string
        String baseType = baseInventoryId.entrySet().stream()
                .filter(ent -> ent.getKey().isSelected())
                .map(ent -> ent.getKey().getText())
                .collect(Collectors.joining(", "));

        if (baseType.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one base.");
            alert.showAndWait();
            return;
        }
        // Collect selected toppings (up to 5)
        List<String> toppings = toppingInventoryId.entrySet().stream()
                .filter(ent -> ent.getKey().isSelected())
                .map(ent -> String.valueOf(ent.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        // Price calculation
        boolean isLarge   = largeSize.isSelected();
        double sizeExtra  = isLarge ? 2.00 : 0.00;
        double toppingExtra = toppings.size() * 0.50;
        double extras     = sizeExtra + toppingExtra;
        double finalPrice = MainApp.selectedItem.basePrice + extras;
        MainApp.OrderLineItem line = new MainApp.OrderLineItem(
                MainApp.selectedItem.itemId,
                MainApp.selectedItem.name,
                isLarge ? "Large" : "Normal",
                baseType,
                selectedIce,
                selectedTemp,
                selectedSugar,
                extras,
                toppings,
                finalPrice
        );
        MainApp.addToCart(line);
        goBack();
    }
    @FXML
    private void backToMenu(ActionEvent e) {
        goBack();
    }
    private void goBack() {
        Stage stage = (Stage) addToCart.getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/cashierview.fxml");
    }
}
