package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.Main;
import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.DirectoriesCreator;
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

/**
 * The type Configuration view controller.
 */
@Controller
public class ConfigurationViewController {

    // =======================
    // Inyección de dependencias y nodos FXML
    // =======================

    @FXML
    private TextField txtPath;
    @FXML
    private ToggleGroup windowMode, resolution;
    @FXML
    private RadioMenuItem rmi1920x1080, rmiFullScreen, rmiWindow, rmi1000x600, rmi1280x720;
    @FXML
    private Label txtThematicQuantity, txtQuestionQuantity;
    @FXML
    private MenuItem miQuestion8, miThematic6, miQuestion6, miThematic8, miQuestion4, miQuestion10, miThematic10, miThematic4;

    @Autowired
    private ClientServiceImpl clientService;
    @Autowired
    private SceneSwitcher sceneSwitcher;
    @Autowired
    private DirectoriesCreator directoriesCreator;


    /**
     * The Client.
     */
    Client client;


    // =======================
    // Inicialización de la vista
    // =======================

    /**
     * Initialize.
     */
    @FXML
    void initialize() {
        client = clientService.getClientById(1);
        txtPath.setText(client.getFolderPath());
        initQuestionAndThematicLabel();
        initWindowModeConfig();
        initResolutionConfig();

    }

    private void initQuestionAndThematicLabel() {
        txtThematicQuantity.setText(client.getThematicNumber() + "");
        txtQuestionQuantity.setText(client.getQuestionNumber() + "");
    }

    private void initWindowModeConfig() {
        // Configuración del modo de pantalla
        if (client.getWindowMode() == 1) {
            rmiFullScreen.setSelected(false);
            rmiWindow.setSelected(true);
        } else {
            rmiFullScreen.setSelected(true);
            rmiWindow.setSelected(false);
        }
    }

    private void initResolutionConfig() {
        // Configuración de la resolución
        switch (client.getResolution()) {
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

    // =======================
    // Cambiar ruta de carpetas
    // =======================

    @FXML
    private void changePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar directorio");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Carpetas", "*.dir"));
        File selectedFile = fileChooser.showOpenDialog(
                ((Button) event.getTarget()).getParent().getScene().getWindow()
        );
        if (selectedFile != null) {
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
            // Aquí puedes agregar la lógica para actualizar la ruta si lo deseas
        }
    }

    // =======================
    // Navegación
    // =======================

    @FXML
    private void switchToMenu(ActionEvent actionEvent) throws IOException {
        sceneSwitcher.setRootWithEvent(actionEvent, "/menuView.fxml");
    }

    // =======================
    // Configuración de pantalla y resolución
    // =======================

    /**
     * Sets full screen.
     *
     * @param event the event
     */
    @FXML
    public void setFullScreen(Event event) {
        clientService.updateModoPantallaById(2, 1);
        Main.primaryStage.setFullScreen(true);
    }

    /**
     * Sets windows.
     *
     * @param event the event
     */
    @FXML
    public void setWindows(Event event) {
        clientService.updateModoPantallaById(1, 1);
        Main.primaryStage.setFullScreen(false);
    }

    /**
     * Sets 1920 x 1080.
     *
     * @param event the event
     */
    @FXML
    public void set1920x1080(Event event) {
        clientService.updateResolucionById("1920x1080", 1);
        Main.primaryStage.setHeight(1080);
        Main.primaryStage.setWidth(1920);
    }

    /**
     * Sets 1280 x 720.
     *
     * @param event the event
     */
    @FXML
    public void set1280x720(Event event) {
        clientService.updateResolucionById("1280x720", 1);
        Main.primaryStage.setHeight(720);
        Main.primaryStage.setWidth(1280);
    }

    /**
     * Sets 1000 x 600.
     *
     * @param event the event
     */
    @FXML
    public void set1000x600(Event event) {
        clientService.updateResolucionById("1000x660", 1);
        Main.primaryStage.setHeight(660);
        Main.primaryStage.setWidth(1000);
    }

    private void updateQuestionNumber(int number) {
        txtQuestionQuantity.setText(String.valueOf(number));
        clientService.updateQuestionNumberById(Integer.parseInt(txtQuestionQuantity.getText()), 1);
    }

    /**
     * Sets question 10.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setQuestion10(ActionEvent actionEvent) {
        updateQuestionNumber(10);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }

    /**
     * Sets question 8.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setQuestion8(ActionEvent actionEvent) {
        updateQuestionNumber(8);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }

    /**
     * Sets question 6.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setQuestion6(ActionEvent actionEvent) {
        updateQuestionNumber(6);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }

    /**
     * Sets question 4.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setQuestion4(ActionEvent actionEvent) {
        updateQuestionNumber(4);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }

    private void updateThematicNumber(int number) {
        txtThematicQuantity.setText(number + "");
        clientService.updateThematicNumberById(Integer.parseInt(txtThematicQuantity.getText()), 1);
    }

    /**
     * Sets thematic 10.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setThematic10(ActionEvent actionEvent) {
        updateThematicNumber(10);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }

    /**
     * Sets thematic 8.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setThematic8(ActionEvent actionEvent) {
        updateThematicNumber(8);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }

    /**
     * Sets thematic 6.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setThematic6(ActionEvent actionEvent) {
        updateThematicNumber(6);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }

    /**
     * Sets thematic 4.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void setThematic4(ActionEvent actionEvent) {
        updateThematicNumber(4);
        directoriesCreator.createAllDirectoriesForTheQuiz();
    }
}
