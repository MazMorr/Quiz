package com.marcosoft.quiz.utils;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SceneSwitcher {

    private final SpringFXMLLoader springFXMLLoader;

    @Autowired
    public SceneSwitcher(SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
    }

    public void setRootWithEvent(ActionEvent event, String fxmlFile) throws IOException {
        // Usar SpringFXMLLoader para cargar el archivo FXML
        Parent root = (Parent) springFXMLLoader.load(fxmlFile);

        // Obtener la escena actual y establecer el nuevo root
        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);
    }

    public void setRoot(Button button, String fxmlFile) throws IOException {
        // Usar SpringFXMLLoader para cargar el archivo FXML
        Parent root = (Parent) springFXMLLoader.load(fxmlFile);

        Scene currentScene = button.getScene();
        currentScene.setRoot(root);
    }
}
