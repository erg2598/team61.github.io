import csv
import psycopg2
from dotenv import load_dotenv
import os

# Load the .env file
load_dotenv()


# 1. Connect to the PostgreSQL database
conn = psycopg2.connect(
    host=os.getenv("DB_HOST"),
    database=os.getenv("DB_NAME"),
    user=os.getenv("DB_USER"),
    password=os.getenv("DB_PASSWORD")
)
cursor = conn.cursor()

# 2. Execute SQL query
sql_query = "SELECT * FROM \"Ingredients\";"
cursor.execute(sql_query)

# 3. Fetch all results
results = cursor.fetchall()
print(results)


# 4. Get column headers from cursor description
headers = [i[0] for i in cursor.description]

# 5. Write to CSV file
with open('output.csv', 'w', newline='') as csv_file:
    csv_writer = csv.writer(csv_file)
    csv_writer.writerow(headers)
    csv_writer.writerows(results)

# 6. Close the connection
conn.close()
  
print("CSV file 'output.csv' generated successfully.")