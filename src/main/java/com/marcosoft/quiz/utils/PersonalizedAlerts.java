package com.marcosoft.quiz.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Component;

@Component
public class PersonalizedAlerts {


    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param message Mensaje de error a mostrar.
     */
    public void showError(String message) {
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
