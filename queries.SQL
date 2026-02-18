
-- Weekly Sales History
SELECT
    DATE_TRUNC('week', "orderDate") AS week_start,
    COUNT(*) AS order_count
FROM "Order"
GROUP BY week_start
ORDER BY week_start;

-- 10 most expensive orders in a day
SELECT
    DATE("orderDate") AS sale_day,
    SUM("totalAmount") AS total_sales
FROM "Order"
GROUP BY sale_day
ORDER BY total_sales DESC
LIMIT 10;

-- Total orders and sales for a specific hour
SELECT
    EXTRACT(HOUR FROM "orderDate") AS hour_of_day,
    COUNT(*) AS order_count,
    ROUND(SUM("totalAmount")::numeric, 2) AS total_sales
FROM "Order"
GROUP BY hour_of_day
ORDER BY hour_of_day;

-- How many inventory items in each menu item
SELECT
    i.name AS menu_item,
    i.size,
    COUNT(ing."inventoryId") AS inventory_item_count
FROM "Item" i
JOIN "Ingredients" ing ON i."itemId" = ing."itemId"
GROUP BY i."itemId", i.name, i.size
ORDER BY i."itemId";

-- Data for the lowest selling day of the week
WITH daily_sales AS (
    SELECT
        DATE("orderDate") AS sale_day,
        DATE_TRUNC('week', "orderDate") AS week_start,
        SUM("totalAmount") AS total_sales
    FROM "Order"
    GROUP BY sale_day, week_start
),
worst_day_per_week AS (
    SELECT DISTINCT ON (week_start)
        week_start,
        sale_day,
        total_sales
    FROM daily_sales
    ORDER BY week_start, total_sales ASC
),
top_item_per_day AS (
    SELECT DISTINCT ON (DATE(o."orderDate"))
        DATE(o."orderDate") AS sale_day,
        it.name AS top_item,
        COUNT(oli."orderLineId") AS item_count
    FROM "Order" o
    JOIN "OrderLineItem" oli ON o."orderId" = oli."orderId"
    JOIN "Item" it ON oli."itemId" = it."itemId"
    GROUP BY DATE(o."orderDate"), it.name
    ORDER BY DATE(o."orderDate"), item_count DESC
)
SELECT
    w.week_start,
    w.sale_day,
    ROUND(w.total_sales::numeric, 2) AS lowest_sales,
    t.top_item
FROM worst_day_per_week w
JOIN top_item_per_day t ON w.sale_day = t.sale_day
ORDER BY w.week_start;

-- Total Overall Sales
SELECT
    ROUND(SUM("totalAmount")::numeric, 2) AS total_revenue,
    COUNT(*) AS total_orders
FROM "Order";

-- Best Selling Menu Items
SELECT
    i.name,
    i.size,
    COUNT(oli."orderLineId") AS times_ordered,
    SUM(oli.quantity) AS total_quantity_sold
FROM "Item" i
JOIN "OrderLineItem" oli ON i."itemId" = oli."itemId"
GROUP BY i."itemId", i.name, i.size
ORDER BY total_quantity_sold DESC
LIMIT 10;

-- Inventory Items Below Reorder Threshold
SELECT
    name,
    "quantityOnHand",
    "reorderThreshold"
FROM "Inventory"
WHERE "quantityOnHand" < "reorderThreshold"
ORDER BY "quantityOnHand" ASC;

-- Average Order Value by Month
SELECT
    TO_CHAR(DATE_TRUNC('month', "orderDate"), 'YYYY-MM') AS month,
    ROUND(AVG("totalAmount")::numeric, 2) AS avg_order_value,
    COUNT(*) AS order_count
FROM "Order"
GROUP BY month
ORDER BY month;

-- Normal vs Large Size Sales Comparison
SELECT
    i.size,
    COUNT(oli."orderLineId") AS times_ordered,
    ROUND(SUM(o."totalAmount")::numeric, 2) AS total_revenue
FROM "OrderLineItem" oli
JOIN "Item" i ON oli."itemId" = i."itemId"
JOIN "Order" o ON oli."orderId" = o."orderId"
GROUP BY i.size
ORDER BY total_revenue DESC;

-- Most Popular Customizations for Sugar and Ice
SELECT
    "sugarAmount",
    "iceLevel",
    COUNT(*) AS order_count
FROM "OrderLineItem"
GROUP BY "sugarAmount", "iceLevel"
ORDER BY order_count DESC
LIMIT 10;

-- Daily Order Count for the Last 30 Days
SELECT
    DATE("orderDate") AS sale_day,
    COUNT(*) AS order_count,
    ROUND(SUM("totalAmount")::numeric, 2) AS daily_revenue
FROM "Order"
WHERE "orderDate" >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY sale_day
ORDER BY sale_day;

-- Revenue by Day of Week
SELECT
    TO_CHAR("orderDate", 'Day') AS day_of_week,
    EXTRACT(DOW FROM "orderDate") AS day_num,
    COUNT(*) AS order_count,
    ROUND(SUM("totalAmount")::numeric, 2) AS total_revenue
FROM "Order"
GROUP BY day_of_week, day_num
ORDER BY day_num;

-- Top 5 Highest Single Orders
SELECT
    "orderId",
    "customerName",
    DATE("orderDate") AS order_date,
    ROUND("totalAmount"::numeric, 2) AS total_amount
FROM "Order"
ORDER BY "totalAmount" DESC
LIMIT 5;

-- Items Never Ordered
SELECT
    i."itemId",
    i.name,
    i.size
FROM "Item" i
LEFT JOIN "OrderLineItem" oli ON i."itemId" = oli."itemId"
WHERE oli."itemId" IS NULL;

-- Inventory Usage Per Menu Item
SELECT
    i.name AS menu_item,
    i.size,
    inv.name AS ingredient,
    ing."quantityUsed"
FROM "Item" i
JOIN "Ingredients" ing ON i."itemId" = ing."itemId"
JOIN "Inventory" inv ON ing."inventoryId" = inv."inventoryId"
ORDER BY i."itemId", inv.name;

-- Most orders for a customer
SELECT
    "customerName",
    COUNT(*) AS order_count,
    ROUND(SUM("totalAmount")::numeric, 2) AS total_spent
FROM "Order"
WHERE "customerName" != 'ANONYMOUS'
GROUP BY "customerName"
ORDER BY order_count DESC
LIMIT 10;

-- Weekly Revenue Trend
SELECT
    DATE_TRUNC('week', "orderDate") AS week_start,
    ROUND(SUM("totalAmount")::numeric, 2) AS weekly_revenue,
    COUNT(*) AS order_count
FROM "Order"
WHERE "orderDate" >= CURRENT_DATE - INTERVAL '65 weeks'
GROUP BY week_start
ORDER BY week_start;