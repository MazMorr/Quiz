package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;

import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class QuestionViewController {

    @Autowired
    private ClientServiceImpl clientService;
    @Autowired
    private SceneSwitcher sceneSwitcher;
    @Autowired
    private Points points;
    @FXML
    private Label lblSecondPart, txtGreenTeam, txtPurpleTeam, txtRedTeam, txtBlueTeam, txtTotalQuestion;
    @FXML
    private ImageView questionImg;
    @FXML
    private AnchorPane questionPane;

    @Setter
    private List<String> questions; // Lista de preguntas
    private int currentQuestionIndex = 0; // Índice de la pregunta actual
    @FXML
    private Label txtActualQuestion;
    @FXML
    private ImageView imgChivi;

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
                    if (event.getCode() == KeyCode.RIGHT) {
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

        if (questions != null && !questions.isEmpty()) {
            displayCurrentQuestion();
        } else {
            System.err.println("No se han recibido preguntas para mostrar.");
        }
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

    private void displayCurrentQuestion() {
        if (questions != null && currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            // Mostrar la pregunta actual
            lblSecondPart.setText(questions.get(currentQuestionIndex));

            // Verificar y cargar la imagen "chivi" para la temática actual
            loadChiviImageForCurrentQuestion();
        } else {
            System.err.println("Índice de pregunta fuera de rango.");
        }
    }

    // Método para cargar la imagen "chivi" de la temática actual
    private void loadChiviImageForCurrentQuestion() {
        // Ruta base de las imágenes "chivi"
        String thematicPath = clientService.getClientById(1).getRutaCarpetas();
        String chiviImagePath = thematicPath + "/ChiviTemática" + "/chivi.png";

        File chiviImageFile = new File(chiviImagePath);

        if (chiviImageFile.exists()) {
            // Si la imagen "chivi" existe, cargarla en imgChivi
            imgChivi.setImage(new Image(chiviImageFile.toURI().toString()));
            System.out.println("Imagen chivi cargada: " + chiviImagePath);
        } else {
            // Si no existe, no hacer nada
            imgChivi.setImage(null);
            System.out.println("No se encontró imagen chivi para la pregunta actual.");
        }
    }

    @FXML
    private void displayNextQuestion() {
        if (questions != null && currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        } else {
            System.out.println("No hay más preguntas.");
        }
    }

    @FXML
    private void displayPreviousQuestion() {
        if (questions != null && currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayCurrentQuestion();
        } else {
            System.out.println("No hay preguntas anteriores.");
        }
    }

}
