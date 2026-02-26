package com.boba;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
public class ToggleMenuController {
    @FXML private TextField customerNameField;
    @FXML private FlowPane basesPane;
    @FXML private FlowPane toppingsPane;
    @FXML private RadioButton regularSize, largeSize;
    @FXML private RadioButton noIce, lessIce, regularIce;
    @FXML private RadioButton cold, hot;
    @FXML private RadioButton noSugar, sugar25, sugar50, sugar75, regularSugar;
    @FXML private Button addToCart, backButton;
    private final ToggleGroup sizeGroup  = new ToggleGroup();
    private final ToggleGroup iceGroup   = new ToggleGroup();
    private final ToggleGroup tempGroup  = new ToggleGroup();
    private final ToggleGroup sugarGroup = new ToggleGroup();
    private final Map<CheckBox, Integer> baseInventoryId    = new LinkedHashMap<>();
    private final Map<CheckBox, Integer> toppingInventoryId = new LinkedHashMap<>();
    private String selectedIce   = "NONE";
    private String selectedTemp  = "COLD";
    private int    selectedSugar = 100;
    @FXML
    public void initialize() {
        if (MainApp.selectedItem == null) return;
        if (MainApp.currentCustomerName != null && !MainApp.currentCustomerName.isBlank()) {
            customerNameField.setText(MainApp.currentCustomerName);
        }
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
        regularSize.setSelected(true);
        cold.setSelected(true);
        noIce.setSelected(true);
        regularSugar.setSelected(true);
        try {
            List<Integer> preCheckedIds = MainApp.getIngredientInventoryIdsForItem(
                    MainApp.selectedItem.itemId);
            String baseSql = "SELECT \"inventoryId\", name FROM public.\"Inventory\" " +
                             "WHERE \"type\" IN ('Base', 'Flavor') ORDER BY \"inventoryId\"";
            try (Connection conn = MainApp.getConnection();
                 PreparedStatement ps = conn.prepareStatement(baseSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int invId = rs.getInt("inventoryId");
                    String invName = rs.getString("name");
                    CheckBox cb = new CheckBox(invName);
                    cb.setSelected(preCheckedIds.contains(invId));
                    baseInventoryId.put(cb, invId);
                    basesPane.getChildren().add(cb);
                }
            }
            String toppingSql = "SELECT \"inventoryId\", name FROM public.\"Inventory\" " +
                                "WHERE \"type\" = 'Topping' ORDER BY \"inventoryId\"";
            try (Connection conn = MainApp.getConnection();
                 PreparedStatement ps = conn.prepareStatement(toppingSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int invId = rs.getInt("inventoryId");
                    String invName = rs.getString("name");
                    CheckBox cb = new CheckBox(invName);
                    cb.setSelected(preCheckedIds.contains(invId));
                    toppingInventoryId.put(cb, invId);
                    toppingsPane.getChildren().add(cb);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML private void toggleNoIce(ActionEvent e)      { selectedIce = "NONE"; }
    @FXML private void toggleLessIce(ActionEvent e)    { selectedIce = "LESS"; }
    @FXML private void toggleRegularIce(ActionEvent e) { selectedIce = "REGULAR"; }
    @FXML private void toggleCold(ActionEvent e) { selectedTemp = "COLD"; }
    @FXML private void toggleHot(ActionEvent e)  { selectedTemp = "HOT"; }
    @FXML private void toggleNoSugar(ActionEvent e)      { selectedSugar = 0; }
    @FXML private void toggleSugar25(ActionEvent e)      { selectedSugar = 25; }
    @FXML private void toggleSugar50(ActionEvent e)      { selectedSugar = 50; }
    @FXML private void toggleSugar75(ActionEvent e)      { selectedSugar = 75; }
    @FXML private void toggleRegularSugar(ActionEvent e) { selectedSugar = 100; }
    @FXML
    private void addItem(ActionEvent e) {
        if (MainApp.selectedItem == null) return;
        String name = customerNameField.getText();
        if (name != null && !name.isBlank()) {
            MainApp.currentCustomerName = name.trim();
        }
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
        List<Map.Entry<CheckBox, Integer>> selectedToppingEntries = toppingInventoryId.entrySet().stream()
                .filter(ent -> ent.getKey().isSelected())
                .limit(5)
                .collect(Collectors.toList());

        List<String> toppings = selectedToppingEntries.stream()
                .map(ent -> String.valueOf(ent.getValue()))
                .collect(Collectors.toList());

        boolean isLarge = largeSize.isSelected();
double sizeExtra = isLarge ? 2.00 : 0.00;
double baseExtra = 0.0;
if (MainApp.selectedItem.basePrice == 0) {
    for (Map.Entry<CheckBox, Integer> ent : baseInventoryId.entrySet()) {
        if (ent.getKey().isSelected()) {
            try {
                baseExtra += MainApp.getPricePerUnit(ent.getValue());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
double toppingExtra = 0.0;
for (Map.Entry<CheckBox, Integer> ent : selectedToppingEntries) {
    try {
        toppingExtra += MainApp.getPricePerUnit(ent.getValue());
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
double extras = sizeExtra + baseExtra + toppingExtra;
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