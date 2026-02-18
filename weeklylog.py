import csv
import psycopg2
from dotenv import load_dotenv
import os
import random
from datetime import datetime, timedelta

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

#names
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

ice_types = ["Light", "Normal", "None"]

peak_days = {
    datetime(2025, 1, 21).date(),
    datetime(2025, 8,25).date(),
    datetime(2025,9,6).date(),
    datetime(2025,10,4).date(),
    }

#Clears data from Order and OrderLineItem
sql_query = "DELETE FROM \"OrderLineItem\";"
cursor.execute(sql_query)

sql_query = "DELETE FROM \"Order\";"
cursor.execute(sql_query)

# 65 weeks, $1.25 million in sales, 4 peak days, 24 menu items 
# 18 required queries, all 5 special queries
orderlineId = 0
orderId = 0
for years in range(2024,2026):

    for months in range(1,13):

        for days in range(1,32):
            if months == 2 and days >=29:
                continue
            if months == 4 and days >=30:
                continue
            if months == 6 and days >=30:
                continue
            if months == 9 and days >=30:
                continue
            if months == 11 and days >=30:
                continue


            current = datetime(years,months,days).date()
            if current in peak_days:
                orders = random.randint(200,250)
            else:
                orders = random.randint(50,100)
            for order in range(orders):
                total = 0
                orderId += 1
                drinks = random.randint(1,3)
                hour = random.randint(8,20)
                minute = random.randint(0,59)
                second = random.randint(0,59)
                name = random.choice(names)
                sql_query = f"INSERT INTO \"Order\"(\"orderId\",\"orderDate\",\"status\",\"customerName\") VALUES ({orderId},\'{years}-{months}-{days} {hour}:{minute}:{second}',\'READY\',\'{name}\');"
                cursor.execute(sql_query)
                for drinks_ordered in range(drinks):
                    quantity = 1
                    orderlineId += 1
                    actual_drink_id = random.randint(1,54)
                    sugar = random.choice(sugar_amounts)
                    ice = random.choice(ice_types)
                    sql_query = f"SELECT \"basePrice\" FROM \"Item\" WHERE \"itemId\" = {actual_drink_id};"
                    cursor.execute(sql_query)
                    total += cursor.fetchone()[0]
                    sql_query = f"INSERT INTO \"OrderLineItem\"(\"orderLineId\", \"orderId\", \"itemId\", \"quantity\",\"sugarAmount\",\"iceLevel\") VALUES ({orderlineId},{orderId},{actual_drink_id},{quantity},{sugar},\'{ice}\')"
                    cursor.execute(sql_query)
                
                sql_query = f"UPDATE \"Order\" SET \"totalAmount\" = {total} WHERE \"orderId\" = {orderId};"
                cursor.execute(sql_query)
conn.commit()
conn.close()
                    
                    



