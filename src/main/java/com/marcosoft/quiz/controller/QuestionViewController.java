package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.model.ThematicState;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.SceneSwitcher;
import com.marcosoft.quiz.utils.SoundPlayer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la vista de preguntas.
 * Maneja la visualización de preguntas, opciones de respuesta, imágenes asociadas
 * y la actualización de puntos de los equipos.
 */
@Controller
public class QuestionViewController {

    // Servicios y utilidades
    @Autowired private ClientServiceImpl clientService;
    @Autowired private SceneSwitcher sceneSwitcher;
    @Autowired private Points points;
    @Autowired private ThematicState thematicState;
    @Autowired private SoundPlayer soundPlayer;

    // Componentes de la interfaz
    @FXML private Label lblSecondPart, txtGreenTeam, txtPurpleTeam, txtRedTeam, txtBlueTeam, txtTotalQuestion, txtActualQuestion;
    @FXML private ImageView questionImg, imgChivi;
    @FXML private AnchorPane questionPane;
    @FXML private Button btnFirstOption, btnSecondOption, btnThirdOption;

    // Estado de preguntas y respuestas
    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();
    private int currentQuestionIndex = 0;

    // =======================
    // Inicialización
    // =======================

    @FXML
    private void initialize() {
        loadQuestionsAndAnswersFromThematic();
        initNodes();
        initKeyboardControls();
        resizeQuestionImage();
        if (questions != null && !questions.isEmpty()) {
            displayCurrentQuestion();
        } else {
            System.err.println("No se han recibido preguntas para mostrar.");
        }
    }

    private void initNodes() {
        if (txtBlueTeam != null && txtRedTeam != null && txtGreenTeam != null && txtPurpleTeam != null) {
            points.refreshPoints(txtBlueTeam, txtRedTeam, txtGreenTeam, txtPurpleTeam);
        } else {
            System.err.println("Los nodos @FXML no están inicializados");
        }
    }

    private void resizeQuestionImage() {
        questionPane.widthProperty().addListener((observable, oldValue, newValue) -> adjustImage());
        questionPane.heightProperty().addListener((observable, oldValue, newValue) -> adjustImage());
    }

