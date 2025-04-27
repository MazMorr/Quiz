package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.model.ThematicState;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import com.marcosoft.quiz.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private ImageView imgFirstOption;
    @FXML
    private ImageView imgSecondOption;

    @FXML
    void firstOptionSelected(ActionEvent event) throws IOException {
        String selectedThematic = txtFirstOption.getText();
        thematicState.selectThematic(selectedThematic);

        if (thematicState.allThematicsSelected()) {
            String lastThematic = thematicState.getLastRemainingThematic();
            txtSecondOption.setText(lastThematic);
            imgSecondOption.setImage(loadThematicImage(lastThematic));
        } else {
            System.out.println("Temática seleccionada: " + selectedThematic);
            sceneSwitcher.setRoot(event, "/questionView.fxml");
        }
    }

    @FXML
    void secondOptionSelected(ActionEvent event) throws IOException {
        String selectedThematic = txtSecondOption.getText();
        thematicState.selectThematic(selectedThematic);

        if (thematicState.allThematicsSelected()) {
            String lastThematic = thematicState.getLastRemainingThematic();
            txtFirstOption.setText(lastThematic);
            imgFirstOption.setImage(loadThematicImage(lastThematic));
        } else {
            System.out.println("Temática seleccionada: " + selectedThematic);
            sceneSwitcher.setRoot(event, "/questionView.fxml");
        }
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

    @FXML
    private void initialize() {
        displayAllTheThematics();
    }

    private void displayAllTheThematics() {
        List<String> thematicPaths = List.of("Temática1", "Temática2", "Temática3", "Temática4");
        Timeline timeline = new Timeline();

        System.out.println("Inicializando selección de temáticas...");

        for (int i = 0; i < 25; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 0.1), e -> {
                try {
                    String[] thematics = selectRandomThematics(thematicPaths);
                    String thematic1 = thematics[0];
                    String thematic2 = thematics[1];

                    System.out.println("Seleccionadas temáticas aleatorias: " + thematic1 + " y " + thematic2);

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
                String[] thematics = selectRandomThematics(thematicPaths);
                String finalThematic1 = thematics[0];
                String finalThematic2 = thematics[1];

                String finalName1 = readThematicName(finalThematic1);
                String finalName2 = readThematicName(finalThematic2);

                Image finalImage1 = loadThematicImage(finalThematic1);
                Image finalImage2 = loadThematicImage(finalThematic2);

                txtFirstOption.setText(finalName1);
                txtSecondOption.setText(finalName2);
                imgFirstOption.setImage(finalImage1);
                imgSecondOption.setImage(finalImage2);
            } catch (Exception ex) {
                showError("Error al finalizar la selección de temáticas: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        timeline.play();
        System.out.println("Timeline iniciado.");
    }

    private String[] selectRandomThematics(List<String> thematicPaths) {
        Random random = new Random();
        String thematic1, thematic2;
        do {
            thematic1 = thematicPaths.get(random.nextInt(thematicPaths.size()));
            thematic2 = thematicPaths.get(random.nextInt(thematicPaths.size()));
        } while (thematic1.equals(thematic2) || thematicState.isThematicSelected(thematic1) || thematicState.isThematicSelected(thematic2));
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

    private List<String> loadQuestionsForThematic(String thematic) {
        String questionsPath = clientService.getClientById(1).getRutaCarpetas() + "/" + thematic;
        List<String> questions = new ArrayList<>();

        try {
            for (int i = 1; i <= 6; i++) { // Suponiendo que hay 6 preguntas por temática
                File questionFile = new File(questionsPath + "/Pregunta" + i + "/Pregunta.txt");
                if (questionFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(questionFile))) {
                        String question = reader.readLine();
                        if (question != null && !question.isBlank()) {
                            questions.add(question);
                        } else {
                            System.out.println("El archivo de la pregunta está vacío: " + questionFile.getAbsolutePath());
                        }
                    }
                } else {
                    System.out.println("El archivo de la pregunta no existe: " + questionFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            showError("Error al cargar las preguntas para la temática: " + thematic);
            e.printStackTrace();
        }

        return questions;
    }
}
