package com.boba;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.time.LocalDate;
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
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Employee");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Employee Name:");
        String nameStr = nameDialog.showAndWait().orElse(null);
        if (nameStr == null || nameStr.isBlank()) return;

        TextInputDialog IDDialog = new TextInputDialog("");
        IDDialog.setTitle("Add Employee");
        IDDialog.setHeaderText(null);
        IDDialog.setContentText("Employee ID:");
        String IDStr = IDDialog.showAndWait().orElse(null);
        if (IDStr == null) return;

        TextInputDialog salaryDialog = new TextInputDialog("");
        salaryDialog.setTitle("Add Employee");
        salaryDialog.setHeaderText(null);
        salaryDialog.setContentText("Employee Salary:");
        String salaryStr = salaryDialog.showAndWait().orElse(null);
        if (salaryStr == null) return;

        TextInputDialog jobDialog = new TextInputDialog("");
        jobDialog.setTitle("Add Employee");
        jobDialog.setHeaderText(null);
        jobDialog.setContentText("Job Title:");
        String jobStr = jobDialog.showAndWait().orElse(null);
        if (jobStr == null) return;

        //date is the type in the database
        TextInputDialog dateDialog = new TextInputDialog("");
        dateDialog.setTitle("Add Employee");
        dateDialog.setHeaderText(null);
        dateDialog.setContentText("Employee Hire Date (yyyy-mm-dd):");
        String dateStr = dateDialog.showAndWait().orElse(null);
        if (dateStr == null) return;

        TextInputDialog usernameDialog = new TextInputDialog("");
        usernameDialog.setTitle("Add Employee");
        usernameDialog.setHeaderText(null);
        usernameDialog.setContentText("Login Username:");
        String usernameStr = usernameDialog.showAndWait().orElse(null);
        if (usernameStr == null) return;

        TextInputDialog passwordDialog = new TextInputDialog("");
        passwordDialog.setTitle("Add Employee");
        passwordDialog.setHeaderText(null);
        passwordDialog.setContentText("Login Password:");
        String passwordStr = passwordDialog.showAndWait().orElse(null);
        if (passwordStr == null) return;

        TextInputDialog viewDialog = new TextInputDialog("");
        viewDialog.setTitle("Add Employee");
        viewDialog.setHeaderText(null);
        viewDialog.setContentText("Cashier or Manager");
        String viewStr = viewDialog.showAndWait().orElse(null);
        if (viewStr == null) return;


        try {
            int id = Integer.parseInt(IDStr);
            Float wage = Float.parseFloat(salaryStr);
            Date date = java.sql.Date.valueOf(dateStr);
            String sql = "INSERT INTO public.\"Employees\" " +
                         "(\"employeeId\", \"name\", \"salary\", \"date_hired\", \"job_title\", \"username\", \"password\", \"view\") " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = MainApp.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, nameStr);
                ps.setFloat(3, wage);
                ps.setDate(4, date);
                ps.setString(5, jobStr);
                ps.setString(6, usernameStr);
                ps.setString(7, passwordStr);
                ps.setString(8, viewStr);
                ps.executeUpdate();
            }
            loadTable(null);
            showAlert("Success", "Employee added.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void removeEmployee(ActionEvent event) {
        TextInputDialog IDDialog = new TextInputDialog();
        IDDialog.setTitle("Delete Employee");
        IDDialog.setHeaderText(null);
        IDDialog.setContentText("Employee Id:");
        String IDStr = IDDialog.showAndWait().orElse(null);
        if (IDStr == null || IDStr.isBlank()) return;

         try {
            int id = Integer.parseInt(IDStr);
            String sql = "DELETE FROM public.\"Employees\" " +
                         "WHERE \"employeeId\" = ?";
            try (Connection conn = MainApp.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            loadTable(null);
            showAlert("Success", "Deleted Employee.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }

    
    @FXML
    void editEmployee(ActionEvent event) {
        TextInputDialog IDDialog = new TextInputDialog("");
        IDDialog.setTitle("Edit Employee");
        IDDialog.setHeaderText(null);
        IDDialog.setContentText("Employee ID:");
        String IDStr = IDDialog.showAndWait().orElse(null);
        if (IDStr == null) return;

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Edit Employee");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Employee Name:");
        String nameStr = nameDialog.showAndWait().orElse(null);
        if (nameStr == null) return;

        TextInputDialog salaryDialog = new TextInputDialog("");
        salaryDialog.setTitle("Edit Employee");
        salaryDialog.setHeaderText(null);
        salaryDialog.setContentText("Employee Salary:");
        String salaryStr = salaryDialog.showAndWait().orElse(null);
        if (salaryStr == null) return;

        TextInputDialog jobDialog = new TextInputDialog("");
        jobDialog.setTitle("Edit Employee");
        jobDialog.setHeaderText(null);
        jobDialog.setContentText("Job Title:");
        String jobStr = jobDialog.showAndWait().orElse(null);
        if (jobStr == null) return;

        //date is the type in the database
        TextInputDialog dateDialog = new TextInputDialog("");
        dateDialog.setTitle("Edit Employee");
        dateDialog.setHeaderText(null);
        dateDialog.setContentText("Employee Hire Date (yyyy-mm-dd):");
        String dateStr = dateDialog.showAndWait().orElse(null);
        if (dateStr == null) return;

        TextInputDialog usernameDialog = new TextInputDialog("");
        usernameDialog.setTitle("Add Employee");
        usernameDialog.setHeaderText(null);
        usernameDialog.setContentText("Login Username:");
        String usernameStr = usernameDialog.showAndWait().orElse(null);
        if (usernameStr == null) return;

        TextInputDialog passwordDialog = new TextInputDialog("");
        passwordDialog.setTitle("Add Employee");
        passwordDialog.setHeaderText(null);
        passwordDialog.setContentText("Login Password:");
        String passwordStr = passwordDialog.showAndWait().orElse(null);
        if (passwordStr == null) return;

        TextInputDialog viewDialog = new TextInputDialog("");
        viewDialog.setTitle("Add Employee");
        viewDialog.setHeaderText(null);
        viewDialog.setContentText("Cashier or Manager");
        String viewStr = viewDialog.showAndWait().orElse(null);
        if (viewStr == null) return;


        try {
            int id = Integer.parseInt(IDStr);
            String sql = "UPDATE public.\"Employees\" SET";
            if(!nameStr.isEmpty()){
                sql = sql + " \"name\" = ?";
                if((!salaryStr.isEmpty() ) || (!jobStr.isEmpty()) || (!dateStr.isEmpty()) || (!usernameStr.isEmpty()) || (!passwordStr.isEmpty()) || (!viewStr.isEmpty())){
                    sql = sql + ",";
                }
            }
            if(!salaryStr.isEmpty()){
                sql = sql + " \"salary\" = ?";
                
                if((!jobStr.isEmpty()) || (!dateStr.isEmpty()) || (!usernameStr.isEmpty()) || (!passwordStr.isEmpty())|| (!viewStr.isEmpty())){
                    sql = sql + ",";
                }
            }
            if(!jobStr.isEmpty() ){
                sql = sql + " \"job_title\" = ?";
                if((!dateStr.isEmpty()|| (!usernameStr.isEmpty()) || (!passwordStr.isEmpty()) || (!viewStr.isEmpty()))){
                    sql = sql + ",";
                }
            }
            if(!dateStr.isEmpty()){
                sql = sql + " \"date_hired\" = ?";
                if((!usernameStr.isEmpty()) || (!passwordStr.isEmpty()) || (!viewStr.isEmpty())){
                    sql = sql + ",";
                }
                
            }
            if(!usernameStr.isEmpty()){
                sql = sql + " \"username\" = ?";
                if((!passwordStr.isEmpty()) || (!viewStr.isEmpty())){
                    sql = sql + ",";
                }
                
            }
            if(!passwordStr.isEmpty()){
                sql = sql + " \"password\" = ?";
                if((!viewStr.isEmpty())){
                    sql = sql + ",";
                }
            }
            if(!viewStr.isEmpty()){
                sql = sql + " \"view\" = ?";
            }
            sql = sql + " WHERE \"employeeId\" = ?";
            

            try (Connection conn = MainApp.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                    int count = 1;
                    if(!nameStr.isEmpty()){
                        ps.setString(count, nameStr);
                        count += 1;
                    }
                    if(!salaryStr.isEmpty()){
                        Float wage = Float.parseFloat(salaryStr);
                        ps.setFloat(count, wage);
                        count += 1;
                    }
                    if(!jobStr.isEmpty()){
                        ps.setString(count, jobStr);
                        count += 1;
                    }
                    if(!dateStr.isEmpty()){
                        Date date = java.sql.Date.valueOf(dateStr);
                        ps.setDate(count, date);
                        count += 1;
                    }
                    if(!usernameStr.isEmpty()){
                        ps.setString(count, usernameStr);
                        count += 1;
                    }
                    if(!passwordStr.isEmpty()){
                        ps.setString(count, passwordStr);
                        count += 1;
                    }
                    if(!viewStr.isEmpty()){
                        ps.setString(count, viewStr);
                        count += 1;
                    }
                    ps.setInt(count, id);
                    ps.executeUpdate();                
                }
            loadTable(null);
            showAlert("Success", "Employee edited.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }

    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}