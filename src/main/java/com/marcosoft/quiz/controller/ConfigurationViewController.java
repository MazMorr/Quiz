package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.Main;
import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
public class ConfigurationViewController {
    @FXML
    private TextField txtPath;
    @FXML
    private ToggleGroup windowMode, resolution;
    @FXML
    private RadioMenuItem rmi1920x1080, rmiFullScreen, rmiVentana, rmi1000x600, rmi1280x720;
    @Autowired
    ClientServiceImpl clientService;
    @Autowired
    SceneSwitcher sceneSwitcher;

    @FXML
    void initialize(){
        txtPath.setText(clientService.getClientById(1).getRutaCarpetas());

        if(clientService.getClientById(1).getModoPantalla()==1){
            rmiFullScreen.setSelected(false);
            rmiVentana.setSelected(true);
        }else{
            rmiFullScreen.setSelected(true);
            rmiVentana.setSelected(false);
        }

        switch(clientService.getClientById(1).getResolucion()){
            case "1000x660":
                rmi1000x600.setSelected(true);
                System.out.println("La opción 1000x660 está puesta");
                break;
            case "1280x720":
                rmi1280x720.setSelected(true);
                break;
            case "1920x1080":
                rmi1920x1080.setSelected(true);
                break;
            default:
                System.err.println("Ninguna de las opciones predispuestas está almacenada");
                break;
        }
    }

    @FXML
    private void changePath(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar directorio");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Carpetas", "*.dir"));
        File selectedFile = fileChooser.showOpenDialog(((Button) event.getTarget()).getParent().getScene().getWindow());
        if (selectedFile != null) {
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());

        }

    }

    @FXML
    private void switchToMenu(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRoot(actionEvent, "/menuView.fxml");
    }

    @FXML
    public void setFullScreen(Event event) {
        clientService.updateModoPantallaById(2, 1);
        Main.getPrimaryStage().setFullScreen(true);
    }

    @FXML
    public void set1920x1080(Event event) {
        clientService.updateResolucionById("1920x1080",1);
        Main.getPrimaryStage().setHeight(1080);
        Main.getPrimaryStage().setWidth(1920);
    }

    @FXML
    public void set1280x720(Event event) {
        clientService.updateResolucionById("1280x720",1);
        Main.getPrimaryStage().setHeight(720);
        Main.getPrimaryStage().setWidth(1280);
    }

    @FXML
    public void set1000x600(Event event) {
        clientService.updateResolucionById("1000x660",1);
        Main.getPrimaryStage().setHeight(660);
        Main.getPrimaryStage().setWidth(1000);
    }

    @FXML
    public void setWindows(Event event) {
        clientService.updateModoPantallaById(1, 1);
        Main.getPrimaryStage().setFullScreen(false);
    }
}
