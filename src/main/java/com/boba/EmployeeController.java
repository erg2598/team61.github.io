package com.boba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EmployeeController {
    //simple employee class to populate the table
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> employeeColumn;
    @FXML private TableColumn<Employee, String> IDColumn;
    @FXML private TableColumn<Employee, String> salaryColumn;
    @FXML private TableColumn<Employee, String> dateColumn;
    @FXML private TableColumn<Employee, String> titleColumn;

    public class Employee {
        private final SimpleStringProperty name;
        private final SimpleStringProperty ID;
        private final SimpleStringProperty salary;
        private final SimpleStringProperty title;
        private final SimpleStringProperty hireDate;

        public Employee(String name, String ID, String salary, String title, String hireDate) {
            this.name = new SimpleStringProperty(name);
            this.ID = new SimpleStringProperty(ID);
            this.salary = new SimpleStringProperty(salary);
            this.title = new SimpleStringProperty(title);
            this.hireDate = new SimpleStringProperty(hireDate);
        }
    }   

    //populates and load table in the viewer
    @FXML
    public void initialize() {
        employeeColumn.setCellValueFactory(c      -> c.getValue().name);
        salaryColumn.setCellValueFactory(c    -> c.getValue().salary);
        IDColumn.setCellValueFactory(c     -> c.getValue().ID);
        titleColumn.setCellValueFactory(c   -> c.getValue().title);
        dateColumn.setCellValueFactory(c -> c.getValue().hireDate);
        loadTable(null);
    }

    // LOADS the table in the viewer
    @FXML
    public void loadTable(ActionEvent event) {
        ObservableList<Employee> rows = FXCollections.observableArrayList();
        String sql = "SELECT \"employeeId\", \"name\", \"salary\", \"date_hired\", " +
                     "\"job_title\" FROM public.\"Employees\" ORDER BY \"employeeId\"";
        try (Connection conn = MainApp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Employee(
                        rs.getString("employeeID"),
                        rs.getString("name"),
                        rs.getString("salary"),
                        rs.getString("date_hired"),
                        rs.getString("job_title")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        employeeTable.setItems(rows);
    }
    @FXML
    void returnHome(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        MainApp.switchScene(stage, "/fxml/managerview.fxml");
    }

    @FXML
    void addEmployee(ActionEvent event) {
        
    }

    @FXML
    void removeEmployee(ActionEvent event) {
    }

    
    @FXML
    void editEmployee(ActionEvent event) {


    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}