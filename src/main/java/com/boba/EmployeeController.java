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

import java.sql.*;

public class EmployeeController {

    @FXML private Button returnHomeBtn;
    @FXML private TextField employeeName;
    @FXML private TextField employeeSalary;
    @FXML private TextField dateHired;
    @FXML private TextField employeeTitle;
    @FXML private TextField employeeId;
    @FXML private TextField employeeName1;
    @FXML private TextField employeeSalary1;
    @FXML private TextField dateHired1;
    @FXML private TextField employeeTitle1;
    @FXML private TextField employeeID;

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
    void editEmployee(ActionEvent event){
        //TODO
        String name = employeeName1.getText();
        String salary = employeeSalary1.getText();
        String hiringDate = dateHired1.getText();
        String title = employeeTitle1.getText();
        String ID = employeeID.getText();

        String sql = "UPDATE \"Employees\" SET ????? WHERE \"employeeId\" = ?;";
         try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if(name != null){
                stmt.setString(1, "name = \'" + name + "\'");
            } else{
                stmt.setString(1,null);
            }
            if(name != null && salary != null){
                stmt.setString(2,",");
            } else{
                stmt.setString(2,null);
            }
            if(salary != null){
                stmt.setString(3, "salary = " + Integer.parseInt(salary));
            } else {
                stmt.setString(3,null);
            }
            if(title != null && salary != null){
                stmt.setString(4,",");
            } else{
                stmt.setString(4,null);
            }
            if(title != null){
                stmt.setString(5, "job_title = \'" + title + "\'");
            } else {
                stmt.setString(5,null);
            }
            stmt.setInt(6, Integer.parseInt(ID));
            stmt.executeUpdate();
            
            employeeName1.clear();
            employeeSalary1.clear();
            dateHired1.clear();
            employeeTitle1.clear();
            employeeID.clear();

            // loadEmployees();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee editted successfully!");
        } catch (SQLException ex){
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Base Error", ex.getMessage());
        }
    }
}