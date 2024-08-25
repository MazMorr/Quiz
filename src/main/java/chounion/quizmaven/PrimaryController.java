package chounion.quizmaven;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;

public class PrimaryController {

    // Declaración de variables
    private int blueTeam;
    private int orangeTeam;
    private int redTeam;
    private int yellowTeam;

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private AnchorPane anchorPane;
    @FXML private Button btnChangeWallpaper;
    @FXML private Rectangle rectangleImageView;
    @FXML private Button btnDeploySecondaryWindow;
    @FXML private Button btnLessBlue, btnLessOrange, btnLessRed, btnLessYellow;
    @FXML private Button btnMoreBlue, btnMoreOrange, btnMoreRed, btnMoreYellow;
    @FXML private Button[] btnQuestions = new Button[30];
    @FXML private Button[] rdbButtons = new Button[30];
    @FXML private MediaView testMediaView;
    @FXML private StackPane testMediaContainer;
    @FXML private Label testLabel;
    @FXML private ImageView testImageView;
    @FXML private Button btnPauseVideo;
    @FXML private Button btnFullScreen;
    @FXML private Label labelYellow, labelOrange, labelRed, blueLabel;
    @FXML private Button btnResetPoints;
    @FXML private BarChart<String, Number> bcRanking;

    private FileWatcher questionWatcher;
    private SecondaryController secondaryController;
    private MediaPlayer currentMediaPlayer;
    private Stage stage;

    // Métodos de inicialización
    @FXML
    void initialize() {
       if (anchorPane != null) {
            initializeButtons();
        }
        initializeFileWatcher();
        initializeMediaComponents();
        for (Button btn : btnQuestions) {
            if (btn != null) {
                btn.setOnAction(this::handleQuestion);
            }
        }
        btnFullScreen.setText("Pantalla Completa");
    }

    private void initializeButtons() {
        for (int i = 0; i < 30; i++) {
            String btnQuestionId = "#btnQuestion" + (i + 1);
            String rdbId = "#rdb" + (i + 1);

            Button btnQuestion = (Button) anchorPane.lookup(btnQuestionId);
            Button btnDeploy = (Button) anchorPane.lookup(rdbId);

            if (btnQuestion != null) {
                btnQuestion.setOnAction(this::handleQuestion); 
                btnQuestions[i] = btnQuestion;
            }

            if (btnDeploy != null) {
                btnDeploy.setOnAction(this::handleDeploy);
                rdbButtons[i] = btnDeploy;
            }
        }
    }

