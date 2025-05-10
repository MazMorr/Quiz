package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.model.ThematicState;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class ThematicSelectionViewController {

    @Autowired
    private Points points;
    @Autowired
    private ThematicState thematicState;
    @Autowired
    private ClientServiceImpl clientService;
    @Autowired
    private SceneSwitcher sceneSwitcher;
    @FXML
    private Label txtFirstOption, txtSecondOption, txtGreenTeam, txtPurpleTeam, txtRedTeam, txtBlueTeam;
    @FXML
    private ImageView imgFirstOption, imgSecondOption;
    @FXML
    private Button btnFirstOption, btnSecondOption;

    public String thematicDisplayed1;
    public String thematicDisplayed2;

    // Guarda el proceso de la voz para poder detenerlo después
    private Process cortanaVoiceProcess = null;

    @FXML
    private void initialize() {
        setPoints();
        // Iniciar la selección de temáticas
        Platform.runLater(() -> {

            displayAllTheThematics();
        });
    }

    public void setPoints() {
        txtBlueTeam.setText(points.getBlueTeamPoints() + "");
        txtGreenTeam.setText(points.getGreenTeamPoints() + "");
        txtPurpleTeam.setText(points.getPurpleTeamPoints() + "");
        txtRedTeam.setText(points.getRedTeamPoints() + "");
    }


    @FXML
    void firstOptionSelected(ActionEvent event) throws IOException {
        String selectedThematic = txtFirstOption.getText();
        String pathToSelectedThematic = thematicDisplayed1;
        if (thematicDisplayed1.equals("Temática4")) {
            thematicState.setThematic4selected(true);
        }
        if (thematicDisplayed1.equals("Temática3")) {
            thematicState.setThematic3selected(true);
        }
        if (thematicDisplayed1.equals("Temática2")) {
            thematicState.setThematic2selected(true);
        }
        if (thematicDisplayed1.equals("Temática1")) {
            thematicState.setThematic1selected(true);
        }

        thematicState.actualThematic(pathToSelectedThematic);
        System.out.println("Temática seleccionada: " + selectedThematic);
        sceneSwitcher.setRootWithEvent(event, "/questionView.fxml");

    }

    @FXML
    void secondOptionSelected(ActionEvent event) throws IOException {
        String selectedThematic = txtSecondOption.getText();
        String pathToSelectedThematic = thematicDisplayed2;
        if (thematicDisplayed2.equals("Temática4")) {
            thematicState.setThematic4selected(true);
        }
        if (thematicDisplayed2.equals("Temática3")) {
            thematicState.setThematic3selected(true);
        }
        if (thematicDisplayed2.equals("Temática2")) {
            thematicState.setThematic2selected(true);
        }
        if (thematicDisplayed2.equals("Temática1")) {
            thematicState.setThematic1selected(true);
        }


        thematicState.actualThematic(pathToSelectedThematic);
        System.out.println("Temática seleccionada: " + selectedThematic);
        sceneSwitcher.setRootWithEvent(event, "/questionView.fxml");

    }

    @FXML
    private void upgradePurplePoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            points.setPurpleTeamPoints(points.getPurpleTeamPoints() + 1);
            txtPurpleTeam.setText("" + points.getPurpleTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getPurpleTeamPoints() != 0) {
            points.setPurpleTeamPoints(points.getPurpleTeamPoints() - 1);
            txtPurpleTeam.setText("" + points.getPurpleTeamPoints());
        }
    }

    @FXML
    private void upgradeGreenPoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            points.setGreenTeamPoints(points.getGreenTeamPoints() + 1);
            txtGreenTeam.setText("" + points.getGreenTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getGreenTeamPoints() != 0) {
            points.setGreenTeamPoints(points.getGreenTeamPoints() - 1);
            txtGreenTeam.setText("" + points.getGreenTeamPoints());
        }
    }

    @FXML
    public void upgradeRedPoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            points.setRedTeamPoints(points.getRedTeamPoints() + 1);
            txtRedTeam.setText("" + points.getRedTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getRedTeamPoints() != 0) {
            points.setRedTeamPoints(points.getRedTeamPoints() - 1);
            txtRedTeam.setText("" + points.getRedTeamPoints());
        }
    }

    @FXML
    private void upgradeBluePoints(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) { // Verifica si es el botón izquierdo
            points.setBlueTeamPoints(points.getBlueTeamPoints() + 1);
            txtBlueTeam.setText("" + points.getBlueTeamPoints());
        } else if (event.getButton() == MouseButton.SECONDARY && points.getBlueTeamPoints() != 0) { // Verifica si es el botón derecho
            points.setBlueTeamPoints(points.getBlueTeamPoints() - 1);
            txtBlueTeam.setText("" + points.getBlueTeamPoints());
        }
    }


    private void displayAllTheThematics() {
        List<String> availableThematics = new ArrayList<>();

        if (!thematicState.isThematic1selected()) {
            availableThematics.add("Temática1");
        }
        if (!thematicState.isThematic2selected()) {
            availableThematics.add("Temática2");
        }
        if (!thematicState.isThematic3selected()) {
            availableThematics.add("Temática3");
        }
        if (!thematicState.isThematic4selected()) {
            availableThematics.add("Temática4");
        }

        // Si no queda ninguna temática, saltar directamente al leaderboard
        if (thematicState.getX() == 4) {
            try {
                System.out.println("No quedan temáticas disponibles. Cambiando a la vista del leaderboard...");
                sceneSwitcher.setRoot(btnFirstOption, "/leaderboardView.fxml");
            } catch (IOException e) {
                showError("Error al cambiar a la vista del leaderboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (availableThematics.size() == 1) {
            String remainingThematic = availableThematics.get(0);

            if (remainingThematic.equals("Temática4")) {
                thematicState.setThematic4selected(true);
            }
            if (remainingThematic.equals("Temática3")) {
                thematicState.setThematic3selected(true);
            }
            if (remainingThematic.equals("Temática2")) {
                thematicState.setThematic2selected(true);
            }
            if (remainingThematic.equals("Temática1")) {
                thematicState.setThematic1selected(true);
            }
            thematicState.actualThematic(remainingThematic);
            thematicState.setX(thematicState.getX() + 1);

            try {
                System.out.println("Solo queda una temática disponible: " + remainingThematic);
                String contentText = "A partir del hecho de que no quede más ninguna temática he decidido seleccionar" +
                        " automáticamente la temática: " + readThematicName(remainingThematic) + ", espero que" +
                        " esté contento con mi decisión Jefe Maestro";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Selección automática de temática");
                alert.setContentText(contentText);
                alert.setTitle("Cortana");
                speakWithCortanaVoice(contentText);

                alert.showAndWait();
                stopCortanaVoice(); // <-- Detener la voz justo después de cerrar el Alert

                sceneSwitcher.setRoot(btnFirstOption, "/questionView.fxml"); // Cambiar directamente a las preguntas
            } catch (IOException e) {
                showError("Error al cambiar a la vista de preguntas: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (availableThematics.size() == 2) {
            thematicState.setX(thematicState.getX() + 1);

            String[] thematics = selectRandomThematics(availableThematics);
            String finalThematic1 = thematics[0];
            String finalThematic2 = thematics[1];
            thematicDisplayed1 = finalThematic1;
            thematicDisplayed2 = finalThematic2;

            String finalName1 = readThematicName(finalThematic1);
            String finalName2 = readThematicName(finalThematic2);

            Image finalImage1 = loadThematicImage(finalThematic1);
            Image finalImage2 = loadThematicImage(finalThematic2);

            txtFirstOption.setText(finalName1);
            txtSecondOption.setText(finalName2);
            imgFirstOption.setImage(finalImage1);
            imgSecondOption.setImage(finalImage2);

        } else {
            // Animación para seleccionar temáticas
            thematicState.setX(thematicState.getX() + 1);

            // Deshabilitar botones antes de la animación
            btnFirstOption.setDisable(true);
            btnSecondOption.setDisable(true);

            Timeline timeline = new Timeline();
            System.out.println("Inicializando selección de temáticas...");

            for (int i = 0; i < 25; i++) {
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 0.1), e -> {
                    try {
                        String[] thematics = selectRandomThematics(availableThematics);
                        String thematic1 = thematics[0];
                        String thematic2 = thematics[1];

                        String name1 = readThematicName(thematic1);
                        String name2 = readThematicName(thematic2);

                        Image image1 = loadThematicImage(thematic1);
                        Image image2 = loadThematicImage(thematic2);

                        txtFirstOption.setText(name1);
                        txtSecondOption.setText(name2);
                        imgFirstOption.setImage(image1);
                        imgSecondOption.setImage(image2);
                    } catch (Exception ex) {
                        showError("Error durante la selección de temáticas: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                timeline.getKeyFrames().add(keyFrame);
            }

            timeline.setOnFinished(e -> {
                try {
                    System.out.println("Finalizando selección de temáticas...");
                    String[] thematics = selectRandomThematics(availableThematics);
                    String finalThematic1 = thematics[0];
                    String finalThematic2 = thematics[1];
                    thematicDisplayed1 = finalThematic1;
                    thematicDisplayed2 = finalThematic2;

                    String finalName1 = readThematicName(finalThematic1);
                    String finalName2 = readThematicName(finalThematic2);

                    Image finalImage1 = loadThematicImage(finalThematic1);
                    Image finalImage2 = loadThematicImage(finalThematic2);

                    txtFirstOption.setText(finalName1);
                    txtSecondOption.setText(finalName2);
                    imgFirstOption.setImage(finalImage1);
                    imgSecondOption.setImage(finalImage2);

                    // Habilitar botones después de la animación
                    btnFirstOption.setDisable(false);
                    btnSecondOption.setDisable(false);

                } catch (Exception ex) {
                    showError("Error al finalizar la selección de temáticas: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            timeline.play();
            System.out.println("Timeline iniciado.");
        }
    }

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
        String filePath = clientService.getClientById(1).getRutaCarpetas() + "/" + thematic + "/NombreTemática/nombre.txt";
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
                showError("Error al leer el archivo: " + filePath);
                e.printStackTrace();
            }
        } else {
            showError("El archivo no existe: " + filePath);
        }
        return "Nombre desconocido";
    }

    private Image loadThematicImage(String thematic) {
        String imagePath = clientService.getClientById(1).getRutaCarpetas() + "/" + thematic + "/ImagenTemática/imagen.png";
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            return new Image(imageFile.toURI().toString());
        } else {
            System.err.println("La imagen no existe: " + imagePath + ". Usando imagen por defecto.");
        }
        return new Image(getClass().getResource("/images/default.png").toString());
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void speakWithCortanaVoice(String text) {
        try {
            String command = "PowerShell -Command \"Add-Type –AssemblyName System.Speech; " +
                    "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                    "$speak.Speak('" + text.replace("'", "''") + "');\"";
            cortanaVoiceProcess = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCortanaVoice() {
        if (cortanaVoiceProcess != null && cortanaVoiceProcess.isAlive()) {
            cortanaVoiceProcess.destroy();
            cortanaVoiceProcess = null;
        }
    }

    @Deprecated
    public void testSelectThematic(ActionEvent actionEvent) {
        String selectedThematic = txtFirstOption.getText();
        String pathToSelectedThematic = thematicDisplayed1;
        int x = 1;
        if (thematicDisplayed1.equals("Temática4")) {
            thematicState.setThematic4selected(true);
        }
        if (thematicDisplayed1.equals("Temática3")) {
            thematicState.setThematic3selected(true);
        }
        if (thematicDisplayed1.equals("Temática2")) {
            thematicState.setThematic2selected(true);
        }
        if (thematicDisplayed1.equals("Temática1")) {
            thematicState.setThematic1selected(true);
        }

        thematicState.actualThematic(pathToSelectedThematic);
        System.out.println("Temática seleccionada: " + selectedThematic);

        displayAllTheThematics();
    }
}
