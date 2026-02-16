import csv
import psycopg2

# 1. Connect to the PostgreSQL database
conn = psycopg2.connect(
    host="csce-315-db.engr.tamu.edu",
    database="team_61_db",
    user="team_61",      
    password="331_61"   
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