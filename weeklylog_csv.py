import psycopg2
from dotenv import load_dotenv
import os
import random
import csv 
from datetime import datetime

load_dotenv()

# Connect to the database
conn = psycopg2.connect(
    host = "csce-315-db.engr.tamu.edu",
    database = "team_61_db",
    user = "team_61",
    password = "331_61"
)
cursor = conn.cursor()

# Base data arrays
names = [
    "Liam", "Olivia", "Noah", "Emma", "Oliver", "Charlotte", "Eli", "Amelia",
    "James", "Ava", "William", "Sophia", "Benjamin", "Isabella", "Lucas", "Mia",
    "Henry", "Evelyn", "Theodore", "Harper", "Jack", "Luna", "Levi", "Camila",
    "Alexander", "Gianna", "Jackson", "Elizabeth", "Mateo", "Eleanor", "Nilay", "Ella",
    "Michael", "Abigail", "Mason", "Grant", "Sebastian", "Avery", "Ethan", "Scarlett",
    "Logan", "Emily", "Owen", "Aria", "Samuel", "Penelope", "Jacob", "Chloe",
    "Asher", "Layla", "Aiden", "Mila", "John", "Nora", "Joseph", "Hazel",
    "Wyatt", "Madison", "David", "Ellie", "Leo", "Lily", "Luke", "Nova",
    "Julian", "Isla", "Hudson", "Grace", "Grayson", "Maher", "Matthew", "Aurora",
    "Ezra", "Riley", "Gabriel", "Zoey", "Carter", "Willow", "Isaac", "Emilia",
    "Jayden", "Stella", "Luca", "Zoe", "Anthony", "Victoria", "Dylan", "Hannah",
    "Lincoln", "Addison", "Thomas", "Leah", "Maverick", "Lucy", "Elias", "Lillian",
    "Jeremiah", "Natalie", "Charles", "Paisley"
]

flavorNums = [2,5,6,7,10,12,14,15]
baseNums = [1,3,4,8,9,11,13,16]
temps = ["HOT", "COLD"]
sugar_amounts = [0, 25, 50, 75, 100]
ice_types = ["NONE", "LESS", "REGULAR" ,"EXTRA"]

peak_days = {
    datetime(2025, 1, 21).date(),
    datetime(2025, 8, 25).date(),
    datetime(2025, 9, 6).date(),
    datetime(2025, 10, 4).date(),
}

#Fetch menu item bases
cursor.execute('''
    SELECT item."itemId", ing."inventoryId"
    FROM "Item" item
    JOIN "Ingredients" ing ON item."itemId" = ing."itemId"
    JOIN "Inventory" inv ON ing."inventoryId" = inv."inventoryId"
    WHERE inv."type" = 'Base';
''')
menu_item_bases = {row[0]: str(row[1]) for row in cursor.fetchall()}

#Fetch menu item preset toppings
cursor.execute('''
    SELECT item."itemId", ing."inventoryId"
    FROM "Item" item
    JOIN "Ingredients" ing ON item."itemId" = ing."itemId"
    JOIN "Inventory" inv ON ing."inventoryId" = inv."inventoryId"
    WHERE inv."type" = 'Topping';
''')
menu_item_toppings = {}
for row in cursor.fetchall():
    item_id = row[0]
    topping_id = str(row[1])
    
    if item_id not in menu_item_toppings:
        menu_item_toppings[item_id] = []
        
    # Keep up to 5 toppings per drink
    if len(menu_item_toppings[item_id]) < 5:
        menu_item_toppings[item_id].append(topping_id)

# Load in prices from database
cursor.execute('SELECT "itemId", "basePrice" FROM "Item";')
prices = {row[0]: row[1] for row in cursor.fetchall()}

cursor.execute('SELECT "inventoryId", "pricePerUnit" FROM "Inventory" WHERE "type" = \'Base\';')
basePrices = {row[0]: row[1] for row in cursor.fetchall()}

cursor.execute('SELECT "inventoryId", "pricePerUnit" FROM "Inventory" WHERE "type" = \'Flavor\';')
flavorPrices = {row[0]: row[1] for row in cursor.fetchall()}

