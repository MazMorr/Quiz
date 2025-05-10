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

import static com.sun.javafx.fxml.ModuleHelper.getResourceAsStream;
import static com.sun.javafx.scene.control.skin.Utils.getResource;

/**
 * Controlador para la vista de preguntas.
 * Maneja la visualización de preguntas, opciones de respuesta, imágenes asociadas
 * y la actualización de puntos de los equipos.
 */
@Controller
public class QuestionViewController {

    // Servicios y utilidades
    @Autowired
    private ClientServiceImpl clientService; // Servicio para obtener rutas de archivos
    @Autowired
    private SceneSwitcher sceneSwitcher; // Utilidad para cambiar entre vistas
    @Autowired
    private Points points; // Modelo para manejar los puntos de los equipos
    @Autowired
    private ThematicState thematicState;
    @Autowired
    private SoundPlayer soundPlayer;

    // Componentes de la interfaz
    @FXML
    private Label lblSecondPart, txtGreenTeam, txtPurpleTeam, txtRedTeam, txtBlueTeam, txtTotalQuestion, txtActualQuestion;
    @FXML
    private ImageView questionImg, imgChivi; // Imágenes asociadas a las preguntas
    @FXML
    private AnchorPane questionPane; // Contenedor principal de la vista
    @FXML
    private Button btnFirstOption, btnSecondOption, btnThirdOption; // Botones de opciones de respuesta

    // Variables de estado

    private List<String> questions= new ArrayList<>(); // Lista de preguntas cargadas
    private List<String> answers= new ArrayList<>();

    private List<File> questionsFiles= new ArrayList<>();
    private List<File> answersFiles= new ArrayList<>();

    private int currentQuestionIndex = 0; // Índice de la pregunta actual

    /**
     * Inicialización de la vista.
     * Configura eventos de teclado, listeners para redimensionar la ventana
     * y muestra la primera pregunta si está disponible.
     */
    @FXML
    private void initialize() {
        loadQuestionsAndAnswersFromThematic();

        // Asegúrate de que los nodos @FXML estén inicializados
        if (txtBlueTeam != null && txtRedTeam != null && txtGreenTeam != null && txtPurpleTeam != null) {
            points.refreshPoints(txtBlueTeam, txtRedTeam, txtGreenTeam, txtPurpleTeam);
        } else {
            System.err.println("Los nodos @FXML no están inicializados");
        }

        // Configurar eventos de teclado después de que la escena esté lista
        Platform.runLater(() -> {
            if (questionPane.getScene() != null) {
                questionPane.getScene().getRoot().requestFocus(); // Asegura que el nodo raíz tenga el foco
                questionPane.getScene().setOnKeyPressed(event -> {
                    System.out.println("Tecla presionada: " + event.getCode());
                    if (event.getCode() == KeyCode.RIGHT) {
                        System.out.println("Flecha derecha presionada");
                        displayNextQuestion();
                    } else if (event.getCode() == KeyCode.LEFT) {
                        System.out.println("Flecha izquierda presionada");
                        displayPreviousQuestion();
                    }
                });
            }
        });

        // Agregar listeners para redimensionar la ventana
        questionPane.widthProperty().addListener((observable, oldValue, newValue) -> adjustImage());
        questionPane.heightProperty().addListener((observable, oldValue, newValue) -> adjustImage());

        // Mostrar la primera pregunta si hay preguntas disponibles
        if (questions != null && !questions.isEmpty()) {
            displayCurrentQuestion();
        } else {
            System.err.println("No se han recibido preguntas para mostrar.");
        }
    }

    // Métodos para cambiar entre vistas

    /**
     * Cambia a la vista del menú principal.
     *
     * @param event Evento de acción
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    private void switchToMenu(ActionEvent event) throws IOException {
        sceneSwitcher.setRootWithEvent(event, "/menuView.fxml");
    }

    /**
     * Cambia a la vista de selección de temáticas.
     *
     * @param event Evento de acción
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    private void switchToThematicSelection(ActionEvent event) throws IOException {
        sceneSwitcher.setRootWithEvent(event, "/thematicSelectionView.fxml");
    }

    // Métodos para manejar las preguntas

    /**
     * Muestra la pregunta actual en la interfaz.
     */
    private void displayCurrentQuestion() {
        if (questions != null && currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            lblSecondPart.setText(questions.get(currentQuestionIndex)); // Mostrar el texto de la pregunta

            // Mostrar las respuestas en los botones
            int answerStartIndex = currentQuestionIndex * 3; // Cada pregunta tiene 3 respuestas
            btnFirstOption.setText(answers.get(answerStartIndex));
            btnSecondOption.setText(answers.get(answerStartIndex + 1));
            btnThirdOption.setText(answers.get(answerStartIndex + 2));

            loadQuestionImageOrThematicImage(); // Carga imagen pregunta/temática
            loadChiviImageForCurrentQuestion(); // Carga imagen chivi
        } else {
            System.err.println("Índice de pregunta fuera de rango.");
        }
    }

