package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.utils.PersonalizedAlerts;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class LeaderboardViewController {

    @Autowired
    private Points points;
    @Autowired
    private SceneSwitcher sceneSwitcher;
    @Autowired
    private PersonalizedAlerts personalizedAlerts;
    @FXML
    private BarChart<String, Number> leaderboard;

    private CategoryAxis getCategoryAxis() {
        return (CategoryAxis) leaderboard.getXAxis();
    }

    @FXML
    private void initialize() {
        setupBarChartWithAnimation();
        initBarChartWithTransparentBackground();
    }

    private void initBarChartWithTransparentBackground() {
        // Fondo transparente para el BarChart y el área de trazado
        leaderboard.setStyle("-fx-background-color: transparent;");

        // Fondo transparente para el área de trazado (plot)
        leaderboard.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
    }

    private void setupBarChartWithAnimation() {
        // Obtener los puntos y nombres de los equipos
        int[] teamPoints = {
                points.getRedTeamPoints(),
                points.getBlueTeamPoints(),
                points.getPurpleTeamPoints(),
                points.getGreenTeamPoints()
        };
        String[] teamNames = {"Rojo", "Azul", "Púrpura", "Verde"};
        String[] colors = {"#ff0000", "#2200ff", "#bb0ada", "#14b905"};

        // Crear lista de índices y ordenarla por puntuación ascendente
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < teamPoints.length; i++) {
            indices.add(i);
        }
        indices.sort(Comparator.comparingInt(i -> teamPoints[i]));

        // Cambiar el orden de las categorías del eje X
        List<String> orderedCategories = new ArrayList<>();
        for (int idx : indices) {
            orderedCategories.add(teamNames[idx]);
        }
        getCategoryAxis().setCategories(javafx.collections.FXCollections.observableArrayList(orderedCategories));

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Agregar datos en orden ascendente de puntuación
        for (int idx : indices) {
            series.getData().add(new XYChart.Data<>(teamNames[idx], 0));
        }

        leaderboard.getData().clear();
        leaderboard.getData().add(series);

        // Animar las barras y mostrar el valor encima de cada barra
        animateBarChartWithLabels(series, teamPoints, teamNames, colors, indices);
    }

    // Animar y mostrar el valor encima de cada barra
    private void animateBarChartWithLabels(XYChart.Series<String, Number> series, int[] teamPoints, String[] teamNames, String[] colors, List<Integer> indices) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < indices.size(); i++) {
            final int index = i; // posición en la serie ordenada
            final int originalIdx = indices.get(i); // índice original
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i + 1), e -> {
                XYChart.Data<String, Number> data = series.getData().get(index);
                data.setYValue(teamPoints[originalIdx]);
                customizeBarColor(data, colors[originalIdx]);

                // Mostrar el valor encima de la barra
                Label label = new Label(String.valueOf(teamPoints[originalIdx]));
                label.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 80; -fx-font-family: \"Mortal Kombat 2\";");
                // Esperar a que el nodo esté disponible
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        StackPane stack = (StackPane) newNode;
                        stack.getChildren().add(label);
                    }
                });
                if (data.getNode() != null) {
                    StackPane stack = (StackPane) data.getNode();
                    stack.getChildren().add(label);
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
    }

    // Personaliza el color de una barra específica
    private void customizeBarColor(XYChart.Data<String, Number> data, String color) {
        if (data.getNode() != null) {
            data.getNode().setStyle("-fx-bar-fill: " + color + ";");
        } else {
            data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.setStyle("-fx-bar-fill: " + color + ";");
                }
            });
        }
    }

    @FXML
    public void switchToMenu(ActionEvent actionEvent) {
        try {
            sceneSwitcher.setRootWithEvent(actionEvent, "/menuView.fxml");
        } catch (IOException e) {
            personalizedAlerts.showError("Error", "Cambio de ventana", "Error al cambiar a la ventana del menú");
        }
    }
}
