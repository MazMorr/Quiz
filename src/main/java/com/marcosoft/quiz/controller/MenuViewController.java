package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;


@Controller
public class MenuViewController {
    @Autowired
    private Points points;
    @Autowired
    SceneSwitcher sceneSwitcher;
    @FXML
    private Button btnQuit;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnConfiguration;
    @FXML
    private Label title,txtVersion;

    @FXML
    public void initialize() {

        txtVersion.setText("Alfa");
        points.setGreenTeamPoints(0);
        points.setBlueTeamPoints(0);
        points.setRedTeamPoints(0);
        points.setPurpleTeamPoints(0);
    }

    @Deprecated
    public void switchToAdjust(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRoot(actionEvent, "/configurationView.fxml");

    }

    @FXML
    public void quit(ActionEvent actionEvent) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void switchToRuleteView(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRoot(actionEvent, "/ThematicSelectionView.fxml");
    }

    @FXML
    public void switchToConfiguration(ActionEvent actionEvent) {
    }
}
