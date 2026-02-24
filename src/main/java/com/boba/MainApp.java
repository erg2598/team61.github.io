package com.boba;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class MainApp extends Application {

    
    // DATABASE
    
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

    
    // MODELS
    
    public static class Item {
        public int    itemId;
        public String name;
        public double basePrice;
        public String size;

        public Item(int id, String name, double price, String size) {
            this.itemId    = id;
            this.name      = name;
            this.basePrice = price;
            this.size      = size;
        }
    }

    public static class OrderLineItem {
        public int           itemId;
        public String        size;
        public String        base;
        public List<Integer> toppingIds;
        public String        iceLevel;
        public String        temperature;
        public int           sugarLevel;
        public double        price;
        public String        drinkName;

        public OrderLineItem(int itemId, String drinkName, String size,
                             String base, List<Integer> toppingIds,
                             String iceLevel, String temperature,
                             int sugarLevel, double price) {
            this.itemId      = itemId;
            this.drinkName   = drinkName;
            this.size        = size;
            this.base        = base;
            this.toppingIds  = toppingIds;
            this.iceLevel    = iceLevel;
            this.temperature = temperature;
            this.sugarLevel  = sugarLevel;
            this.price       = price;
        }
    }

    
    // SHARED STATE
    
    public static Item                selectedItem = null;
    public static String              selectedSize = "Regular";
    public static List<OrderLineItem> currentOrder = new ArrayList<>();
    public static double              orderTotal   = 0.0;
    public static Map<Integer, Item>  itemCache    = new HashMap<>();

    public static void addToOrder(OrderLineItem line) {
        currentOrder.add(line);
        orderTotal += line.price;
    }

    public static void clearOrder() {
        currentOrder.clear();
        orderTotal = 0.0;
    }

    
    // DATABASE QUERIES
    
    public static void loadItemCache() throws SQLException {
        String sql = "SELECT \"itemId\", name, \"basePrice\", size " +
                     "FROM public.\"Item\" WHERE size = 'Normal' ORDER BY \"itemId\"";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Item item = new Item(
                    rs.getInt("itemId"),
                    rs.getString("name"),
                    rs.getDouble("basePrice"),
                    rs.getString("size")
                );
                itemCache.put(item.itemId, item);
            }
        }
    }

    public static List<Integer> getIngredientIds(int itemId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT \"inventoryId\" FROM public.\"Ingredients\" " +
                     "WHERE \"itemId\" = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("inventoryId"));
            }
        }
        return ids;
    }

    public static int submitOrderToDB(String customerName) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

            String orderSQL = "INSERT INTO public.\"Order\" " +
                              "(\"customerName\", status, \"totalAmount\") " +
                              "VALUES (?, 'NOT READY', ?) RETURNING \"orderId\"";
            int orderId;
            try (PreparedStatement stmt = conn.prepareStatement(orderSQL)) {
                stmt.setString(1, customerName.isEmpty() ? "ANONYMOUS" : customerName);
                stmt.setDouble(2, orderTotal);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                orderId = rs.getInt("orderId");
            }

            String lineSQL = "INSERT INTO public.\"OrderLineItem\" " +
                             "(\"orderId\", \"itemId\", quantity, \"iceLevel\", \"sugarAmount\") " +
                             "VALUES (?, ?, 1, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(lineSQL)) {
                for (OrderLineItem line : currentOrder) {
                    stmt.setInt(1, orderId);
                    stmt.setInt(2, line.itemId);
                    stmt.setString(3, line.iceLevel);
                    stmt.setInt(4, line.sugarLevel);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    
    // APP ENTRY POINT
    
    @Override
    public void start(Stage stage) throws Exception {
        loadItemCache();
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/cashierview.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Boba POS");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    
    // CASHIER CONTROLLER
    
    public static class CashierController {

        @FXML private VBox background;
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
        @FXML private Button customitem, orderButton;

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
            Item item = itemCache.get(itemId);
            if (item != null) {
                btn.setText(item.name + "\n$" +
                            String.format("%.2f", item.basePrice));
                btn.setWrapText(true);
            }
        }

        private void openCustomization(int itemId) {
            selectedItem = itemCache.get(itemId);
            selectedSize = "Regular";
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/togglemenu.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) orderButton.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @FXML void addClassicPearlMilkTea(ActionEvent e)           { openCustomization(1);  }
        @FXML void addHoneyPearlMilkTea(ActionEvent e)             { openCustomization(2);  }
        @FXML void addCoffeeCreama(ActionEvent e)                   { openCustomization(3);  }
        @FXML void addCoffeeMilkTeaWCoffeeJelly(ActionEvent e)     { openCustomization(4);  }
        @FXML void addHokkaidoMilkTea(ActionEvent e)                { openCustomization(5);  }
        @FXML void addThaiPearlMilkTea(ActionEvent e)              { openCustomization(6);  }
        @FXML void addTaroPearlMilkTea(ActionEvent e)              { openCustomization(7);  }
        @FXML void addMangoGreenMilkTea(ActionEvent e)             { openCustomization(8);  }
        @FXML void addCoconutPearlMilkTea(ActionEvent e)           { openCustomization(9);  }
        @FXML void addClassicTea(ActionEvent e)                    { openCustomization(10); }
        @FXML void addHoneyTea(ActionEvent e)                      { openCustomization(11); }
        @FXML void addMangoGreenTea(ActionEvent e)                 { openCustomization(12); }
        @FXML void addBerryLycheeJelly(ActionEvent e)              { openCustomization(13); }
        @FXML void addPeachTeaWHoneyJelly(ActionEvent e)           { openCustomization(14); }
        @FXML void addMangoPassionFruitTea(ActionEvent e)          { openCustomization(15); }
        @FXML void addHoneyLemonade(ActionEvent e)                 { openCustomization(16); }
        @FXML void addStrawberryCoconut(ActionEvent e)             { openCustomization(17); }
        @FXML void addHaloHalo(ActionEvent e)                      { openCustomization(18); }
        @FXML void addWinterMelonTea(ActionEvent e)                { openCustomization(19); }
        @FXML void addWintermelonMilkTea(ActionEvent e)            { openCustomization(20); }
        @FXML void addBrownSugarPearlMilkTea(ActionEvent e)        { openCustomization(21); }
        @FXML void addBrownSugarCoffeeMilkTea(ActionEvent e)       { openCustomization(22); }
        @FXML void addBrownSugarHokkaidoMilkTea(ActionEvent e)     { openCustomization(23); }
        @FXML void addBrownSugarThaiPearlMilkTea(ActionEvent e)    { openCustomization(24); }
        @FXML void addBrownSugarTaroPearlMilkTea(ActionEvent e)    { openCustomization(25); }
        @FXML void addBrownSugarMangoGreenMilkTea(ActionEvent e)   { openCustomization(26); }
        @FXML void addBrownSugarCoconutPearlMilkTea(ActionEvent e) { openCustomization(27); }

        @FXML
        void submitOrder(ActionEvent event) {
            if (currentOrder.isEmpty()) {
                showAlert("Empty Order", "Add at least one item first.");
                return;
            }
            try {
                int orderId = submitOrderToDB("ANONYMOUS");
                showAlert("Order Placed",
                          "Order #" + orderId + " submitted! Total: $" +
                          String.format("%.2f", orderTotal));
                clearOrder();
            } catch (SQLException e) {
                showAlert("DB Error", "Order failed: " + e.getMessage());
            }
        }

        @FXML void goToCustomItemMenu(ActionEvent event) { }

        private void showAlert(String title, String msg) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(msg);
            alert.showAndWait();
        }
    }

    
    // CUSTOMIZATION CONTROLLER
    // matches togglemenu.fxml fx:id names exactly
    
    public static class CustomizationController {

        // Base radio buttons — match fx:id in togglemenu.fxml
        @FXML private RadioButton milkTea, honey, coffee, thaiMilkTea;
        @FXML private RadioButton taro, mango, coconut, tea;
        @FXML private RadioButton mangoTea, berry, peachTea, passionFruit;
        @FXML private RadioButton lemonade, strawberry, haloHalo, wintermelonTea;

        // Topping radio buttons — match fx:id in togglemenu.fxml
        @FXML private RadioButton lychee, pearls, coffeeJelly, pudding;
        @FXML private RadioButton lycheeJelly, honeyJelly, crystalBoba, mangoPoppingBoba;
        @FXML private RadioButton strawbberyPoppingBoba, iceCream, creama;

        // Size
        @FXML private RadioButton regularSize, largeSize;

        // Ice
        @FXML private RadioButton noIce, lessIce, regularIce;

        // Temperature
        @FXML private RadioButton cold, hot;

        // Sugar
        @FXML private RadioButton noSugar, sugar25, sugar50, sugar75, regularSugar;

        // Buttons
        @FXML private Button addToCart, backButton;

        // Toggle groups
        private final ToggleGroup baseGroup  = new ToggleGroup();
        private final ToggleGroup sizeGroup  = new ToggleGroup();
        private final ToggleGroup iceGroup   = new ToggleGroup();
        private final ToggleGroup tempGroup  = new ToggleGroup();
        private final ToggleGroup sugarGroup = new ToggleGroup();

        private final Map<RadioButton, Integer> baseMap    = new LinkedHashMap<>();
        private final Map<RadioButton, Integer> toppingMap = new LinkedHashMap<>();

        // Current selections
        private String selectedIce   = "NONE";
        private String selectedTemp  = "COLD";
        private int    selectedSugar = 0;

        @FXML
        public void initialize() {
            if (selectedItem == null) return;

            // Map bases to inventoryIds
            baseMap.put(milkTea,       1);  baseMap.put(honey,        2);
            baseMap.put(coffee,        3);  baseMap.put(thaiMilkTea,  4);
            baseMap.put(taro,          5);  baseMap.put(mango,        6);
            baseMap.put(coconut,       7);  baseMap.put(tea,          8);
            baseMap.put(mangoTea,      9);  baseMap.put(berry,       10);
            baseMap.put(peachTea,     11);  baseMap.put(passionFruit,12);
            baseMap.put(lemonade,     13);  baseMap.put(strawberry,  14);
            baseMap.put(haloHalo,     15);  baseMap.put(wintermelonTea, 16);
            baseMap.keySet().forEach(rb -> rb.setToggleGroup(baseGroup));

            // Map toppings to inventoryIds
            toppingMap.put(lychee,               17);
            toppingMap.put(pearls,               18);
            toppingMap.put(coffeeJelly,          19);
            toppingMap.put(pudding,              20);
            toppingMap.put(lycheeJelly,          21);
            toppingMap.put(honeyJelly,           22);
            toppingMap.put(crystalBoba,          23);
            toppingMap.put(mangoPoppingBoba,     24);
            toppingMap.put(strawbberyPoppingBoba,25);
            toppingMap.put(iceCream,             26);
            toppingMap.put(creama,               27);
            // Toppings NOT in a group so multiple can be selected

            // Wire size group
            regularSize.setToggleGroup(sizeGroup);
            largeSize.setToggleGroup(sizeGroup);

            // Wire ice group
            noIce.setToggleGroup(iceGroup);
            lessIce.setToggleGroup(iceGroup);
            regularIce.setToggleGroup(iceGroup);

            // Wire temperature group
            cold.setToggleGroup(tempGroup);
            hot.setToggleGroup(tempGroup);

            // Wire sugar group
            noSugar.setToggleGroup(sugarGroup);
            sugar25.setToggleGroup(sugarGroup);
            sugar50.setToggleGroup(sugarGroup);
            sugar75.setToggleGroup(sugarGroup);
            regularSugar.setToggleGroup(sugarGroup);

            // Pre-tick ingredients that come with this drink
            try {
                List<Integer> ids = getIngredientIds(selectedItem.itemId);
                baseMap.forEach((rb, id) -> {
                    if (ids.contains(id)) rb.setSelected(true);
                });
                toppingMap.forEach((rb, id) -> {
                    if (ids.contains(id)) rb.setSelected(true);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // ── Each toggle method matches onAction in togglemenu.fxml ──

        @FXML private void toggleMilkTea(ActionEvent e)      { }
        @FXML private void toggleHoney(ActionEvent e)         { }
        @FXML private void toggleCoffee(ActionEvent e)        { }
        @FXML private void toggleThaiMilkTea(ActionEvent e)   { }
        @FXML private void toggleTaro(ActionEvent e)          { }
        @FXML private void toggleMango(ActionEvent e)         { }
        @FXML private void toggleCoconut(ActionEvent e)       { }
        @FXML private void toggleTea(ActionEvent e)           { }
        @FXML private void toggleMangoTea(ActionEvent e)      { }
        @FXML private void toggleBerry(ActionEvent e)         { }
        @FXML private void togglePeachTea(ActionEvent e)      { }
        @FXML private void togglePassionFruit(ActionEvent e)  { }
        @FXML private void toggleLemonade(ActionEvent e)      { }
        @FXML private void toggleStrawberry(ActionEvent e)    { }
        @FXML private void toggleHaloHalo(ActionEvent e)      { }
        @FXML private void toggleWintermelonTea(ActionEvent e){ }

        @FXML private void toggleLychee(ActionEvent e)               { }
        @FXML private void togglePearls(ActionEvent e)               { }
        @FXML private void toggkeCoffeeJelly(ActionEvent e)          { } // typo in your FXML kept intentionally
        @FXML private void togglePudding(ActionEvent e)              { }
        @FXML private void toggleLycheeJelly(ActionEvent e)          { }
        @FXML private void toggleHoneyJelly(ActionEvent e)           { }
        @FXML private void toggleCrystalBoba(ActionEvent e)          { }
        @FXML private void toggleMangoPoppingBoba(ActionEvent e)     { }
        @FXML private void toggleStrawberryPoppingBoba(ActionEvent e){ }
        @FXML private void toggleIceCream(ActionEvent e)             { }
        @FXML private void toggleCreama(ActionEvent e)               { }

        @FXML private void toggleRegularSize(ActionEvent e) {
            selectedSize = "Regular";
        }
        @FXML private void toggleLarge(ActionEvent e) {
            selectedSize = "Large";
        }

        @FXML private void toggleNoIce(ActionEvent e)      { selectedIce = "NONE";    }
        @FXML private void toggleLessIce(ActionEvent e)    { selectedIce = "LESS";    }
        @FXML private void toggleRegularIce(ActionEvent e) { selectedIce = "REGULAR"; }

        @FXML private void toggleCold(ActionEvent e) { selectedTemp = "COLD"; }
        @FXML private void toggleHot(ActionEvent e)  { selectedTemp = "HOT";  }

        @FXML private void toggleNoSugar(ActionEvent e)      { selectedSugar = 0;   }
        @FXML private void toggleSugar25(ActionEvent e)      { selectedSugar = 25;  }
        @FXML private void toggleSugar50(ActionEvent e)      { selectedSugar = 50;  }
        @FXML private void toggleSugar75(ActionEvent e)      { selectedSugar = 75;  }
        @FXML private void toggleRegularSugar(ActionEvent e) { selectedSugar = 100; }

        // Add to cart — packages everything and adds to order
        @FXML
        private void addItem(ActionEvent e) {
            List<Integer> chosenToppings = new ArrayList<>();
            toppingMap.forEach((rb, id) -> {
                if (rb.isSelected()) chosenToppings.add(id);
            });

            RadioButton baseBtn = (RadioButton) baseGroup.getSelectedToggle();
            String baseName  = baseBtn != null ? baseBtn.getText() : "None";
            String size      = largeSize.isSelected() ? "Large" : "Regular";
            double sizeExtra = largeSize.isSelected() ? 2.00 : 0.00;
            double finalPrice = selectedItem.basePrice +
                                (chosenToppings.size() * 0.50) + sizeExtra;

            OrderLineItem line = new OrderLineItem(
                selectedItem.itemId, selectedItem.name,
                size, baseName, chosenToppings,
                selectedIce, selectedTemp,
                selectedSugar, finalPrice
            );
            addToOrder(line);

            System.out.println("Added: "     + selectedItem.name +
                               " | Base: "   + baseName +
                               " | Size: "   + size +
                               " | Temp: "   + selectedTemp +
                               " | Ice: "    + selectedIce +
                               " | Sugar: "  + selectedSugar + "%" +
                               " | Price: $" + String.format("%.2f", finalPrice));

            // Go back to cashier screen
            goBack();
        }

        // Back button — goes back without adding to order
        @FXML
        private void backToMenu(ActionEvent e) {
            goBack();
        }

        private void goBack() {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/cashierview.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) addToCart.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}