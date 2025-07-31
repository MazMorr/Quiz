package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.model.ThematicState;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.PersonalizedAlerts;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The type Thematic selection view controller.
 */
@Controller
public class ThematicSelectionViewController {

    // =======================
    // Inyección de dependencias y nodos FXML
    // =======================

    @Autowired
    private Points points;
    @Autowired
    private ThematicState thematicState;
    @Autowired
    private ClientServiceImpl clientService;
    @Autowired
    private SceneSwitcher sceneSwitcher;
    @Autowired
    private PersonalizedAlerts personalizedAlerts;

    @FXML
    private Label txtFirstOption, txtSecondOption, txtGreenTeam, txtPurpleTeam, txtRedTeam, txtBlueTeam;
    @FXML
    private ImageView imgFirstOption, imgSecondOption;
    @FXML
    private Button btnFirstOption, btnSecondOption;

    /**
     * The Thematic displayed 1.
     */
    public String thematicDisplayed1;
    /**
     * The Thematic displayed 2.
     */
    public String thematicDisplayed2;

    // Proceso para la voz de Cortana
    private Process cortanaVoiceProcess = null;

    // =======================
    // Inicialización
    // =======================

    @FXML
    private void initialize() {
        setPoints();
        Platform.runLater(this::displayAllTheThematics);
    }

    /**
     * Sets points.
     */
    public void setPoints() {
        txtBlueTeam.setText(String.valueOf(points.getBlueTeamPoints()));
        txtGreenTeam.setText(String.valueOf(points.getGreenTeamPoints()));
        txtPurpleTeam.setText(String.valueOf(points.getPurpleTeamPoints()));
        txtRedTeam.setText(String.valueOf(points.getRedTeamPoints()));
    }

    // =======================
    // Selección de temáticas
    // =======================

    private void displayAllTheThematics() {
        List<String> availableThematics = getAvailableThematics();

        if (thematicState.getThematicsSelectedCounter() == clientService.getClientById(1).getThematicNumber()) {
            goToLeaderboard();
        } else if (availableThematics.size() == 1) {
            handleSingleThematic(availableThematics.getFirst());
        } else if (availableThematics.size() == 2) {
            handleTwoThematics(availableThematics);
        } else {
            handleMultipleThematics(availableThematics);
        }
    }

    private List<String> getAvailableThematics() {
        List<String> available = new ArrayList<>();
        int total = clientService.getClientById(1).getThematicNumber();
        for (int i = 1; i <= total; i++) {
            String thematicName = "Temática" + i;
            if (!thematicState.isThematicSelected(thematicName)) {
                available.add(thematicName);
            }
        }
        return available;
    }

    private void goToLeaderboard() {
        try {
            System.out.println("No quedan temáticas disponibles. Cambiando a la vista del leaderboard...");
            sceneSwitcher.setRoot(btnFirstOption, "/leaderboardView.fxml");
        } catch (IOException e) {
            personalizedAlerts.showError("Error", "Cambio de ventana", "Error al cambiar a la vista del leaderboard: " + e.getMessage());
        }
    }

    private void handleSingleThematic(String remainingThematic) {
        thematicState.selectThematic(remainingThematic);
        thematicState.setActualThematic(remainingThematic);

        try {
            String contentText = cortanaStyleMessage(remainingThematic);
            speakWithCortanaVoice(contentText);
            Alert alert= new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(contentText);
            alert.setHeaderText("Selección de Temáticas");
            alert.setTitle("Cortana");
            alert.showAndWait();
            stopCortanaVoice();

            sceneSwitcher.setRoot(btnFirstOption, "/questionView.fxml");
        } catch (IOException e) {
            personalizedAlerts.showError("Error", "cambio de Ventana", "Error al cambiar a la vista de preguntas: " + e.getMessage());
        }
    }

    private void handleTwoThematics(List<String> availableThematics) {
        String[] thematics = selectRandomThematics(availableThematics);
        thematicDisplayed1 = thematics[0];
        thematicDisplayed2 = thematics[1];

        txtFirstOption.setText(readThematicName(thematicDisplayed1));
        txtSecondOption.setText(readThematicName(thematicDisplayed2));
        imgFirstOption.setImage(loadThematicImage(thematicDisplayed1));
        imgSecondOption.setImage(loadThematicImage(thematicDisplayed2));
    }

