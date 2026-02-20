import psycopg2
from dotenv import load_dotenv
import os
import random
import csv # Added the built-in csv module
from datetime import datetime, timedelta

# Connecting to the database to fetch initial prices
load_dotenv()

conn = psycopg2.connect(
    # host=os.getenv("DB_HOST"),
    # database=os.getenv("DB_NAME"),
    # user=os.getenv("DB_USER"),
    # password=os.getenv("DB_PASSWORD")
    host = "csce-315-db.engr.tamu.edu",
    database = "team_61_db",
    user = "team_61",
    password = "331_61"
)
cursor = conn.cursor()

# 100 names for the purpose of generating a random name, Different sugar amounts
# ice types and peak days
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

sugar_amounts = [0, 25, 50, 75, 100]

ice_types = ["NONE", "LESS", "REGULAR" ,"EXTRA"]

peak_days = {
    datetime(2025, 1, 21).date(),
    datetime(2025, 8, 25).date(),
    datetime(2025, 9, 6).date(),
    datetime(2025, 10, 4).date(),
}

# loads in prices from database
cursor.execute('SELECT "itemId", "basePrice" FROM "Item";')
prices = {row[0]: row[1] for row in cursor.fetchall()}

# We no longer need to DELETE from the database here since we are just making CSVs.
# You can uncomment these if you still want the script to wipe the live tables.
# cursor.execute('DELETE FROM "OrderLineItem";')
# cursor.execute('DELETE FROM "Order";')
# conn.commit() 

orderlineId = 0
orderId = 0

# Open the CSV files for writing
with open('orders.csv', mode='w', newline='') as orders_file, \
     open('order_line_items.csv', mode='w', newline='') as items_file:
    
    orders_writer = csv.writer(orders_file)
    items_writer = csv.writer(items_file)
    
    # Write the headers for your CSV files
    orders_writer.writerow(['orderId', 'orderDate', 'status', 'customerName', 'totalAmount'])
    items_writer.writerow(['orderLineId', 'orderId', 'itemId', 'quantity', 'sugarAmount', 'iceLevel'])

    for years in range(2024, 2026):
        for months in range(1, 13):
            for days in range(1, 32):
                # skipping non real days
                if months == 2 and days >= 29:
                    continue
                if months == 4 and days >= 30:
                    continue
                if months == 6 and days >= 30:
                    continue
                if months == 9 and days >= 30:
                    continue
                if months == 11 and days >= 30:
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
                    
                    # Format the date string cleanly
                    order_date_str = f"{years}-{months:02d}-{days:02d} {hour:02d}:{minute:02d}:{second:02d}"

                    for drinks_ordered in range(drinks):
                        quantity = 1
                        orderlineId += 1
                        actual_drink_id = random.randint(1, 54)
                        sugar = random.choice(sugar_amounts)
                        ice = random.choice(ice_types)
                        total += prices[actual_drink_id]
                        
                        # Write row to OrderLineItem CSV
                        items_writer.writerow([orderlineId, orderId, actual_drink_id, quantity, sugar, ice])
                    
                    # Write row to Order CSV *after* the total is fully calculated
                    orders_writer.writerow([orderId, order_date_str, 'READY', name, round(total, 2)])

# Close the database connection
conn.close()
print("CSVs generated successfully!")