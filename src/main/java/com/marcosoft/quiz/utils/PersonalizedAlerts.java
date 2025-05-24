package com.marcosoft.quiz.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Component;

@Component
public class PersonalizedAlerts {


    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param title Título de la ventana
     * @param header Mensaje de la cabecera
     * @param message Mensaje de error a mostrar.
     */
    public void showError(String title, String header, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.getDialogPane().setPrefWidth(600); // Ajustar el ancho del cuadro de diálogo si el mensaje es largo
            alert.showAndWait();
        });
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param title Título de la ventana
     * @param header Mensaje de la cabecera
     * @param message Mensaje de información a mostrar.
     */
    public void showInformation(String title,String header,String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.getDialogPane().setPrefWidth(600); // Ajustar el ancho del cuadro de diálogo si el mensaje es largo
            alert.showAndWait();
        });
    }
}
