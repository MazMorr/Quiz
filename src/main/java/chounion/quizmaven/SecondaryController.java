package chounion.quizmaven;

import java.io.File;
import java.net.URISyntaxException;
import javafx.scene.text.Font;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Window;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The SecondaryController class handles the secondary screen of the quiz application.
 * It manages the media display, team scores, and the layout of the user interface components.
 * This class is responsible for initializing the UI elements, loading media, 
 * and responding to user interactions.
 */
public class SecondaryController {

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private StackPane rootStackPane;
    @FXML private Label deployLabel;
    @FXML private ImageView imgWallpaper;
    @FXML private StackPane mediaContainer;
    @FXML private MediaView deployMediaView;
    @FXML private ImageView deployImageView;
    @FXML private BarChart<String, Number> bcRanking;
    @FXML private Label[] disponibleQuestions = new Label[30];
    @FXML private Label blueTeamdeployPoints, redTeamDeployPoints, orangeTeamDeployPoints, yellowTeamDeployPoints; 
    @FXML private Label answerLabel;
    
    private MediaPlayer currentMediaPlayer;
    private PrimaryController primaryController;
    private boolean[] questionSelected = new boolean[30];
    private ChangeListener<Boolean> focusListener;
    
     /**
     * Initializes the SecondaryController by setting up the bar chart,
     * initializing question labels, and configuring the layout.
     */
    @FXML
    void initialize() {
        setupBarChart();
        
        Platform.runLater(() -> {
            initializeQuestionLabels();
            setupResponsiveLayout();
            initializeTeamPointLabels();
            System.out.println("Inicialización completada");
        });
    }
    
    /**
     * Initializes the team point labels for the blue, red, orange, and yellow teams.
     */
    private void initializeTeamPointLabels() {
        blueTeamdeployPoints = (Label) lookup("#blueTeamDeployPoints");
        redTeamDeployPoints = (Label) lookup("#redTeamDeployPoints");
        orangeTeamDeployPoints = (Label) lookup("#orangeTeamDeployPoints");
        yellowTeamDeployPoints = (Label) lookup("#yellowTeamDeployPoints");
    }
    
    /**
     * Initializes the labels for the available questions.
     */
    public void initializeQuestionLabels() {
        System.out.println("Iniciando labels de preguntas");
        for (int i = 1; i <= 30; i++) {
            String labelId = "#disponibleQuestion" + i;
            System.out.println("Buscando label con id: " + labelId);
            disponibleQuestions[i-1] = (Label) lookup("#disponibleQuestion" + i);
            if (disponibleQuestions[i-1] == null) {
                System.out.println("No se encontró el label: disponibleQuestion" + i);
            } else {
                System.out.println("Label encontrado y configurado: " + labelId);
                disponibleQuestions[i-1].setOpacity(1.0);
                disponibleQuestions[i-1].setVisible(true);
            }
        }
        System.out.println("Finalizada la inicialización de labels de preguntas");
    }
    
