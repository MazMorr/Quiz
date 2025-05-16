package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.Main;
import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.model.ThematicState;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.DatabaseInitializer;
import com.marcosoft.quiz.utils.DirectoriesCreator;
import com.marcosoft.quiz.utils.SceneSwitcher;
import com.marcosoft.quiz.utils.SoundPlayer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    @Autowired
    private DatabaseInitializer databaseInitializer;
    @Autowired
    private ClientServiceImpl clientServiceImpl;
    @Autowired
    private Points points;
    @Autowired
    private SceneSwitcher sceneSwitcher;
    @Autowired
    private DirectoriesCreator directoriesCreator;
    @Autowired
    private SoundPlayer soundPlayer;
    @Autowired
    private ThematicState thematicState;

    @FXML
    private Button btnQuit, btnStart, btnConfiguration;
    @FXML
    private Label title, txtVersion;

    @FXML
    public void initialize() throws IOException {
        databaseInitializer.init();

        txtVersion.setText("0.9.8b");
        restartPoints();
        restartThematics();

        if (!clientServiceImpl.existsClientByClientId(1)) {
            Client client = new Client(1, "1000x660", 1, true, DirectoriesCreator.getBasePath());
            clientServiceImpl.save(client);
        }

        //Set FullScreen by Configuration stored in Database
        if (clientServiceImpl.getClientById(1).getModoPantalla() == 2) {
            Main.getPrimaryStage().setFullScreen(true);
        } else if (clientServiceImpl.getClientById(1).getModoPantalla() == 1) {
            Main.getPrimaryStage().setFullScreen(false);
        }
        //Set Resolution by configuration stored in database
        if (clientServiceImpl.getClientById(1).getResolucion().equals("1000x660")) {
            Main.getPrimaryStage().setWidth(1100);
            Main.getPrimaryStage().setHeight(700);
        }

        // Configurar eventos de teclado después de que la escena esté lista
        Platform.runLater(() -> {
            if (btnQuit.getScene() != null) {
                btnQuit.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.CONTROL) {
                        Main.getPrimaryStage().setFullScreen(true);
                    }
                });
            }
        });

        directoriesCreator.createAllDirectoriesForTheQuiz();
        //soundPlayer.playMusic(clientServiceImpl.getClientById(1).getRutaCarpetas()+"/musica.mp3");
    }

    @FXML
    public void quit(ActionEvent actionEvent) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void switchToThematicSelectionView(ActionEvent actionEvent) {
        try {
            // Validar configuración de preguntas, respuestas, nombres de temáticas e imágenes
            if (validateConfiguration()) {
            // Cambiar a la vista de selección de temáticas si todo está correcto
            sceneSwitcher.setRootWithEvent(actionEvent, "/thematicSelectionView.fxml");
            } else {
            // Mostrar un mensaje de error si hay problemas en la configuración
                showError("Hay problemas en la configuración. Por favor, revisa los detalles y corrige los errores.");
            }
        } catch (IOException e) {
            showError("Error al cambiar a la vista de selección de temáticas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToConfiguration(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRootWithEvent(actionEvent, "/configurationView.fxml");
    }

    public void restartPoints() {
        points.setGreenTeamPoints(0);
        points.setBlueTeamPoints(0);
        points.setRedTeamPoints(0);
        points.setPurpleTeamPoints(0);
    }

    public void restartThematics() {
        thematicState.setThematic1selected(false);
        thematicState.setThematic2selected(false);
        thematicState.setThematic3selected(false);
        thematicState.setThematic4selected(false);
        thematicState.setX(0);
    }

    /**
     * Valida que todas las preguntas, respuestas, nombres de temáticas e imágenes estén configurados correctamente.
     *
     * @return true si todo está correctamente configurado, false en caso contrario.
     */
    private boolean validateConfiguration() {
        boolean allValid = true;
        StringBuilder errorMessage = new StringBuilder("Se encontraron los siguientes problemas:\n");
        String basePath = DirectoriesCreator.getBasePath();

        // Validar cada temática
        for (int thematicIndex = 1; thematicIndex <= 4; thematicIndex++) { // Cambia 4 por el número total de temáticas
            String thematicPath = basePath + "/Temática" + thematicIndex;

            // Validar nombre de la temática
            File nameFile = new File(thematicPath + "/NombreTemática/nombre.txt");
            if (!nameFile.exists() || nameFile.length() == 0) {
                errorMessage.append("- La temática ").append(thematicIndex).append(" no tiene un nombre configurado correctamente.\n");
                allValid = false;
            }

            // Validar imagen de la temática
            File imageFile = new File(thematicPath + "/ImagenTemática/imagen.png");
            if (!imageFile.exists()) {
                errorMessage.append("- La temática ").append(thematicIndex).append(" no tiene una imagen configurada.\n");
                allValid = false;
            }

            // Validar preguntas y respuestas
            for (int questionIndex = 1; questionIndex <= 6; questionIndex++) { // Cambia 6 por el número de preguntas por temática
                String questionPath = thematicPath + "/Pregunta" + questionIndex;

                // Validar archivo de la pregunta
                File questionFile = new File(questionPath + "/Pregunta.txt");
                if (!questionFile.exists() || questionFile.length() == 0) {
                    errorMessage.append("- La temática ").append(thematicIndex).append(", pregunta ").append(questionIndex).append(" no tiene un archivo de pregunta válido.\n");
                    allValid = false;
                }

                // Validar archivo de la respuesta correcta
                File correctAnswerFile = new File(questionPath + "/NúmeroRespuestaCorrecta.txt");
                if (!correctAnswerFile.exists()) {
                    errorMessage.append("- La temática ").append(thematicIndex).append(", pregunta ").append(questionIndex).append(" no tiene configurado el archivo NúmeroRespuestaCorrecta.txt.\n");
                    allValid = false;
                } else {
                    try (BufferedReader reader = new BufferedReader(new FileReader(correctAnswerFile))) {
                        String line = reader.readLine();
                        int correctAnswer = Integer.parseInt(line.trim());
                        if (correctAnswer < 1 || correctAnswer > 3) {
                            errorMessage.append("- La temática ").append(thematicIndex).append(", pregunta ").append(questionIndex).append(" tiene un valor inválido en NúmeroRespuestaCorrecta.txt: ").append(line).append("\n");
                            allValid = false;
                        }
                    } catch (IOException | NumberFormatException e) {
                        errorMessage.append("- Error al leer el archivo NúmeroRespuestaCorrecta.txt para la temática ").append(thematicIndex).append(", pregunta ").append(questionIndex).append(": ").append(e.getMessage()).append("\n");
                        allValid = false;
                    }
                }
            }
        }

        // Mostrar el mensaje de error si hay problemas
        if (!allValid) {
            showError(errorMessage.toString());
        }

        return allValid;
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param message Mensaje de error a mostrar.
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de configuración");
            alert.setHeaderText("Configuración inválida");
            alert.setContentText(message);
            alert.getDialogPane().setPrefWidth(600); // Ajustar el ancho del cuadro de diálogo si el mensaje es largo
            alert.showAndWait();
        });
    }

}
