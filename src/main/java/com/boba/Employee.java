package com.boba;
import javafx.beans.property.SimpleStringProperty;
//The employee class for populating the table of currect employees
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

    public String getEmployeeName() {
        return name.get();
    }
    public String getEmployeeID(){
        return ID.get();
    }
    public String getEmployeeSalary(){
        return salary.get();
    }
    public String getEmployeeTitle(){
        return title.get();
    }
    public String getEmployeeHireDate(){
        return hireDate.get();
    }

    public SimpleStringProperty nameProperty() { return name; }
    public SimpleStringProperty IDProperty() { return ID; }
    public SimpleStringProperty salaryProperty() { return salary; }
    public SimpleStringProperty titleProperty() { return title; }
    public SimpleStringProperty hireDateProperty() { return hireDate; }

}