    private void handleMultipleThematics(List<String> availableThematics) {
        btnFirstOption.setDisable(true);
        btnSecondOption.setDisable(true);

        Timeline timeline = new Timeline();

        for (int i = 0; i < 25; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 0.1), e -> {
                try {
                    String[] thematics = selectRandomThematics(availableThematics);
                    txtFirstOption.setText(readThematicName(thematics[0]));
                    txtSecondOption.setText(readThematicName(thematics[1]));
                    imgFirstOption.setImage(loadThematicImage(thematics[0]));
                    imgSecondOption.setImage(loadThematicImage(thematics[1]));
                } catch (Exception ex) {
                    personalizedAlerts.showError("Error", "Selección de Temáticas", "Error durante la selección de temáticas: " + ex.getMessage());
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setOnFinished(e -> {
            try {
                System.out.println("Finalizando selección de temáticas...");
                String[] thematics = selectRandomThematics(availableThematics);
                thematicDisplayed1 = thematics[0];
                thematicDisplayed2 = thematics[1];

                txtFirstOption.setText(readThematicName(thematicDisplayed1));
                txtSecondOption.setText(readThematicName(thematicDisplayed2));
                imgFirstOption.setImage(loadThematicImage(thematicDisplayed1));
                imgSecondOption.setImage(loadThematicImage(thematicDisplayed2));

                btnFirstOption.setDisable(false);
                btnSecondOption.setDisable(false);

            } catch (Exception ex) {
                personalizedAlerts.showError("Error", "Finalización de temática", "Error al finalizar la selección de temáticas: " + ex.getMessage());
            }
        });

        timeline.play();
    }

    // =======================
    // Selección de opciones
    // =======================

    /**
     * First option selected.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    void firstOptionSelected(ActionEvent event) throws IOException {
        selectThematic(thematicDisplayed1, event);
    }

    /**
     * Second option selected.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    void secondOptionSelected(ActionEvent event) throws IOException {
        selectThematic(thematicDisplayed2, event);
    }

    private void selectThematic(String thematic, ActionEvent event) throws IOException {
        thematicState.selectThematic(thematic);
        thematicState.setActualThematic(thematic);
        System.out.println("Temática seleccionada: " + readThematicName(thematic));
        sceneSwitcher.setRootWithEvent(event, "/questionView.fxml");
    }

    // =======================
    // Puntos de los equipos
    // =======================

    @FXML
    private void upgradePurplePoints(MouseEvent event) {
        updateTeamPoints(event, points::getPurpleTeamPoints, points::setPurpleTeamPoints, txtPurpleTeam);
    }

    @FXML
    private void upgradeGreenPoints(MouseEvent event) {
        updateTeamPoints(event, points::getGreenTeamPoints, points::setGreenTeamPoints, txtGreenTeam);
    }

    /**
     * Upgrade red points.
     *
     * @param event the event
     */
    @FXML
    public void upgradeRedPoints(MouseEvent event) {
        updateTeamPoints(event, points::getRedTeamPoints, points::setRedTeamPoints, txtRedTeam);
    }

    @FXML
    private void upgradeBluePoints(MouseEvent event) {
        updateTeamPoints(event, points::getBlueTeamPoints, points::setBlueTeamPoints, txtBlueTeam);
    }

    private void updateTeamPoints(MouseEvent event, java.util.function.Supplier<Integer> getter,
                                  java.util.function.Consumer<Integer> setter, Label label) {
        if (event.getButton() == MouseButton.PRIMARY) {
            setter.accept(getter.get() + 1);
        } else if (event.getButton() == MouseButton.SECONDARY && getter.get() != 0) {
            setter.accept(getter.get() - 1);
        }
        label.setText(String.valueOf(getter.get()));
    }

    // =======================
    // Utilidades de temáticas
    // =======================

    private String[] selectRandomThematics(List<String> availableThematics) {
        Random random = new Random();
        String thematic1, thematic2;
        do {
            thematic1 = availableThematics.get(random.nextInt(availableThematics.size()));
            thematic2 = availableThematics.get(random.nextInt(availableThematics.size()));
        } while (thematic1.equals(thematic2));
        return new String[]{thematic1, thematic2};
    }

    private String readThematicName(String thematic) {
        String filePath = clientService.getClientById(1).getFolderPath() + "/" + thematic + "/NombreTemática/nombre.txt";
        File file = new File(filePath);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line == null || line.isBlank()) {
                    System.out.println("El nombre de la temática: " + thematic + " está vacío");
                    return "Nombre desconocido";
                }
                return line;
            } catch (IOException e) {
                personalizedAlerts.showError("Error", "", "Error al leer el archivo: " + filePath);
            }
        } else {
            personalizedAlerts.showError("Error", "", "El archivo no existe: " + filePath);
        }
        return "Nombre desconocido";
    }

    private Image loadThematicImage(String thematic) {
        String imagePath = clientService.getClientById(1).getFolderPath() + "/" + thematic + "/ImagenTemática/imagen.png";
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            return new Image(imageFile.toURI().toString());
        } else {
            System.err.println("La imagen no existe: " + imagePath + ". Usando imagen por defecto.");
        }
        return new Image(getClass().getResource("/images/default.png").toString());
    }

    // =======================
    // Utilidades de voz y errores
    // =======================

    private String cortanaStyleMessage(String remainingThematic) {
        return "Última temática: " + readThematicName(remainingThematic) + ", procederé a seleccionarla automáticamente.";
    }

    private void speakWithCortanaVoice(String text) {
        try {
            String command = "PowerShell -Command \"Add-Type –AssemblyName System.Speech; " +
                    "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                    "$speak.Speak('" + text.replace("'", "''") + "');\"";
            cortanaVoiceProcess = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void stopCortanaVoice() {
        if (cortanaVoiceProcess != null && cortanaVoiceProcess.isAlive()) {
            cortanaVoiceProcess.destroy();
            cortanaVoiceProcess = null;
        }
    }


}
