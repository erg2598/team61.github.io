package com.boba;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
/**
 * This class contains all of the logic for
 * the main login page where individuals can enter their
 * ID to access the cashier or manager view.
 * @author Grant Duong, Nilay Alwar, Eli Goodrich, Maher Zaveri, Jack Anderson
 */
public class MainPageController {
    /**
     * This class does not have a constructor.
     */
    public MainPageController(){}

    @FXML
    private Button enterButton;

    @FXML
    private Button managerSide;

    @FXML
    private Label loginLabel;

    @FXML
    private TextField passwordEntry;

    @FXML
    private TextField usernameEntry;

    @FXML
    /**
     * checks the username and password entered by the user
     * and launches either the cashier view or manager view
     * based on database log in 
     * @param event This function triggers when a button is pressed.
     */
    public void launchView(ActionEvent event) {
        String usernameInput = usernameEntry.getText();
        String passwordInput = passwordEntry.getText();
        try{
            String sql = "SELECT \"password\", \"view\" FROM public.\"Employees\" WHERE username = ?";
    
            Connection conn = MainApp.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usernameInput);
            var rs = ps.executeQuery();
            rs.next();
            String password = rs.getString("password");
            String view = rs.getString("view");

            if(!password.equals(passwordInput))
                throw new Exception("Incorrect password");

            if(view.equals("Cashier")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cashierview.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = new Stage();
                stage.setTitle("Cashier View");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ManagerView.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = new Stage();
                stage.setTitle("Manager View");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            }
        } catch (Exception e) {
            showAlert("Error", "Incorrect username or password.");
        }
    }
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
        a.showAndWait();
    }
}