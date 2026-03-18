package com.boba;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * This is the main class of our app. It connects to the database and launches the main view.
 */
public class MainApp extends Application {
    /**
     * This class does not have a constructor.
     */
    public MainApp(){}
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
    /**
     * Returns the connection to the database.
     * @return Returns the connection to the database.
     * @throws SQLException Throws an exception when the app is unable to connect to the database.
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    // SHARED STATE
    /**
     * A nested class within the main app. It is used for sending default menu items for each order to the database.
     */
    public static class Item {
        private final int itemId;
        private final String name;
        private final double basePrice;
        private final String size;
        private final boolean enabled;
        /**
         * The constructor for the item class.
         * @param itemId The item's ID.
         * @param name The item's name.
         * @param basePrice The item's default price.
         * @param size The item's size, regular or large.
         * @param enabled A boolean that determines whether the item is able to be sold at the time. It's useful for tracking seasonal or discontinued menu items.
         *
         */
        public Item(int itemId, String name, double basePrice, String size, boolean enabled) {
            this.itemId = itemId;
            this.name = name;
            this.basePrice = basePrice;
            this.size = size;
            this.enabled = enabled;
        }
    }
    /**
     * A nested class within the main app. It is used for sending items that have been customized to the database.
     */
    public static class OrderLineItem {
        private final int itemId;
        private final String drinkName;
        private final String size;
        private final String baseType;
        private final String iceLevel;
        private final String temperature;
        private final int sugarAmount;
        private final double extras;
        private final List<String> toppings; 
        private final double price;
        /**
         * The constructor for each item in an order.
         * @param itemId The item's ID.
         * @param drinkName The name of the item.
         * @param size The size of the item.
         * @param baseType The liquid base of the item.
         * @param iceLevel How much ice the drink has.
         * @param temperature The temperature of the drink, either hot or cold.
         * @param sugarAmount The sugar amount of the drink.
         * @param extras Any extra add ins in the drink.
         * @param toppings Any extra toppings in the drink.
         * @param price The price of the drink after any extra costs.
         */      
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
    private static Item selectedItem = null;
    private static final List<OrderLineItem> cart = new ArrayList<>();
    private static double cartTotal = 0.0;
    private static String currentCustomerName = "";
    private static final Map<Integer, Item> itemCache = new LinkedHashMap<>();
    /**
     * This function adds an item to the cart, readying it to be added to the database in an order.
     * @param line This function takes in an OrderLineItem.
     */
    public static void addToCart(OrderLineItem line) {
        cart.add(line);
        cartTotal += line.price;
    }
    /**
     * This functions removes any items that are in the cart.
     */
    public static void clearCart() {
        cart.clear();
        cartTotal = 0.0;
        currentCustomerName = "";
    }