cursor.execute('SELECT "inventoryId", "pricePerUnit" FROM "Inventory" WHERE "type" = \'Topping\';')
toppingsPrices = {row[0]: row[1] for row in cursor.fetchall()}

orderLineId = 0
orderId = 0

# Open the CSV files for writing
with open('orders.csv', mode='w', newline='') as orders_file, \
     open('order_line_items.csv', mode='w', newline='') as items_file:
    
    orders_writer = csv.writer(orders_file)
    items_writer = csv.writer(items_file)
    
    # Write the headers
    orders_writer.writerow(['orderId', 'orderDate', 'status', 'totalAmount', 'customerName'])
    items_writer.writerow(['orderLineId', 'orderId', 'itemId', 'quantity', 'sugarAmount', 'iceLevel', 'temperature', 'baseType', 'extras', 'topping1', 'topping2', 'topping3', 'topping4', 'topping5'])

    for years in range(2024, 2026):
        for months in range(1, 13):
            for days in range(1, 32):
                # skipping non real days
                if months == 2 and days >= 29:
                    continue
                if months in [4, 6, 9, 11] and days >= 30:
                    continue

                current = datetime(years, months, days).date()
                if current in peak_days:
                    orders = random.randint(200, 250)
                else:
                    orders = random.randint(50, 100)

                for order in range(orders):
                    total = 0
                    orderId += 1
                    drinks = random.randint(1, 3)
                    hour = random.randint(8, 20)
                    minute = random.randint(0, 59)
                    second = random.randint(0, 59)
                    name = random.choice(names)
                    
                    # Format the date string 
                    order_date_str = f"{years}-{months:02d}-{days:02d} {hour:02d}:{minute:02d}:{second:02d}"

                    menuDrinks = random.randint(0, drinks)
                    customDrinks = drinks - menuDrinks

                    # Menu Drinks
                    for drinks_ordered in range(menuDrinks):
                        quantity = 1
                        orderLineId += 1
                        actual_drink_id = random.randint(1, 55)
                        while actual_drink_id == 28:
                            actual_drink_id = random.randint(1,55)
                        sugar = random.choice(sugar_amounts)
                        ice = random.choice(ice_types)
                        temperature = random.choice(temps)
                        
                        # Fetch base
                        menu_base = menu_item_bases.get(actual_drink_id)

                        # Fetch preset toppings
                        preset_toppings = menu_item_toppings.get(actual_drink_id, [])
                        t = [None, None, None, None, None]
                        for i, top in enumerate(preset_toppings):
                            t[i] = top

                        item_price = prices.get(actual_drink_id, 0)
                        total += item_price
                        
                        # Write row to OrderLineItem CSV
                        items_writer.writerow([
                            orderLineId, orderId, actual_drink_id, quantity, 
                            sugar, ice, temperature, menu_base, 0, 
                            t[0], t[1], t[2], t[3], t[4]
                        ])
                    
                    # Custom Drinks
                    for customDrinksOrdered in range(customDrinks):
                        quantity = 1
                        orderLineId += 1
                        baseDrink = random.randint(1, 56) 
                        sugar = random.choice(sugar_amounts)
                        ice = random.choice(ice_types)
                        temperature = random.choice(temps)
                        
                        base = random.choice(baseNums)
                        flavor = random.choice(flavorNums)
                        
                        base_cost = basePrices.get(base, 0)
                        flavor_cost = flavorPrices.get(flavor, 0)
                        extras_cost = base_cost + flavor_cost
                        total += extras_cost
                        
                        num_toppings = random.randint(0, 5)
                        t = [None, None, None, None, None]
                        
                        for topping_idx in range(num_toppings):
                            randomTopping = random.randint(17, 27)
                            t[topping_idx] = str(randomTopping)
                            
                            topping_cost = toppingsPrices.get(randomTopping, 0)
                            extras_cost += topping_cost
                            total += topping_cost

                        items_writer.writerow([
                            orderLineId, orderId, baseDrink, quantity, 
                            sugar, ice, temperature, str(base), round(extras_cost, 2), 
                            t[0], t[1], t[2], t[3], t[4]
                        ])
                    
                    # Write row to Order CSV 
                    orders_writer.writerow([orderId, order_date_str, 'READY', round(total, 2), name])

# Close the database connection
conn.close()
print("CSVs generated successfully!")