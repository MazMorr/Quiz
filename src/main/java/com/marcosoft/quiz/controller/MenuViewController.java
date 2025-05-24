package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.Main;
import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.model.ThematicState;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Controller
public class MenuViewController {

    // =======================
    // Inyección de dependencias y nodos FXML
    // =======================

    @Autowired private DatabaseInitializer databaseInitializer;
    @Autowired private ClientServiceImpl clientServiceImpl;
    @Autowired private Points points;
    @Autowired private SceneSwitcher sceneSwitcher;
    @Autowired private DirectoriesCreator directoriesCreator;
    @Autowired private SoundPlayer soundPlayer;
    @Autowired private ThematicState thematicState;
    @Autowired private PersonalizedAlerts personalizedAlerts;

    @FXML private Button btnQuit, btnStart, btnConfiguration;
    @FXML private Label txtVersion;

    // =======================
    // Inicialización
    // =======================

    @FXML
    public void initialize() {
        txtVersion.setText("0.9.91b");
        databaseInitializer.init();
        restartPointsAndThematics();
        createClientIfDoesNotExists();
        initKeyboardEvents();
        directoriesCreator.createAllDirectoriesForTheQuiz();
        // soundPlayer.playMusic(clientServiceImpl.getClientById(1).getRutaCarpetas()+"/musica.mp3");
    }

    private void createClientIfDoesNotExists() {
        if (!clientServiceImpl.existsClientByClientId(1)) {
            Client client = new Client(1, "1280x700", 1, true, DirectoriesCreator.getBasePath(), 6, 4);
            clientServiceImpl.save(client);
        }
    }

    private void initKeyboardEvents() {
        Platform.runLater(() -> {
            if (btnQuit.getScene() != null) {
                btnQuit.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.CONTROL) {
                        Main.primaryStage.setFullScreen(true);
                    }
                });
            }
        });
    }

    // =======================
    // Navegación y acciones de UI
    // =======================

    @FXML
    public void quit(ActionEvent actionEvent) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void switchToThematicSelectionView(ActionEvent actionEvent) {
        try {
            if (validateConfiguration()) {
                sceneSwitcher.setRootWithEvent(actionEvent, "/thematicSelectionView.fxml");
            } else {
                personalizedAlerts.showError("Error", "Problema de Configuración", "Hay problemas en la configuración. Por favor, revisa los detalles y corrige los errores.");
            }
        } catch (IOException e) {
            personalizedAlerts.showError("Error", "Problema en cambio de Ventana", "Error al cambiar a la vista de selección de temáticas: " + e.getMessage());
        }
    }

    @FXML
    public void switchToConfiguration(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRootWithEvent(actionEvent, "/configurationView.fxml");
    }

    // =======================
    // Reinicio de puntos y temáticas
    // =======================

    public void restartPointsAndThematics() {
        points.setGreenTeamPoints(0);
        points.setBlueTeamPoints(0);
        points.setRedTeamPoints(0);
        points.setPurpleTeamPoints(0);
        thematicState.reset();
        thematicState.setThematicsSelectedCounter(0);
    }

    // =======================
    // Validación de configuración
    // =======================

    /**
     * Valida que todas las preguntas, respuestas, nombres de temáticas e imágenes estén configurados correctamente.
     *
     * @return true si todo está correctamente configurado, false en caso contrario.
     */
    private boolean validateConfiguration() {
        boolean allValid = true;
        StringBuilder errorMessage = new StringBuilder("Se encontraron los siguientes problemas:\n");
        String basePath = DirectoriesCreator.getBasePath();

        for (int thematicIndex = 1; thematicIndex <= 4; thematicIndex++) {
            String thematicPath = basePath + "/Temática" + thematicIndex;

            if (!isThematicNameValid(thematicPath)) {
                appendThematicNameError(errorMessage, thematicIndex);
                allValid = false;
            }

            if (!isThematicImageValid(thematicPath)) {
                appendThematicImageError(errorMessage, thematicIndex);
                allValid = false;
            }

            for (int questionIndex = 1; questionIndex <= 6; questionIndex++) {
                String questionPath = thematicPath + "/Pregunta" + questionIndex;

                if (!isQuestionFileValid(questionPath)) {
                    appendQuestionFileError(errorMessage, thematicIndex, questionIndex);
                    allValid = false;
                }

                String correctAnswerError = validateCorrectAnswerFile(questionPath, thematicIndex, questionIndex);
                if (correctAnswerError != null) {
                    errorMessage.append(correctAnswerError);
                    allValid = false;
                }
            }
        }

        if (!allValid) {
            personalizedAlerts.showError("Error", "Validación de Archivos", errorMessage.toString());
        }

        return allValid;
    }

    private boolean isThematicNameValid(String thematicPath) {
        File nameFile = new File(thematicPath + "/NombreTemática/nombre.txt");
        return nameFile.exists() && nameFile.length() > 0;
    }

    private boolean isThematicImageValid(String thematicPath) {
        File imageFile = new File(thematicPath + "/ImagenTemática/imagen.png");
        return imageFile.exists();
    }

    private boolean isQuestionFileValid(String questionPath) {
        File questionFile = new File(questionPath + "/Pregunta.txt");
        return questionFile.exists() && questionFile.length() > 0;
    }

    private String validateCorrectAnswerFile(String questionPath, int thematicIndex, int questionIndex) {
        File correctAnswerFile = new File(questionPath + "/NúmeroRespuestaCorrecta.txt");
        if (!correctAnswerFile.exists()) {
            return "- La temática " + thematicIndex + ", pregunta " + questionIndex + " no tiene configurado el archivo NúmeroRespuestaCorrecta.txt.\n";
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(correctAnswerFile))) {
                String line = reader.readLine();
                int correctAnswer = Integer.parseInt(line.trim());
                if (correctAnswer < 1 || correctAnswer > 3) {
                    return "- La temática " + thematicIndex + ", pregunta " + questionIndex + " tiene un valor inválido en NúmeroRespuestaCorrecta.txt: " + line + "\n";
                }
            } catch (IOException | NumberFormatException e) {
                return "- Error al leer el archivo NúmeroRespuestaCorrecta.txt para la temática " + thematicIndex + ", pregunta " + questionIndex + ": " + e.getMessage() + "\n";
            }
        }
        return null;
    }

    // =======================
    // Métodos auxiliares para mensajes de error
    // =======================

    private void appendThematicNameError(StringBuilder errorMessage, int thematicIndex) {
        errorMessage.append("- La temática ").append(thematicIndex).append(" no tiene un nombre configurado correctamente.\n");
    }

    private void appendThematicImageError(StringBuilder errorMessage, int thematicIndex) {
        errorMessage.append("- La temática ").append(thematicIndex).append(" no tiene una imagen configurada.\n");
    }

    private void appendQuestionFileError(StringBuilder errorMessage, int thematicIndex, int questionIndex) {
        errorMessage.append("- La temática ").append(thematicIndex).append(", pregunta ").append(questionIndex).append(" no tiene un archivo de pregunta válido.\n");
    }
}