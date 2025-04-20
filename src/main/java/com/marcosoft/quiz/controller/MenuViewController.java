package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.Main;
import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.DatabaseInitializer;
import com.marcosoft.quiz.utils.DirectoriesCreator;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
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

    @FXML
    private Button btnQuit, btnStart, btnConfiguration;
    @FXML
    private Label title, txtVersion;

    @FXML
    public void initialize() throws IOException {
        databaseInitializer.init();
        directoriesCreator.createAllDirectoriesForTheQuiz();

        txtVersion.setText("Alfa");
        restartPoints();

        if(!clientServiceImpl.existsClientByClientId(1)){
            Client client= new Client(1, "1000x660", 1, true, DirectoriesCreator.getBasePath());
            clientServiceImpl.save(client);
        }




        //Set FullScreen by Configuration stored in Database
        if(clientServiceImpl.getClientById(1).getModoPantalla() ==2){
            Main.getPrimaryStage().setFullScreen(true);
        } else if(clientServiceImpl.getClientById(1).getModoPantalla() ==1) {
            Main.getPrimaryStage().setFullScreen(false);
        }
        //Set Resolution by configuration stored in database
        if(clientServiceImpl.getClientById(1).getResolucion().equals("1000x660")){
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

        // Leer el contenido del archivo "nombre.txt" de la carpeta "NombreTemática" de "Temática1"
        String filePath = DirectoriesCreator.getBasePath() + "/Temática1/NombreTemática/nombre.txt";
        File file = new File(filePath);


        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String content = reader.readLine(); // Leer la primera línea del archivo
                txtVersion.setText(content); // Establecer el contenido en txtVersion
                System.out.println("Contenido de nombre.txt: " + content);
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + filePath);
                e.printStackTrace();
            }
        } else {
            System.err.println("El archivo no existe: " + filePath);
        }


    }                    

    @FXML
    public void quit(ActionEvent actionEvent) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void switchToRouletteView(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRoot(actionEvent, "/questionView.fxml");
    }

    @FXML
    public void switchToConfiguration(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRoot(actionEvent, "/configurationView.fxml");
    }

    public void restartPoints() {
        points.setGreenTeamPoints(0);
        points.setBlueTeamPoints(0);
        points.setRedTeamPoints(0);
        points.setPurpleTeamPoints(0);
    }

}
