package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

public class AllReportsController {

    @FXML
    private TextField endDateProductUsage;

    @FXML
    private TextField endTimeProductUsage;

    @FXML
    private Button generateProductUsage;

    @FXML
    private TableColumn<?, ?> itemId;

    @FXML
    private TableColumn<?, ?> name;

    @FXML
    private TextField startDateProductUsage;

    @FXML
    private TextField startTimeProductUsage;

    @FXML
    private TableColumn<?, ?> totalUsed;

    @FXML
    void generateProductUsage(ActionEvent event) {

    }

}

