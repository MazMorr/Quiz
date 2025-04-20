package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.Main;
import com.marcosoft.quiz.model.Points;

import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

@Controller
public class QuestionViewController {

    @Autowired
    private SceneSwitcher sceneSwitcher;
    @Autowired
    private Main main;
    @Autowired
    private Points points;
    @FXML
    private Label lblSecondPart, txtGreenTeam, txtPurpleTeam, txtRedTeam, txtBlueTeam, txtTotalQuestion;
    @FXML
    private ImageView questionImg;
    @FXML
    private AnchorPane questionPane;

    private List<String> questions; // Lista de preguntas
    private int currentQuestionIndex = 0; // Índice de la pregunta actual

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    @FXML
    private void upgradePurplePoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            points.setPurpleTeamPoints(points.getPurpleTeamPoints() + 1);
            txtPurpleTeam.setText("" + points.getPurpleTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getPurpleTeamPoints() != 0) {
            points.setPurpleTeamPoints(points.getPurpleTeamPoints() - 1);
            txtPurpleTeam.setText("" + points.getPurpleTeamPoints());
        }
    }

    @FXML
    private void upgradeGreenPoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            points.setGreenTeamPoints(points.getGreenTeamPoints() + 1);
            txtGreenTeam.setText("" + points.getGreenTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getGreenTeamPoints() != 0) {
            points.setGreenTeamPoints(points.getGreenTeamPoints() - 1);
            txtGreenTeam.setText("" + points.getGreenTeamPoints());
        }
    }

    @FXML
    public void upgradeRedPoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            points.setRedTeamPoints(points.getRedTeamPoints() + 1);
            txtRedTeam.setText("" + points.getRedTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getRedTeamPoints() != 0) {
            points.setRedTeamPoints(points.getRedTeamPoints() - 1);
            txtRedTeam.setText("" + points.getRedTeamPoints());
        }
    }

    @FXML
    private void upgradeBluePoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) { // Verifica si es el botón izquierdo
            points.setBlueTeamPoints(points.getBlueTeamPoints() + 1);
            txtBlueTeam.setText("" + points.getBlueTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getBlueTeamPoints() != 0) { // Verifica si es el botón derecho
            points.setBlueTeamPoints(points.getBlueTeamPoints() - 1);
            txtBlueTeam.setText("" + points.getBlueTeamPoints());
        }
    }

    @FXML
    private void initialize() {
        // Asegúrate de que los nodos @FXML estén inicializados
        if (txtBlueTeam != null && txtRedTeam != null && txtGreenTeam != null && txtPurpleTeam != null) {
            points.refreshPoints(txtBlueTeam, txtRedTeam, txtGreenTeam, txtPurpleTeam);
        } else {
            System.err.println("Los nodos @FXML no están inicializados");
        }

        // Configurar eventos de teclado después de que la escena esté lista
        Platform.runLater(() -> {
            if (txtRedTeam.getScene() != null) {
                txtRedTeam.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.CONTROL) {
                        main.getPrimaryStage().setFullScreen(true);
                    } else if (event.getCode() == KeyCode.RIGHT) {
                        displayNextQuestion();
                    } else if (event.getCode() == KeyCode.LEFT) {
                        displayPreviousQuestion();
                    }
                });
            }
        });

        // Agregar listeners para redimensionar la ventana
        questionPane.widthProperty().addListener((observable, oldValue, newValue) -> adjustImage());
        questionPane.heightProperty().addListener((observable, oldValue, newValue) -> adjustImage());


    }

    @FXML
    private void switchToMenu(ActionEvent event) throws IOException {
        sceneSwitcher.setRoot(event, "/menuView.fxml");
    }

    @FXML
    private void switchToThematicSelection(ActionEvent event) throws IOException {
        sceneSwitcher.setRoot(event, "/thematicSelectionView.fxml");
    }

    public void adjustImage() {
        double paneWidth = questionPane.getWidth();
        double paneHeight = questionPane.getHeight();

        // Ajustar el tamaño de la imagen al tamaño del panel sin preservar la relación de aspecto
        questionImg.setFitWidth(paneWidth);
        questionImg.setFitHeight(paneHeight);
    }

    public void displayNextQuestion() {
        if (questions != null && currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            lblSecondPart.setText(questions.get(currentQuestionIndex));
        } else {
            System.out.println("No hay más preguntas.");
        }
    }

    public void displayPreviousQuestion() {
        if (questions != null && currentQuestionIndex > 0) {
            currentQuestionIndex--;
            lblSecondPart.setText(questions.get(currentQuestionIndex));
        } else {
            System.out.println("No hay preguntas anteriores.");
        }
    }

}
