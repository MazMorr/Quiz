package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.SceneSwitcher;
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
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
    @Setter
    private List<String> questions; // Lista de preguntas cargadas
    private int currentQuestionIndex = 0; // Índice de la pregunta actual

    /**
     * Inicialización de la vista.
     * Configura eventos de teclado, listeners para redimensionar la ventana
     * y muestra la primera pregunta si está disponible.
     */
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
     * @param event Evento de acción
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    private void switchToMenu(ActionEvent event) throws IOException {
        sceneSwitcher.setRoot(event, "/menuView.fxml");
    }

    /**
     * Cambia a la vista de selección de temáticas.
     * @param event Evento de acción
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    private void switchToThematicSelection(ActionEvent event) throws IOException {
        sceneSwitcher.setRoot(event, "/thematicSelectionView.fxml");
    }

    // Métodos para manejar las preguntas

    /**
     * Muestra la pregunta actual en la interfaz.
     */
    private void displayCurrentQuestion() {
        if (questions != null && currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            lblSecondPart.setText(questions.get(currentQuestionIndex)); // Mostrar el texto de la pregunta
            loadChiviImageForCurrentQuestion(); // Cargar la imagen "chivi" asociada
        } else {
            System.err.println("Índice de pregunta fuera de rango.");
        }
    }

    /**
     * Muestra la siguiente pregunta si está disponible.
     */
    private void displayNextQuestion() {
        if (questions != null && currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            resetButtonColors(); // Restablecer los colores de los botones
            displayCurrentQuestion();
        } else {
            System.out.println("No hay más preguntas.");
        }
    }

    /**
     * Muestra la pregunta anterior si está disponible.
     */
    private void displayPreviousQuestion() {
        if (questions != null && currentQuestionIndex > 0) {
            currentQuestionIndex--;
            resetButtonColors(); // Restablecer los colores de los botones
            displayCurrentQuestion();
        } else {
            System.out.println("No hay preguntas anteriores.");
        }
    }

    // Métodos para manejar las opciones de respuesta

    /**
     * Maneja la selección de la primera opción.
     * @param actionEvent Evento de acción
     */
    @FXML
    public void firstOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(1, (Button) actionEvent.getSource());
    }

    /**
     * Maneja la selección de la segunda opción.
     * @param actionEvent Evento de acción
     */
    @FXML
    public void secondOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(2, (Button) actionEvent.getSource());
    }

    /**
     * Maneja la selección de la tercera opción.
     * @param actionEvent Evento de acción
     */
    @FXML
    public void thirdOptionSelected(ActionEvent actionEvent) {
        handleOptionSelected(3, (Button) actionEvent.getSource());
    }

    /**
     * Valida la opción seleccionada y cambia el color del botón según sea correcta o incorrecta.
     * @param selectedOption Número de la opción seleccionada
     * @param button Botón que fue presionado
     */
    private void handleOptionSelected(int selectedOption, Button button) {
        int correctOption = getCorrectAnswerForCurrentQuestion();

        if (selectedOption == correctOption) {
            setButtonColor(button, "green");
        } else {
            setButtonColor(button, "red");
        }
    }

    /**
     * Obtiene la respuesta correcta para la pregunta actual desde un archivo.
     * @return Número de la respuesta correcta (1, 2 o 3)
     */
    private int getCorrectAnswerForCurrentQuestion() {
        String thematicPath = clientService.getClientById(1).getRutaCarpetas();
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
     * @param button Botón a modificar
     * @param color Color a aplicar (green o red)
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
        String thematicPath = clientService.getClientById(1).getRutaCarpetas();
        String chiviImagePath = thematicPath + "/Pregunta" + (currentQuestionIndex + 1) + "/chivi.png";

        File chiviImageFile = new File(chiviImagePath);

        if (chiviImageFile.exists()) {
            imgChivi.setImage(new Image(chiviImageFile.toURI().toString()));
            System.out.println("Imagen chivi cargada: " + chiviImagePath);
        } else {
            imgChivi.setImage(null);
            System.out.println("No se encontró imagen chivi para la pregunta actual.");
        }
    }

    // Métodos para actualizar los puntos de los equipos

    /**
     * Incrementa o decrementa los puntos del equipo púrpura.
     * @param event Evento del mouse
     */
    @FXML
    private void upgradePurplePoints(MouseEvent event) {
        updateTeamPoints(event, points::getPurpleTeamPoints, points::setPurpleTeamPoints, txtPurpleTeam);
    }

    /**
     * Incrementa o decrementa los puntos del equipo verde.
     * @param event Evento del mouse
     */
    @FXML
    private void upgradeGreenPoints(MouseEvent event) {
        updateTeamPoints(event, points::getGreenTeamPoints, points::setGreenTeamPoints, txtGreenTeam);
    }

    /**
     * Incrementa o decrementa los puntos del equipo rojo.
     * @param event Evento del mouse
     */
    @FXML
    public void upgradeRedPoints(MouseEvent event) {
        updateTeamPoints(event, points::getRedTeamPoints, points::setRedTeamPoints, txtRedTeam);
    }

    /**
     * Incrementa o decrementa los puntos del equipo azul.
     * @param event Evento del mouse
     */
    @FXML
    private void upgradeBluePoints(MouseEvent event) {
        updateTeamPoints(event, points::getBlueTeamPoints, points::setBlueTeamPoints, txtBlueTeam);
    }

    /**
     * Actualiza los puntos de un equipo según el botón del mouse presionado.
     * @param event Evento del mouse
     * @param getter Función para obtener los puntos actuales
     * @param setter Función para establecer los nuevos puntos
     * @param label Etiqueta para mostrar los puntos actualizados
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