    private void initializeFileWatcher() {
        try {
            Path basePath = Paths.get(getClass().getResource("/chounion/quizmaven/config").toURI());
            questionWatcher = new FileWatcher(basePath, this::reloadCurrentQuestion);
            questionWatcher.watch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeMediaComponents() {
        if (testMediaContainer == null) testMediaContainer = new StackPane();
        if (testMediaView == null) testMediaView = new MediaView();
        if (testImageView == null) testImageView = new ImageView();

        testImageView.fitWidthProperty().bind(testMediaContainer.widthProperty());
        testImageView.fitHeightProperty().bind(testMediaContainer.heightProperty());
        testMediaView.fitWidthProperty().bind(testMediaContainer.widthProperty());
        testMediaView.fitHeightProperty().bind(testMediaContainer.heightProperty());

        testMediaContainer.getChildren().clear();
        testMediaContainer.getChildren().addAll(testImageView, testMediaView);
    }

    // Métodos para manejar puntajes
    @FXML
    void lessBlue(ActionEvent event) {
        if (blueTeam > 0) blueTeam--;
        updateScores();
    }

    @FXML
    void lessOrange(ActionEvent event) {
        if (orangeTeam > 0) orangeTeam--;
        updateScores();
    }

    @FXML void lessRed(ActionEvent event) {
        if (redTeam > 0) redTeam--;
        updateScores();
    }

    @FXML void lessYellow(ActionEvent event) {
        if (yellowTeam > 0) yellowTeam--;
        updateScores();
    }

    @FXML void moreBlue(ActionEvent event) {
        if (blueTeam < 16) blueTeam++;
        updateScores();
    }

    @FXML void moreOrange(ActionEvent event) {
        if (orangeTeam < 16) orangeTeam++;
        updateScores();
    }

    @FXML void moreRed(ActionEvent event) {
        if (redTeam < 16) redTeam++;
        updateScores();
    }

    @FXML void moreYellow(ActionEvent event) {
        if (yellowTeam < 16) yellowTeam++;
        updateScores();
    }

    private void setLabelOrange(int value) {
        if (labelOrange != null) {
            labelOrange.setText(String.valueOf(value));
        }
    }

    private void setLabelRed(int value) {
        if (labelRed != null) {
            labelRed.setText(String.valueOf(value));
        }
    }

    private void setLabelYellow(int value) {
        if (labelYellow != null) {
            labelYellow.setText(String.valueOf(value));
        }
    }

    private void setBlueLabel(int value) {
        if (blueLabel != null) {
            blueLabel.setText(String.valueOf(value));
        }
    }

    private void setCurrentMediaPlayer(MediaPlayer mediaPlayer) {
        this.currentMediaPlayer = mediaPlayer;
    }

    // Método para actualizar puntajes
    private void updateScores() {
        // Actualizar las etiquetas en la ventana principal
        blueLabel.setText(String.valueOf(blueTeam));
        labelRed.setText(String.valueOf(redTeam));
        labelOrange.setText(String.valueOf(orangeTeam));
        labelYellow.setText(String.valueOf(yellowTeam));

        // Actualizar el gráfico en la ventana secundaria
        if (secondaryController != null) {
            secondaryController.updateTeamPoints(blueTeam, redTeam, orangeTeam, yellowTeam);
            secondaryController.updateBarChart(blueTeam, redTeam, orangeTeam, yellowTeam);
        }
    }

    @FXML
    void deploySecondaryWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
            Parent root = loader.load();
            secondaryController = loader.getController();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {
            });
            stage.show();

            secondaryController.setPrimaryController(this);
            secondaryController.setupResponsiveLayout();
            secondaryController.setupKeyHandler();
            stage.setAlwaysOnTop(true);
            
            // Mover la ventana secundaria al segundo monitor si está disponible
            secondaryController.moveToSecondaryScreen();
            
            // Actualizar el gráfico de barras inicial
            secondaryController.updateBarChart(blueTeam, redTeam, orangeTeam, yellowTeam);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void closeMedia(ActionEvent event){
        if (secondaryController != null) {
            secondaryController.closeCurrentMedia();
        }
    }

    @FXML
    void handleDeploy(ActionEvent event) {
        if (secondaryController == null) {
            deploySecondaryWindow(null);
        }

        Object source = event.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            // Verificar si el ID comienza con "rdb"
            if (button.getId().startsWith("rdb")) { 
                int deployNumber = Integer.parseInt(button.getId().replace("rdb", ""));
                deployAnswer(deployNumber);
            }
        }   
    }
    