    /**
     * Muestra la siguiente pregunta si está disponible.
     */
    private void displayNextQuestion() {
        if (questions != null && !questions.isEmpty() && currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            txtActualQuestion.setText((currentQuestionIndex + 1) + ""); // Actualizar el número de la pregunta
            resetButtonColors(); // Restablecer los colores de los botones
            displayCurrentQuestion(); // Mostrar la pregunta actual
        } else {
            System.out.println("No hay más preguntas disponibles.");
        }
    }

    /**
     * Muestra la pregunta anterior si está disponible.
     */
    private void displayPreviousQuestion() {
        if (questions != null && !questions.isEmpty() && currentQuestionIndex > 0) {
            currentQuestionIndex--;
            txtActualQuestion.setText((currentQuestionIndex + 1) + ""); // Actualizar el número de la pregunta
            resetButtonColors(); // Restablecer los colores de los botones
            displayCurrentQuestion(); // Mostrar la pregunta actual
        } else {
            System.out.println("No hay preguntas anteriores disponibles.");
        }
    }

    // Métodos para manejar las opciones de respuesta

    /**
     * Maneja la selección de la primera opción.
     *
     * @param actionEvent Evento de acción
     */
    @FXML
    public void firstOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(1, (Button) actionEvent.getSource());
    }

    /**
     * Maneja la selección de la segunda opción.
     *
     * @param actionEvent Evento de acción
     */
    @FXML
    public void secondOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(2, (Button) actionEvent.getSource());
    }

    /**
     * Maneja la selección de la tercera opción.
     *
     * @param actionEvent Evento de acción
     */
    @FXML
    public void thirdOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(3, (Button) actionEvent.getSource());
    }

    /**
     * Valida la opción seleccionada y cambia el color del botón según sea correcta o incorrecta.
     *
     * @param selectedOption Número de la opción seleccionada
     * @param button         Botón que fue presionado
     */
    private void handleOptionSelected(int selectedOption, Button button) {
        int correctOption = getCorrectAnswerForCurrentQuestion();

        if (selectedOption == correctOption) {
            soundPlayer.playSound(getClass().getResource("/sounds/collect_points.mp3").toString());
            setButtonColor(button, "green");
        } else {
            setButtonColor(button, "red");
        }
    }

    /**
     * Obtiene la respuesta correcta para la pregunta actual desde un archivo.
     *
     * @return Número de la respuesta correcta (1, 2 o 3)
     */
    private int getCorrectAnswerForCurrentQuestion() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas()+ thematicState.getActualThematic();
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
        return -1; // Retorna un valor inválido si no se encuentra el archivo
    }

    /**
     * Cambia el color del botón seleccionado.
     *
     * @param button Botón a modificar
     * @param color  Color a aplicar (green o red)
     */
    private void setButtonColor(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Restablece los colores de los botones al color original.
     */
    private void resetButtonColors() {
        btnFirstOption.setStyle("-fx-background-color: #333333;");
        btnSecondOption.setStyle("-fx-background-color: #333333;");
        btnThirdOption.setStyle("-fx-background-color: #333333;");
    }

    // Métodos auxiliares

    /**
     * Ajusta el tamaño de la imagen de la pregunta al tamaño del panel.
     */
    public void adjustImage() {
        double paneWidth = questionPane.getWidth();
        double paneHeight = questionPane.getHeight();
        questionImg.setFitWidth(paneWidth);
        questionImg.setFitHeight(paneHeight);
    }

    /**
     * Carga la imagen "chivi" asociada a la pregunta actual.
     */
    private void loadChiviImageForCurrentQuestion() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas()+thematicState.getActualThematic();
        String chiviImagePath = thematicPath + "/ChiviTemática/chivi.png";

        File chiviImageFile = new File(chiviImagePath);

        if (chiviImageFile.exists()) {
            imgChivi.setImage(new Image(chiviImageFile.toURI().toString()));
            System.out.println("Imagen chivi cargada: " + chiviImagePath);
        } else {
            System.out.println("No se encontró imagen chivi para la pregunta actual.");
        }
    }

    /**
     * Carga la imagen de la pregunta si existe, si no, carga la imagen de la temática.
     */
    private void loadQuestionImageOrThematicImage() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas() + thematicState.getActualThematic();
        String questionPath = thematicPath + "/Pregunta" + (currentQuestionIndex + 1);

        // Extensiones soportadas
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
            // Imagen de la temática
            File thematicImage = new File(thematicPath + "/ImagenTemática/imagen.png");
            if (thematicImage.exists()) {
                questionImg.setImage(new Image(thematicImage.toURI().toString()));
            } else {
                // Imagen por defecto si tampoco hay imagen de temática
                questionImg.setImage(new Image(getClass().getResource("/images/default.png").toString()));
            }
        }
    }

    private void loadQuestionsAndAnswersFromThematic() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas() + thematicState.getActualThematic();

        // Limpiar listas para evitar duplicados si se llama varias veces
        questions.clear();
        answersFiles.clear();
        answers.clear();

        // Cargar preguntas y respuestas
        for (int i = 1; i <= 6; i++) { // Cambia 6 por el número de preguntas por temática
            String questionPath = thematicPath + "/Pregunta" + i;

            // Cargar la pregunta
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

            // Cargar las respuestas
            for (int j = 1; j <= 3; j++) { // Cambia 3 por el número de respuestas por pregunta
                File answerFile = new File(questionPath + "/Respuesta" + j + ".txt");
                if (answerFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(answerFile))) {
                        String answer = reader.readLine();
                        if (answer != null && !answer.isBlank()) {
                            answers.add(answer);
                        } else {
                            System.err.println("El archivo de la respuesta está vacío: " + answerFile.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        System.err.println("Error al leer el archivo de la respuesta: " + answerFile.getAbsolutePath());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("El archivo de la respuesta no existe: " + answerFile.getAbsolutePath());
                }
            }
        }
    }

    // Métodos para actualizar los puntos de los equipos

    /**
     * Incrementa o decrementa los puntos del equipo púrpura.
     *
     * @param event Evento del mouse
     */
    @FXML
    private void upgradePurplePoints(MouseEvent event) {
        updateTeamPoints(event, points::getPurpleTeamPoints, points::setPurpleTeamPoints, txtPurpleTeam);
    }

    /**
     * Incrementa o decrementa los puntos del equipo verde.
     *
     * @param event Evento del mouse
     */
    @FXML
    private void upgradeGreenPoints(MouseEvent event) {
        updateTeamPoints(event, points::getGreenTeamPoints, points::setGreenTeamPoints, txtGreenTeam);
    }

    /**
     * Incrementa o decrementa los puntos del equipo rojo.
     *
     * @param event Evento del mouse
     */
    @FXML
    public void upgradeRedPoints(MouseEvent event) {
        updateTeamPoints(event, points::getRedTeamPoints, points::setRedTeamPoints, txtRedTeam);
    }

    /**
     * Incrementa o decrementa los puntos del equipo azul.
     *
     * @param event Evento del mouse
     */
    @FXML
    private void upgradeBluePoints(MouseEvent event) {
        updateTeamPoints(event, points::getBlueTeamPoints, points::setBlueTeamPoints, txtBlueTeam);
    }

    /**
     * Actualiza los puntos de un equipo según el botón del mouse presionado.
     *
     * @param event  Evento del mouse
     * @param getter Función para obtener los puntos actuales
     * @param setter Función para establecer los nuevos puntos
     * @param label  Etiqueta para mostrar los puntos actualizados
     */
    private void updateTeamPoints(MouseEvent event, java.util.function.Supplier<Integer> getter,
                                  java.util.function.Consumer<Integer> setter, Label label) {
        if (event.getButton() == MouseButton.PRIMARY) {
            setter.accept(getter.get() + 1);
        } else if (event.getButton() == MouseButton.SECONDARY && getter.get() != 0) {
            setter.accept(getter.get() - 1);
        }
        label.setText("" + getter.get());
    }

}