    private void initKeyboardControls() {
        Platform.runLater(() -> {
            if (questionPane.getScene() != null) {
                setSceneFocus();
                questionPane.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.RIGHT) {
                        displayNextQuestion();
                    } else if (event.getCode() == KeyCode.LEFT) {
                        displayPreviousQuestion();
                    }
                });
            }
        });
    }

    private void setSceneFocus() {
        questionPane.getScene().getRoot().requestFocus();
    }

    // =======================
    // Navegación entre vistas
    // =======================

    @FXML
    private void switchToMenu(ActionEvent event) throws IOException {
        sceneSwitcher.setRootWithEvent(event, "/menuView.fxml");
    }

    @FXML
    private void switchToThematicSelection(ActionEvent event) throws IOException {
        sceneSwitcher.setRootWithEvent(event, "/thematicSelectionView.fxml");
    }

    // =======================
    // Navegación entre preguntas
    // =======================

    private void displayCurrentQuestion() {
        if (questions != null && currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            int answerStartIndex = currentQuestionIndex * 3;
            if (answers.size() >= answerStartIndex + 3) {
                lblSecondPart.setText(questions.get(currentQuestionIndex));
                btnFirstOption.setText(answers.get(answerStartIndex));
                btnSecondOption.setText(answers.get(answerStartIndex + 1));
                btnThirdOption.setText(answers.get(answerStartIndex + 2));
                loadQuestionImageOrThematicImage();
                loadChiviImageForCurrentQuestion();
            } else {
                System.err.println("No hay suficientes respuestas para la pregunta " + (currentQuestionIndex + 1));
            }
        } else {
            System.err.println("Índice de pregunta fuera de rango.");
        }
    }

    private void displayNextQuestion() {
        if (questions != null && !questions.isEmpty() && currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            txtActualQuestion.setText(String.valueOf(currentQuestionIndex + 1));
            resetButtonColors();
            displayCurrentQuestion();
        } else {
            System.out.println("No hay más preguntas disponibles.");
        }
    }

    private void displayPreviousQuestion() {
        if (questions != null && !questions.isEmpty() && currentQuestionIndex > 0) {
            currentQuestionIndex--;
            txtActualQuestion.setText(String.valueOf(currentQuestionIndex + 1));
            resetButtonColors();
            displayCurrentQuestion();
        } else {
            System.out.println("No hay preguntas anteriores disponibles.");
        }
    }

    // =======================
    // Manejo de opciones de respuesta
    // =======================

    @FXML
    public void firstOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(1, (Button) actionEvent.getSource());
    }

    @FXML
    public void secondOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(2, (Button) actionEvent.getSource());
    }

    @FXML
    public void thirdOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(3, (Button) actionEvent.getSource());
    }

    private void handleOptionSelected(int selectedOption, Button button) {
        int correctOption = getCorrectAnswerForCurrentQuestion();
        if (selectedOption == correctOption) {
            soundPlayer.playSound(getClass().getResource("/sounds/collect_points.mp3").toString());
            setButtonColor(button, "green");
        } else {
            setButtonColor(button, "red");
        }
        setSceneFocus();
    }

    private int getCorrectAnswerForCurrentQuestion() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas() + thematicState.getActualThematic();
        String questionPath = thematicPath + "/Pregunta" + (currentQuestionIndex + 1) + "/NúmeroRespuestaCorrecta.txt";
        File correctAnswerFile = new File(questionPath);

        if (correctAnswerFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(correctAnswerFile))) {
                String line = reader.readLine();
                return Integer.parseInt(line.trim());
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error al leer el archivo de respuesta correcta: " + e.getMessage());
            }
        } else {
            System.err.println("El archivo de respuesta correcta no existe: " + questionPath);
        }
        return -1;
    }

    private void setButtonColor(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + ";");
    }

    private void resetButtonColors() {
        btnFirstOption.setStyle("-fx-background-color: #333333;");
        btnSecondOption.setStyle("-fx-background-color: #333333;");
        btnThirdOption.setStyle("-fx-background-color: #333333;");
    }

    // =======================
    // Imágenes
    // =======================

    public void adjustImage() {
        double paneWidth = questionPane.getWidth();
        double paneHeight = questionPane.getHeight();
        questionImg.setFitWidth(paneWidth);
        questionImg.setFitHeight(paneHeight);
    }

    private void loadChiviImageForCurrentQuestion() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas() + thematicState.getActualThematic();
        String chiviImagePath = thematicPath + "/ChiviTemática/chivi.png";
        File chiviImageFile = new File(chiviImagePath);

        if (chiviImageFile.exists()) {
            imgChivi.setImage(new Image(chiviImageFile.toURI().toString()));
            System.out.println("Imagen chivi cargada: " + chiviImagePath);
        } else {
            System.out.println("No se encontró imagen chivi para la pregunta actual.");
        }
    }

    private void loadQuestionImageOrThematicImage() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas() + thematicState.getActualThematic();
        String questionPath = thematicPath + "/Pregunta" + (currentQuestionIndex + 1);

        String[] exts = {".png", ".jpg", ".jpeg", ".gif"};
        File imageFile = null;

        for (String ext : exts) {
            File f = new File(questionPath + "/imagen" + ext);
            if (f.exists()) {
                imageFile = f;
                break;
            }
        }

        if (imageFile != null && imageFile.exists()) {
            questionImg.setImage(new Image(imageFile.toURI().toString()));
        } else {
            File thematicImage = new File(thematicPath + "/ImagenTemática/imagen.png");
            if (thematicImage.exists()) {
                questionImg.setImage(new Image(thematicImage.toURI().toString()));
            } else {
                questionImg.setImage(new Image(getClass().getResource("/images/default.png").toString()));
            }
        }
    }

    // =======================
    // Carga de preguntas y respuestas
    // =======================

    private void loadQuestionsAndAnswersFromThematic() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas() + thematicState.getActualThematic();

        questions.clear();
        answers.clear();
        currentQuestionIndex = 0;

        for (int i = 1; i <= 6; i++) {
            String questionPath = thematicPath + "/Pregunta" + i;

            // Pregunta
            File questionFile = new File(questionPath + "/Pregunta.txt");
            if (questionFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(questionFile))) {
                    String question = reader.readLine();
                    if (question != null && !question.isBlank()) {
                        questions.add(question);
                    } else {
                        System.err.println("El archivo de la pregunta está vacío: " + questionFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    System.err.println("Error al leer el archivo de la pregunta: " + questionFile.getAbsolutePath());
                    e.printStackTrace();
                }
            } else {
                System.err.println("El archivo de la pregunta no existe: " + questionFile.getAbsolutePath());
            }

            // Respuestas
            for (int j = 1; j <= 3; j++) {
                File answerFile = new File(questionPath + "/Respuesta" + j + ".txt");
                if (answerFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(answerFile))) {
                        String answer = reader.readLine();
                        if (answer != null && !answer.isBlank()) {
                            answers.add(answer);
                        } else {
                            answers.add("Respuesta no disponible");
                            System.err.println("El archivo de la respuesta está vacío: " + answerFile.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        answers.add("Respuesta no disponible");
                        System.err.println("Error al leer el archivo de la respuesta: " + answerFile.getAbsolutePath());
                        e.printStackTrace();
                    }
                } else {
                    answers.add("Respuesta no disponible");
                    System.err.println("El archivo de la respuesta no existe: " + answerFile.getAbsolutePath());
                }
            }
        }
    }

    // =======================
    // Puntos de los equipos
    // =======================

    @FXML
    private void upgradePurplePoints(MouseEvent event) {
        updateTeamPoints(event, points::getPurpleTeamPoints, points::setPurpleTeamPoints, txtPurpleTeam);
    }

    @FXML
    private void upgradeGreenPoints(MouseEvent event) {
        updateTeamPoints(event, points::getGreenTeamPoints, points::setGreenTeamPoints, txtGreenTeam);
    }

    @FXML
    public void upgradeRedPoints(MouseEvent event) {
        updateTeamPoints(event, points::getRedTeamPoints, points::setRedTeamPoints, txtRedTeam);
    }

    @FXML
    private void upgradeBluePoints(MouseEvent event) {
        updateTeamPoints(event, points::getBlueTeamPoints, points::setBlueTeamPoints, txtBlueTeam);
    }

    private void updateTeamPoints(MouseEvent event, java.util.function.Supplier<Integer> getter,
                                  java.util.function.Consumer<Integer> setter, Label label) {
        if (event.getButton() == MouseButton.PRIMARY) {
            setter.accept(getter.get() + 1);
        } else if (event.getButton() == MouseButton.SECONDARY && getter.get() != 0) {
            setter.accept(getter.get() - 1);
        }
        label.setText(String.valueOf(getter.get()));
    }
}