    @FXML
    void handleQuestion(ActionEvent event) {
        if (secondaryController == null) {
            deploySecondaryWindow(null);
        }

        Object source = event.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            // Verificar si el ID comienza con "btnQuestion"
            if (button.getId().startsWith("btnQuestion")) { 
                int deployNumber = Integer.parseInt(button.getId().replace("btnQuestion", ""));
                deployQuestion(deployNumber);
            }
        }   
    }
    
    private void deployAnswer(int deployNumber){
        Platform.runLater(() -> {
            System.out.println("Desplegando respuesta: " + deployNumber);
            secondaryController.resetAllQuestionLabelsExcept(deployNumber);
            secondaryController.printQuestionStates();
            secondaryController.setQuestionLabelOpacity(deployNumber, 0.0);
            
            String textFilePath = "/chounion/quizmaven/config/Question" + deployNumber + "/Respuesta" + deployNumber + ".txt";
            String fileContent = FileReader.readTextFile(textFilePath);
            secondaryController.setAnswerLabel(fileContent);
        });
    }
    
    private void deployQuestion(int deployNumber) {
        Platform.runLater(() -> {
            System.out.println("Desplegando pregunta: " + deployNumber);
            secondaryController.resetAllQuestionLabelsExcept(deployNumber);
            secondaryController.printQuestionStates();
            secondaryController.setQuestionLabelOpacity(deployNumber, 0.0);

            String textFilePath = "/chounion/quizmaven/config/Question" + deployNumber + "/Pregunta" + deployNumber + ".txt";
            String mediaPath = "/chounion/quizmaven/config/Question" + deployNumber + "/media";
            String fileContent = FileReader.readTextFile(textFilePath);
            secondaryController.setDeployLabel(fileContent);
            secondaryController.loadMedia(mediaPath);
        });
    } 
    

    @FXML
    void setFullScreen(ActionEvent event) {
        if (secondaryController != null) {
            boolean isFullScreen = secondaryController.toggleFullScreen();
            updateFullScreenButtonText(isFullScreen);
        } else {
            // Opcional: Mostrar un mensaje si la ventana secundaria no está abierta
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("La ventana secundaria no está abierta.");
            alert.showAndWait();
        }
    }

    public void updateFullScreenButtonText(boolean isFullScreen) {
        btnFullScreen.setText(isFullScreen ? "Salir d FullScreen" : "Pantalla Completa");
    }

    @FXML
    void pauseVideo(ActionEvent event) {
        if (secondaryController != null) {
            secondaryController.togglePlayPause();
        }
    }

    // Otros métodos
    @FXML
    void changeWallpaper(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de imagen", "*.jpg", "*.png", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
            Image image = new Image(selectedFile.toURI().toString());
            if (secondaryController != null) {
                secondaryController.setWallpaper(image);
            }
        }
    }

    @FXML
    void resetPoints(ActionEvent event) {
        blueTeam = 0;
        redTeam = 0;
        orangeTeam = 0;
        yellowTeam = 0;
        updateScores();
    }

    private void loadQuestion(String textFilePath, String mediaPath) {
        String fileContent = FileReader.readTextFile(textFilePath);
        testLabel.setText(fileContent);
        MediaLoader.loadMedia(mediaPath, testImageView, testMediaView, testMediaContainer, this::setCurrentMediaPlayer);
    }

    private void reloadCurrentQuestion() {
        for (Button btn : btnQuestions) {
            if (btn != null && btn.isPressed()) {
                handleDeploy(new ActionEvent(btn, null));
                break;
            }
        }
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setResizable(true);

        // Mover la lógica de binding aquí
        if (bcRanking != null) {
            bcRanking.prefWidthProperty().bind(anchorPane.widthProperty());
            bcRanking.prefHeightProperty().bind(anchorPane.heightProperty().multiply(0.4));
        }

        if (testMediaContainer != null) {
            testMediaContainer.prefWidthProperty().bind(anchorPane.widthProperty().multiply(0.8));
            testMediaContainer.prefHeightProperty().bind(anchorPane.heightProperty().multiply(0.4));
        }

        List<Button> allButtons = Arrays.asList(btnChangeWallpaper, btnDeploySecondaryWindow, btnFullScreen, 
                                                btnLessBlue, btnMoreBlue, btnLessRed, btnMoreRed, 
                                                btnLessOrange, btnMoreOrange, btnLessYellow, btnMoreYellow, 
                                                btnPauseVideo, btnResetPoints);

        for (Button btn : allButtons) {
            if (btn != null) {
                btn.prefWidthProperty().bind(anchorPane.widthProperty().multiply(0.1));
                btn.prefHeightProperty().bind(anchorPane.heightProperty().multiply(0.05));
            }
        }
    }
    
    public boolean isPrimaryStageActive() {
        if (stage != null) {
            return stage.isFocused() || stage.getScene().getRoot().isFocused();
        }
        return false;
    }
}
