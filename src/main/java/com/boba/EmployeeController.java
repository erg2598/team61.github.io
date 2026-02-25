package com.boba;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;

public class EmployeeController {

     @FXML
    private TableColumn<?, ?> IDColumn;
    @FXML
    private TableColumn<?, ?> dateColumn;
    @FXML
    private TextField dateHired;
    @FXML
    private TextField dateHired1;
    @FXML
    private Button editEmployee;
    @FXML
    private TextField employeeID;
    @FXML
    private TextField employeeId;
    @FXML
    private TextField employeeName;
    @FXML
    private TextField employeeName1;
    @FXML
    private TextField employeeSalary;
    @FXML
    private TextField employeeSalary1;
    @FXML
    private TableView<?> employeeTable;
    @FXML
    private TextField employeeTitle;
    @FXML
    private TextField employeeTitle1;
    @FXML
    private Button returnButton;
    @FXML
    private TableColumn<?, ?> salaryColumn;
    @FXML
    private TableColumn<?, ?> tableColumn;
    @FXML
    private TableColumn<?, ?> titleColumn;

    //Connection to Database
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

    //Builds Alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }

    @FXML
    void addEmployee(ActionEvent event) {
        // Complete
        String name = employeeName.getText();
        String salary = employeeSalary.getText();
        String hiringDate = dateHired.getText();
        String title = employeeTitle.getText();

        String sql = "INSERT INTO public.\"Employees\"(name,salary,date_hired,job_title) VALUES (?,?,?,?);";
         try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, Integer.parseInt(salary));
            stmt.setDate(3, Date.valueOf(hiringDate));
            stmt.setString(4, title);
            stmt.executeUpdate();
            
            employeeName.clear();
            employeeSalary.clear();
            dateHired.clear();
            employeeTitle.clear();

            // loadEmployees();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully!");
        } catch (SQLException ex){
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Base Error", ex.getMessage());
        }
    }

    @FXML
    void viewEmployee(ActionEvent event) {
        // TODO
    }

    @FXML
    void removeEmployee(ActionEvent event) {
        // Complete
        String ID = employeeId.getText();

        String sql = "DELETE FROM \"Employees\" WHERE \"employeeId\" = ?;";
         try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(ID));
            stmt.executeUpdate();
            employeeId.clear();

            // loadEmployees();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully!");
        } catch (SQLException ex){
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Base Error", ex.getMessage());
        }
    }

    
    @FXML
    void editEmployee(ActionEvent event) {
    // Get text
    String name = employeeName1.getText().trim();
    String salaryText = employeeSalary1.getText().trim();
    String hiringDate = dateHired1.getText().trim();
    String title = employeeTitle1.getText().trim();
    String idText = employeeID.getText().trim();

    // ID is mandatory to know who to update
    if (idText.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Input Error", "Employee ID is required to edit.");
        return;
    }

    // Dynamically build the SQL and track parameters
    StringBuilder sql = new StringBuilder("UPDATE public.\"Employees\" SET ");
    List<Object> parameters = new ArrayList<>();

    
        if (!name.isEmpty()) {
            sql.append("\"name\" = ?, ");
            parameters.add(name);
        }
        if (!salaryText.isEmpty()) {
            sql.append("\"salary\" = ?, ");
            parameters.add(Double.parseDouble(salaryText));
        }
        if (!hiringDate.isEmpty()) {
            sql.append("\"date_hired\" = ?, ");
            parameters.add(Date.valueOf(hiringDate)); // Assuming format yyyy-mm-dd
        }
        if (!title.isEmpty()) {
            sql.append("\"job_title\" = ?, ");
            parameters.add(title);
        }

        // If the manager didn't fill in anything to change, abort
        if (parameters.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Changes", "Please fill out at least one field to update.");
            return;
        }

        // Remove the trailing comma from the last appended column
        sql.setLength(sql.length() - 2);

        // Add WHERE clause and the ID parameter
        sql.append(" WHERE \"employeeId\" = ?;");
        parameters.add(Integer.parseInt(idText));

        //Execute the dynamically built query
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Loop through our parameter list and set them in the PreparedStatement
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i)); 
            }

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                employeeName1.clear();
                employeeSalary1.clear();
                dateHired1.clear();
                employeeTitle1.clear();
                employeeID.clear();
                // loadEmployees();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee edited successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "No employee found with that ID.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }

}
}