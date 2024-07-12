package mycompany.mavenproject1;

import java.io.File;
import java.net.URL; 
import java.util.Random; 
import java.util.ResourceBundle; 
import javafx.event.ActionEvent; 
import javafx.fxml.FXML; 
import javafx.scene.control.Alert; 
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; 
import javafx.animation.KeyFrame; 
import javafx.animation.Timeline; 
import javafx.util.Duration; 
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Node;

public class PrimaryController { 
    @FXML 
    public ResourceBundle resources; 
    @FXML 
    public URL location; 
    @FXML 
    public Button btnAleatorio; 
    @FXML
    public Button btnChangeWallpapaper;
    @FXML 
    public Label txtRifa; 
    @FXML 
    public TextField txtTickets; 
    
    @FXML
    public ImageView imgWallpaper;
    @FXML
    void ChangeWallpaper(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivos de imagen", "*.jpg", "*.png", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog((Stage) ((Node) event.getSource()).getScene().getWindow());
    
        if (selectedFile != null) {
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
        
            Image image = new Image(selectedFile.toURI().toString());
            imgWallpaper.setImage(image);
        }
    }
    @FXML 
    void Suerte(ActionEvent event) { 
        Random random= new Random(); 
        try{ 
            int Op1 = Integer.parseInt(this.txtTickets.getText());
            int randomNumber = random.nextInt(Op1 + 1);
            Timeline timeline = new Timeline();
            for (int i = 0; i < 15; i++) {
                int finalNumber = random.nextInt(Op1 + 1);
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * 0.01), e -> {
                    String formattedNumber = String.format("%04d", random.nextInt(Op1 + 1));
                    this.txtRifa.setText(formattedNumber);
                });
                timeline.getKeyFrames().add(keyFrame);
            }
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.0001), e -> {
                String formattedNumber = String.format("%04d", randomNumber);
                this.txtRifa.setText(formattedNumber);
            }));
            timeline.play();
        } catch(NumberFormatException e){ 
            Alert alert= new Alert(Alert.AlertType.ERROR); 
            alert.setHeaderText(null); 
            alert.setTitle("ERROR"); 
            alert.setContentText("Formato Incorrecto"); 
            alert.showAndWait(); 
        } 
    } 
    @FXML 
    void initialize() { 
    } 
}