    // SCENE SWITCH HELPER
    /**
     * This functions switches the scene of the app.
     * @param stage The current view.
     * @param fxmlPath The file path of the new view to switch to.
     */
    public static void switchScene(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
            } else {
                stage.getScene().setRoot(root);
            }
                
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
    /**
     * This functions loads the menu items from the database.
     */
    public static void loadItemCache() throws SQLException {
        itemCache.clear();
        String sql = "SELECT \"itemId\", name, \"basePrice\", size, enabled " +
                "FROM public.\"Item\" WHERE enabled = TRUE AND size = 'Normal' ORDER BY \"itemId\"";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("itemId"),
                        rs.getString("name"),
                        rs.getDouble("basePrice"),
                        rs.getString("size"),
                        rs.getBoolean("enabled")
                );
                itemCache.put(item.itemId, item);
            }
        }
    }
    /**
     * This function searches the database for an item ID.
     * @param itemId The item ID to search for
     * @return The function returns an array of items IDs.
     * @throws SQLException This function throws an exception when the database connection fails.
     */
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
    /**
     * This functions adds items in the database.
     * @param name The name of the item.
     * @param normalPrice The price of the item.
     * @param ingredientIds A list of ingredient IDs of the ingredients that make up the item.
     * @param quantityUsed The quantity of each item that has been used.
     * @return This function returns an integer.
     * @throws SQLException This function throws an exception when the database connection fails.
     */
    public static int addItemToDB(String name, double normalPrice,
                                   List<Integer> ingredientIds,
                                   double quantityUsed) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                String itemSql =
                    "INSERT INTO public.\"Item\" (name, \"basePrice\", size, enabled) " +
                    "VALUES (?, ?, ?, TRUE) RETURNING \"itemId\"";

                int normalId;
                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    ps.setString(1, name);
                    ps.setDouble(2, normalPrice);
                    ps.setString(3, "Normal");
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        normalId = rs.getInt("itemId");
                    }
                }
                int largeId;
                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    ps.setString(1, name);
                    ps.setDouble(2, normalPrice + 2.00);
                    ps.setString(3, "Large");
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        largeId = rs.getInt("itemId");
                    }
                }
                String ingSql =
                    "INSERT INTO public.\"Ingredients\" (\"itemId\", \"inventoryId\", \"quantityUsed\") " +
                    "VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(ingSql)) {
                    for (int invId : ingredientIds) {
                        ps.setInt(1, normalId);
                        ps.setInt(2, invId);
                        ps.setDouble(3, quantityUsed);
                        ps.addBatch();
                        ps.setInt(1, largeId);
                        ps.setInt(2, invId);
                        ps.setDouble(3, quantityUsed);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
                return normalId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    /**
     * This function adds an item to the database.
     * @param name The name of the item.
     * @param basePrice The price of the item
     * @param size The size of the item.
     */
    public static void addItemToDB(String name, double basePrice, String size) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO public.\"Item\" (\"name\", \"basePrice\", \"size\") VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, basePrice);
            ps.setString(3, size);
            System.out.println("Executing SQL: " + ps);
            ps.executeUpdate();
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * This functions submits everything in the customers cart to the database as an order.
     * @param customerName The customer name for the order.
     * @return This function returns the ID of the order.
     * @throws SQLException This function throws an exception when the database connection fails.
     */
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

    /**
     * This function gets the price of an item from the database.
     * @param inventoryId The ID of the item to seach for.
     * @return This function returns the price of the item.
     * @throws SQLException This function throws an exception when the database connection fails.
     */
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

    // UPDATES ORDERS everytime to the most recent so orderids are added correctly
    private static void resetSequences() {
        String[] sqls = {
            "SELECT setval(pg_get_serial_sequence('public.\"Order\"', 'orderId'), COALESCE((SELECT MAX(\"orderId\") FROM public.\"Order\"), 0))",
            "SELECT setval(pg_get_serial_sequence('public.\"Inventory\"', 'inventoryId'), COALESCE((SELECT MAX(\"inventoryId\") FROM public.\"Inventory\"), 0))",
            "SELECT setval(pg_get_serial_sequence('public.\"Item\"', 'itemId'), COALESCE((SELECT MAX(\"itemId\") FROM public.\"Item\"), 0))",
            "SELECT setval(pg_get_serial_sequence('public.\"OrderLineItem\"', 'orderLineId'), COALESCE((SELECT MAX(\"orderLineId\") FROM public.\"OrderLineItem\"), 0))"
        };
        try (Connection conn = getConnection()) {
            for (String sql : sqls) {
                conn.prepareStatement(sql).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // APP ENTRY - Jack can change once entry is set 
    @Override
    /**
     * This function launches the main view of the app upon startup.
     */
    public void start(Stage stage) throws Exception {
        resetSequences();
        loadItemCache();
        switchScene(stage, "/fxml/MainPage.fxml");
        stage.setTitle("Boba POS");
        stage.show();
    }
    /**
     * The main function of the program. It launches the app.
     * @param args No commandline arguments are used in this program.
     */
    public static void main(String[] args) {
        launch(args);
    }
}