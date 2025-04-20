package com.marcosoft.quiz.controller;

import com.marcosoft.quiz.model.Points;
import com.marcosoft.quiz.model.ThematicState;
import com.marcosoft.quiz.utils.DirectoriesCreator;
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
    @FXML
    private Label txtFirstOption, txtSecondOption, txtGreenTeam, txtPurpleTeam, txtRedTeam, txtBlueTeam;
    @FXML
    private ImageView imgFirstOption;
    @FXML
    private ImageView imgSecondOption;

    @FXML
    void firstOptionSelected(ActionEvent event) {
        String selectedThematic = txtFirstOption.getText();
        thematicState.selectThematic(selectedThematic);

        if (thematicState.allThematicsSelected()) {
            String lastThematic = thematicState.getLastRemainingThematic();
            txtSecondOption.setText(lastThematic);
            imgSecondOption.setImage(loadThematicImage(lastThematic));
        } else {
            System.out.println("Temática seleccionada: " + selectedThematic);
        }
    }

    @FXML
    void secondOptionSelected(ActionEvent event) {
        String selectedThematic = txtSecondOption.getText();
        thematicState.selectThematic(selectedThematic);

        if (thematicState.allThematicsSelected()) {
            String lastThematic = thematicState.getLastRemainingThematic();
            txtFirstOption.setText(lastThematic);
            imgFirstOption.setImage(loadThematicImage(lastThematic));
        } else {
            System.out.println("Temática seleccionada: " + selectedThematic);
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
    void initialize(ActionEvent event) {
        Random random = new Random();
        List<String> thematicPaths = List.of("Temática1", "Temática2", "Temática3", "Temática4");
        Timeline timeline = new Timeline();
    
        System.out.println("Inicializando selección de temáticas...");
    
        for (int i = 0; i < 25; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 0.1), e -> {
                try {
                    // Seleccionar temáticas aleatorias que no hayan sido seleccionadas
                    String thematic1, thematic2;
                    do {
                        thematic1 = thematicPaths.get(random.nextInt(thematicPaths.size()));
                        thematic2 = thematicPaths.get(random.nextInt(thematicPaths.size()));
                    } while (thematic1.equals(thematic2) || thematicState.isThematicSelected(thematic1) || thematicState.isThematicSelected(thematic2));
    
                    System.out.println("Seleccionadas temáticas aleatorias: " + thematic1 + " y " + thematic2);
    
                    // Leer nombres de las temáticas
                    String name1 = readThematicName(thematic1);
                    String name2 = readThematicName(thematic2);
    
                    System.out.println("Nombres cargados: " + name1 + " y " + name2);
    
                    // Cargar imágenes de las temáticas
                    Image image1 = loadThematicImage(thematic1);
                    Image image2 = loadThematicImage(thematic2);
    
                    System.out.println("Imágenes cargadas para: " + thematic1 + " y " + thematic2);
    
                    // Actualizar los textos y las imágenes
                    txtFirstOption.setText(name1);
                    txtSecondOption.setText(name2);
                    imgFirstOption.setImage(image1);
                    imgSecondOption.setImage(image2);
                } catch (Exception ex) {
                    System.err.println("Error durante la selección de temáticas: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }
    
        timeline.setOnFinished(e -> {
            try {
                System.out.println("Finalizando selección de temáticas...");
                // Seleccionar temáticas finales que no se repitan
                String finalThematic1, finalThematic2;
                do {
                    finalThematic1 = thematicPaths.get(random.nextInt(thematicPaths.size()));
                    finalThematic2 = thematicPaths.get(random.nextInt(thematicPaths.size()));
                } while (finalThematic1.equals(finalThematic2) || thematicState.isThematicSelected(finalThematic1) || thematicState.isThematicSelected(finalThematic2));
    
                System.out.println("Temáticas finales seleccionadas: " + finalThematic1 + " y " + finalThematic2);
    
                // Leer nombres finales
                String finalName1 = readThematicName(finalThematic1);
                String finalName2 = readThematicName(finalThematic2);
    
                System.out.println("Nombres finales cargados: " + finalName1 + " y " + finalName2);
    
                // Cargar imágenes finales
                Image finalImage1 = loadThematicImage(finalThematic1);
                Image finalImage2 = loadThematicImage(finalThematic2);
    
                System.out.println("Imágenes finales cargadas para: " + finalThematic1 + " y " + finalThematic2);
    
                // Actualizar los textos y las imágenes finales
                txtFirstOption.setText(finalName1);
                txtSecondOption.setText(finalName2);
                imgFirstOption.setImage(finalImage1);
                imgSecondOption.setImage(finalImage2);
            } catch (Exception ex) {
                System.err.println("Error al finalizar la selección de temáticas: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    
        timeline.play();
        System.out.println("Timeline iniciado.");
    }

    private String readThematicName(String thematic) {
        String filePath = DirectoriesCreator.getBasePath() + "/" + thematic + "/NombreTemática/nombre.txt";
        File file = new File(filePath);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return reader.readLine(); // Leer la primera línea del archivo
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + filePath);
                e.printStackTrace();
            }
        } else {
            System.err.println("El archivo no existe: " + filePath);
        }
        return "Nombre desconocido";
    }

    private Image loadThematicImage(String thematic) {
        String imagePath = DirectoriesCreator.getBasePath() + "/" + thematic + "/ImagenTemática/imagen.png";
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            return new Image(imageFile.toURI().toString());
        } else {
            System.err.println("La imagen no existe: " + imagePath);
        }
        return new Image(getClass().getResource("/images/default.png").toString()); // Imagen por defecto
    }
}
