package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LeaderboardViewController {

    @Autowired
    private Points points;

    @FXML
    private BarChart<String, Number> leaderboard;

    @FXML
    private void initialize() {
        setupBarChartWithAnimation();
    }

    // Configura el gráfico de barras con animación
    private void setupBarChartWithAnimation() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Agregar datos iniciales con valor 0 para la animación
        series.getData().add(new XYChart.Data<>("Rojo", 0));
        series.getData().add(new XYChart.Data<>("Azul", 0));
        series.getData().add(new XYChart.Data<>("Púrpura", 0));
        series.getData().add(new XYChart.Data<>("Verde", 0));

        leaderboard.getData().add(series);

        // Animar los puntos del equipo con menor puntuación primero
        animateBarChart(series);
    }

    // Método para animar el gráfico de barras
    private void animateBarChart(XYChart.Series<String, Number> series) {
        int[] teamPoints = {
            points.getRedTeamPoints(),
            points.getBlueTeamPoints(),
            points.getPurpleTeamPoints(),
            points.getGreenTeamPoints()
        };

        String[] teamNames = {"Rojo", "Azul", "Púrpura", "Verde"};
        String[] colors = {"#ff0000", "#2200ff", "#bb0ada", "#14b905"};

        // Encontrar el equipo con menor puntuación
        int minIndex = 0;
        for (int i = 1; i < teamPoints.length; i++) {
            if (teamPoints[i] < teamPoints[minIndex]) {
                minIndex = i;
            }
        }

        // Animar el equipo con menor puntuación primero
        Timeline timeline = new Timeline();
        for (int i = 0; i < teamPoints.length; i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i + 1), e -> {
                series.getData().get(index).setYValue(teamPoints[index]);
                customizeBarColor(series.getData().get(index), colors[index]);
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
    }

    // Personaliza el color de una barra específica
    private void customizeBarColor(XYChart.Data<String, Number> data, String color) {
        data.nodeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.setStyle("-fx-bar-fill: " + color + ";");
            }
        });
    }
}