    /**
     * Sets the reference to the PrimaryController.
     */
    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }

    /**
     * Sets up the bar chart for team rankings.
     */
    private void setupBarChart() {
        bcRanking.getData().clear();
        bcRanking.setLegendVisible(false);
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>("Azul", 0));
        series.getData().add(new XYChart.Data<>("Rojo", 0));
        series.getData().add(new XYChart.Data<>("Naranja", 0));
        series.getData().add(new XYChart.Data<>("Amarillo", 0));

        bcRanking.getData().add(series);
        customizeBarColors();
    }

    /**
     * Sets the text of the deploy label.
     * 
     * @param text The text to be displayed on the deploy label.
     */
    public void setDeployLabel(String text) {
        this.deployLabel.setText(text);
    }
    
    /**
     * Sets the text of the answer label.
     * 
     * @param text The text to be displayed on the answer label.
     */
    public void setAnswerLabel(String text){
        this.answerLabel.setText(text);
    }

    /**
     * Sets the wallpaper image for the background.
     * 
     * @param image The image to be set as the wallpaper.
     */
    public void setWallpaper(Image image) {
        this.imgWallpaper.setImage(image);
        updateLayout();
    }

    /**
     * Loads media (image or video) based on the question number.
     * 
     * @param relativePath The relative path to the media file.
     * @param questionNumber The question number to load media for.
     */
    public void loadMedia(String relativePath, int questionNumber) {
        Platform.runLater(() -> {
            closeCurrentMedia();
            String fullRelativePath = "/chounion/quizmaven/config/Question" + questionNumber + relativePath; // Incluir el número de pregunta
            MediaLoader.loadMedia(fullRelativePath, deployImageView, deployMediaView, mediaContainer, this::setCurrentMediaPlayer);
        });
    }
    
    /**
     * Moves the application window to the secondary screen if available.
     */
    public void moveToSecondaryScreen() {
        Platform.runLater(() -> {
            Stage stage = (Stage) mediaContainer.getScene().getWindow();
            Screen secondaryScreen = Screen.getScreens().size() > 1 ? Screen.getScreens().get(1) : Screen.getPrimary();
            Rectangle2D bounds = secondaryScreen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        });
    } 
    
    /**
     * Sets the current media player instance.
     * 
     * @param mediaPlayer The MediaPlayer instance to be set.
     */
    private void setCurrentMediaPlayer(MediaPlayer mediaPlayer) {
        this.currentMediaPlayer = mediaPlayer;
        if (this.currentMediaPlayer != null) {
            deployMediaView.setMediaPlayer(this.currentMediaPlayer);
        }
    }

    /**
     * Gets the current media player instance.
     * 
     * @return The current MediaPlayer instance.
     */
    public MediaPlayer getCurrentMediaPlayer() {
        return currentMediaPlayer;
    }

    /**
     * Toggles the playback of the current media between play and pause.
     */
    public void toggleVideoPlayback() {
        if (currentMediaPlayer != null) {
            if (currentMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                currentMediaPlayer.pause();
            } else {
                currentMediaPlayer.play();
            }
        }
    }

    /**
     * Toggles the full screen mode of the application window.
     * 
     * @return True if the application is now in full screen mode, false otherwise.
     */
    public boolean toggleFullScreen() {
        Stage stage = (Stage) mediaContainer.getScene().getWindow();
        boolean willBeFullScreen = !stage.isFullScreen();
        stage.setFullScreen(willBeFullScreen);

        if (willBeFullScreen) {
            // Mantener la ventana visible inicialmente
            stage.setAlwaysOnTop(true);
            stage.toFront();

            // Agregar un listener que solo actúe cuando la ventana pierde el foco
            focusListener = (obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    Platform.runLater(() -> {
                        // Verificar si la ventana principal tiene el foco
                        if (primaryController != null && !primaryController.isPrimaryStageActive()) {
                            stage.setAlwaysOnTop(true);
                            stage.toFront();
                            stage.requestFocus();
                        }
                    });
                }
            };
            stage.focusedProperty().addListener(focusListener);
        } else {
            stage.setAlwaysOnTop(false);
            if (focusListener != null) {
                stage.focusedProperty().removeListener(focusListener);
                focusListener = null;
            }
        }
        adjustLayoutForFullScreen(willBeFullScreen);
        return willBeFullScreen;
    }

    /**
     * Sets up the key handler for the media container.
     */
    public void setupKeyHandler() {
        if (mediaContainer.getScene() != null) {
            mediaContainer.getScene().setOnKeyPressed(this::handleKeyPress);
        }
    }

    /**
     * Handles key press events for the application.
     * 
     * @param event The key event to handle.
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            Stage stage = (Stage) mediaContainer.getScene().getWindow();
            if (stage.isFullScreen()) {
                stage.setFullScreen(false);
                stage.setAlwaysOnTop(false);
                if (primaryController != null) {
                    primaryController.updateFullScreenButtonText(false);
                }
            }
        }
    }

    /**
     * Toggles play and pause for the current media player.
     */
    public void togglePlayPause() {
        if (currentMediaPlayer != null) {
            if (currentMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                currentMediaPlayer.pause();
            } else {
                currentMediaPlayer.play();
            }
        }
    }

    /**
     * Customizes the colors of the bars in the ranking chart.
     */
    private void customizeBarColors() {
        String[] colors = {"#0000FF", "#FF0000", "#ffaa00", "#FFFF00"};
        int colorIndex = 0;

        for (XYChart.Series<String, Number> series : bcRanking.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                final int index = colorIndex;
                data.nodeProperty().addListener((ov, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-bar-fill: " + colors[index % colors.length] + ";");
                    }
                });
                colorIndex++;
            }
        }
    }

    /**
     * Updates the bar chart with the scores of the teams.
     * 
     * @param blueScore The score of the blue team.
     * @param redScore The score of the red team.
     * @param orangeScore The score of the orange team.
     * @param yellowScore The score of the yellow team.
     */
    public void updateBarChart(int blueScore, int redScore, int orangeScore, int yellowScore) {
        bcRanking.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] colors = {"#0000FF", "#FF0000", "#ffaa00", "#FFFF00"};
        String[] categories = {"Azul", "Rojo", "Naranja", "Amarillo"};
        int[] scores = {blueScore, redScore, orangeScore, yellowScore};

        for (int i = 0; i < 4; i++) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(categories[i], scores[i]);
            series.getData().add(data);

            final int index = i;
            data.nodeProperty().addListener((ov, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + colors[index] + ";");
                }
            });
        }
        bcRanking.getData().add(series);
    }

    /**
     * Closes the current media player and clears the media views.
     */
    public void closeCurrentMedia() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.dispose();
        }
        deployMediaView.setMediaPlayer(null);
        deployImageView.setImage(null);
    }

    /**
     * Cleans up the media views.
     */
    private void cleanupMediaViews() {
        if (deployMediaView != null) {
            deployMediaView.setMediaPlayer(null);
        }
        if (deployImageView != null) {
            deployImageView.setImage(null);
        }
        System.out.println("Vistas de media limpiadas");
    }

    /**
     * Sets the opacity of the specified question label.
     * 
     * @param questionNumber The question number (1-30).
     * @param opacity The opacity value to set (0.0 to 1.0).
     */
    public void setQuestionLabelOpacity(int questionNumber, double opacity) {
        
        if (questionNumber > 0 && questionNumber <= disponibleQuestions.length) {
            Label label = disponibleQuestions[questionNumber - 1];
            if (label != null) {
                label.setOpacity(opacity);
                label.setVisible(opacity > 0);
                questionSelected[questionNumber - 1] = (opacity == 0);
            } 
        }
        printQuestionStates(); // Imprimir estados después de cambiar la opacidad
    }

    /**
     * Looks up a node by its ID in the media container's scene.
     * 
     * @param id The ID of the node to look up.
     * @return The node with the specified ID, or null if not found.
     */
    private Node lookup(String id) {
        try {
            if (mediaContainer != null && mediaContainer.getScene() != null) {
                Node node = mediaContainer.getScene().lookup(id);
                if (node == null) {
                    System.out.println("No se encontró el nodo con id: " + id);
                }
                return node;
            } else {
                System.out.println("mediaContainer o su escena es null al buscar: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar el nodo " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Resets the specified question label to full opacity and visibility.
     * 
     * @param questionNumber The question number (1-30).
     */
    public void resetQuestionLabel(int questionNumber) {
        if (questionNumber > 0 && questionNumber <= disponibleQuestions.length) {
            Label label = disponibleQuestions[questionNumber - 1];
            if (label != null) {
                Platform.runLater(() -> {
                    label.setOpacity(1.0);
                    label.setVisible(true);
                    questionSelected[questionNumber - 1] = false;
                    System.out.println("Pregunta " + questionNumber + " reiniciada");
                });
            }
        }
    }
    
    /**
     * Checks if the scene is ready for interaction.
     * 
     * @return True if the media container and its scene are not null, false otherwise.
     */
    private boolean isSceneReady() {
        return mediaContainer != null && mediaContainer.getScene() != null;
    }
    
    /**
     * Cleans up resources used by the media player.
     */
    public void cleanup() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.dispose();
        }
    }
    
    /**
     * Adjusts the layout for full screen mode.
     * 
     * @param isFullScreen True if the application is in full screen mode, false otherwise.
     */
    private void adjustLayoutForFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        
            bcRanking.setPrefSize(screenWidth * 0.3, screenHeight * 0.4);
        }
    }

    /**
     * Sets up a responsive layout for the UI components.
     */
    public void setupResponsiveLayout() {
        System.out.println("Configurando layout responsivo");
        if (rootStackPane.getScene() == null) {
            rootStackPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    System.out.println("Nueva escena detectada");
                    setupStageListeners(newScene.getWindow());
                }
            });
        } else {
            setupStageListeners(rootStackPane.getScene().getWindow());
        }
    }
    
    /**
     * Sets up listeners for stage size changes.
     * 
     * @param window The window to set up listeners for.
     */
    private void setupStageListeners(Window window) {
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setMinWidth(1280);
            stage.setMinHeight(720);
            stage.widthProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println("Ancho cambiado: " + oldVal + " -> " + newVal);
                Platform.runLater(this::updateLayout);
            });
            stage.heightProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println("Alto cambiado: " + oldVal + " -> " + newVal);
                Platform.runLater(this::updateLayout);
            });
            Platform.runLater(this::updateLayout);
        }
    }

    /**
     * Updates the layout of UI components based on the current size of the root stack pane.
     */
    private void updateLayout() {
        double width = rootStackPane.getWidth();
        double height = rootStackPane.getHeight();
    
        System.out.println("Actualizando layout - Ancho: " + width + ", Alto: " + height);

        // Ajustar el tamaño del imgWallpaper
        imgWallpaper.setFitWidth(width);
        imgWallpaper.setFitHeight(height);
        imgWallpaper.setPreserveRatio(false);

        System.out.println("imgWallpaper - Ancho: " + imgWallpaper.getFitWidth() + ", Alto: " + imgWallpaper.getFitHeight());

        // Asegurarse de que el imgWallpaper esté en el fondo
        imgWallpaper.toBack();

        // Actualizar el tamaño del mediaContainer si es necesario
        if (mediaContainer != null) {
            mediaContainer.setPrefSize(width, height);
            System.out.println("mediaContainer - Ancho: " + mediaContainer.getPrefWidth() + ", Alto: " + mediaContainer.getPrefHeight());
        }
    }

    /**
     * Resets all question labels except for the specified question number.
     * 
     * @param exceptQuestionNumber The question number to exclude from resetting.
     */
    public void resetAllQuestionLabelsExcept(int exceptQuestionNumber) {
        for (int i = 0; i < disponibleQuestions.length; i++) {
            if (i + 1 != exceptQuestionNumber && !questionSelected[i]) {
                final int index = i;
                Label label = disponibleQuestions[i];
                if (label != null) {
                    Platform.runLater(() -> {
                        label.setOpacity(1.0);
                        label.setVisible(true);
                    });
                }
            }
        }
        System.out.println("Todas las preguntas no seleccionadas han sido reiniciadas excepto la pregunta " + exceptQuestionNumber);
    }


    public void printQuestionStates() {
        for (int i = 0; i < disponibleQuestions.length; i++) {
            Label label = disponibleQuestions[i];
            if (label != null) {
                System.out.println("Pregunta " + (i+1) + ": Opacidad = " + label.getOpacity() + 
                                ", Visible = " + label.isVisible() + 
                                ", Seleccionada = " + questionSelected[i]);
            } else {
                System.out.println("Pregunta " + (i+1) + ": Label es null");
            }
        }
    }
    
    public void updateTeamPoints(int bluePoints, int redPoints, int orangePoints, int yellowPoints) {
        Platform.runLater(() -> {
            if (blueTeamdeployPoints != null) blueTeamdeployPoints.setText(String.valueOf(bluePoints));
            if (redTeamDeployPoints != null) redTeamDeployPoints.setText(String.valueOf(redPoints));
            if (orangeTeamDeployPoints != null) orangeTeamDeployPoints.setText(String.valueOf(orangePoints));
            if (yellowTeamDeployPoints != null) yellowTeamDeployPoints.setText(String.valueOf(yellowPoints));
        });
    }
}
