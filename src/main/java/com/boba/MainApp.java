package com.boba;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
public class MainApp extends Application {
    // DB POOL (Hikari) - allows more stable connection? 
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

    // SHARED STATE
    public static class Item {
        public final int itemId;
        public final String name;
        public final double basePrice;
        public final String size;

        public Item(int itemId, String name, double basePrice, String size) {
            this.itemId = itemId;
            this.name = name;
            this.basePrice = basePrice;
            this.size = size;
        }
    }
    public static class OrderLineItem {
        public final int itemId;
        public final String drinkName;
        public final String size;
        public final String baseType;
        public final String iceLevel;
        public final String temperature;
        public final int sugarAmount;
        public final double extras;
        public final List<String> toppings; 
        public final double price;          
        public OrderLineItem(int itemId, String drinkName, String size,
                             String baseType, String iceLevel, String temperature,
                             int sugarAmount, double extras, List<String> toppings,
                             double price) {
            this.itemId = itemId;
            this.drinkName = drinkName;
            this.size = size;
            this.baseType = baseType;
            this.iceLevel = iceLevel;
            this.temperature = temperature;
            this.sugarAmount = sugarAmount;
            this.extras = extras;
            this.toppings = toppings;
            this.price = price;
        }
    }
    public static Item selectedItem = null;
    public static final List<OrderLineItem> cart = new ArrayList<>();
    public static double cartTotal = 0.0;

    // cache menu items (normal size) 
    public static final Map<Integer, Item> itemCache = new HashMap<>();
    public static void addToCart(OrderLineItem line) {
        cart.add(line);
        cartTotal += line.price;
    }
    public static void clearCart() {
        cart.clear();
        cartTotal = 0.0;
    }

    // SCENE SWITCH HELPER
    public static void switchScene(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scene Load Error");
            alert.setHeaderText("Failed to load: " + fxmlPath);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // DataBase QUERIES

    // Load menu items into cache (Normal, since your cashier buttons show Normal)
    public static void loadItemCache() throws SQLException {
        itemCache.clear();
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

    // InventoryIds that belong to this menu item (from Ingredients)
    public static List<Integer> getIngredientInventoryIdsForItem(int itemId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT \"inventoryId\" FROM public.\"Ingredients\" WHERE \"itemId\" = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("inventoryId"));
            }
        }
        return ids;
    }

    // INSERT Order as well as OrderLineItem rows
    public static int submitCartToDB(String customerName) throws SQLException {
        if (cart.isEmpty()) throw new SQLException("Cart is empty.");

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {

                // Insert Order
                String orderSql =
                        "INSERT INTO public.\"Order\" (\"customerName\", status, \"totalAmount\") " +
                        "VALUES (?, 'NOT READY', ?) RETURNING \"orderId\"";

                int orderId;
                try (PreparedStatement stmt = conn.prepareStatement(orderSql)) {
                    stmt.setString(1, (customerName == null || customerName.isBlank()) ? "ANONYMOUS" : customerName);
                    stmt.setDouble(2, cartTotal);
                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        orderId = rs.getInt("orderId");
                    }
                }

                // Insert line items
                String lineSql =
                        "INSERT INTO public.\"OrderLineItem\" " +
                        "(\"orderId\", \"itemId\", \"quantity\", \"sugarAmount\", \"iceLevel\", \"temperature\", \"baseType\", \"extras\", " +
                        "\"topping1\", \"topping2\", \"topping3\", \"topping4\", \"topping5\") " +
                        "VALUES (?, ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(lineSql)) {
                    for (OrderLineItem li : cart) {
                        List<String> t = new ArrayList<>(li.toppings);
                        while (t.size() < 5) t.add(null); // pad to 5
                        stmt.setInt(1, orderId);
                        stmt.setInt(2, li.itemId);
                        stmt.setInt(3, li.sugarAmount);
                        stmt.setString(4, li.iceLevel);
                        stmt.setString(5, li.temperature);
                        stmt.setString(6, li.baseType);
                        stmt.setDouble(7, li.extras);
                        stmt.setString(8,  t.get(0));
                        stmt.setString(9,  t.get(1));
                        stmt.setString(10, t.get(2));
                        stmt.setString(11, t.get(3));
                        stmt.setString(12, t.get(4));
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
            }
        }
    }

    // PULL - price per unit from the database for toppings
    public static double getPricePerUnit(int inventoryId) throws SQLException {
    String sql = "SELECT \"pricePerUnit\" FROM public.\"Inventory\" WHERE \"inventoryId\" = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, inventoryId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("pricePerUnit");
        }
    }
    return 0.0;
}

    // APP ENTRY - Jack can change once entry is set 
    @Override
    public void start(Stage stage) throws Exception {
        loadItemCache();
        switchScene(stage, "/fxml/MainPage.fxml");
        stage.setTitle("Boba POS");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}