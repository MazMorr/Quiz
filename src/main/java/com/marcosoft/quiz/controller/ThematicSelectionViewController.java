package com.marcosoft.quiz.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;

@Controller
public class ThematicSelectionViewController {

    @FXML
    private Label txtFirstOption;

    @FXML
    private Label txtSecondOption;
    @FXML
    private Label txtPurpleTeam;
    @FXML
    private Label txtRedTeam;
    @FXML
    private Label txtBlueTeam;
    @FXML
    private Label txtGreenTeam;

    @FXML
    void firstOptionSelected(ActionEvent event) {

    }

    @FXML
    void secondOptionSelected(ActionEvent event) {

    }

}
