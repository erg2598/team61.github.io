import csv
import psycopg2
from dotenv import load_dotenv
import os
import random

load_dotenv()

conn = psycopg2.connect(
    host=os.getenv("DB_HOST"),
    database=os.getenv("DB_NAME"),
    user=os.getenv("DB_USER"),
    password=os.getenv("DB_PASSWORD")
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

#Clears data from Order and OrderLineItem
sql_query = "DELETE FROM \"Order\";"
cursor.execute(sql_query)

sql_query = "DELETE FROM \"OrderLineItem\";"
cursor.execute(sql_query)

# 65 weeks, $1.25 million in sales, 4 peak days, 24 menu items 
# 18 required queries, all 5 special queries
for years in range(2024,2025):

    for months in range(1,12):

        for days in range(1,31):

            orders = random.randint(100,150)
            for orderid in range(orders):
                total = 0
                drinks = random.randint(1,5)
                hour = random.randint(8,20)
                minute = random.randint(59)
                second = random.randint(59)
                name = random.choice(names)
                sql_query = f"INSERT INTO \"Order\"(\"orderId\",\"orderDate\",\"status\",\"customerName\") VALUES ({orderid},\'{years}-{months}-{days}\',\'READY\',\'{name}\',"
                for drinks_ordered in range(drinks):
                    sql_query = f"INSERT INTO "
                
                sql_query = f"INSERT INTO \"Order\"(\"orderId\",\"orderDate\",\"status\",\"totalAmount\",\"customerName\") VALUES ({orderid},\'{years}-{months}-{days}\',\'READY\',"
                    
                    




# -- Peak days -- 
# start of fall semester, start of spring semester, first home football game, TU game


#  