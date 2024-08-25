package chounion.quizmaven;

import java.net.URL;
import java.util.function.Consumer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

public class MediaLoader {
    public static void loadMedia(String relativePath, ImageView imageView, MediaView mediaView, StackPane mediaContainer, Consumer<MediaPlayer> mediaPlayerConsumer) {
        // Detener y limpiar cualquier reproducción anterior
        if (mediaView.getMediaPlayer() != null) {
            mediaView.getMediaPlayer().stop();
            mediaView.setMediaPlayer(null);
        }
        
        mediaContainer.getChildren().clear();
        imageView.setImage(null);
        mediaView.setMediaPlayer(null);

        boolean imageFound = false;
        boolean videoFound = false;

        // Verificar si existe un archivo de imagen
        String[] imageExtensions = {".png", ".jpg", ".gif"};
        for (String ext : imageExtensions) {
            URL imageUrl = MediaLoader.class.getResource(relativePath + ext);
            if (imageUrl != null) {
                imageFound = true;
                break;
            }
        }

        // Verificar si existe un archivo de video
        URL videoUrl = MediaLoader.class.getResource(relativePath + ".mp4");
        if (videoUrl != null) {
            videoFound = true;
        }

        if (imageFound && videoFound) {
            showAlert("Se encontraron múltiples archivos multimedia en la ruta especificada.");
            return;
        }

        if (imageFound) {
            loadImage(relativePath, imageView, mediaContainer);
        } else if (videoFound) {
            loadVideo(relativePath, mediaView, mediaContainer, mediaPlayerConsumer);
        }
    }

    private static void loadImage(String relativePath, ImageView imageView, StackPane mediaContainer) {
        String[] imageExtensions = {".png", ".jpg", ".gif"};
        for (String ext : imageExtensions) {
            URL imageUrl = MediaLoader.class.getResource(relativePath + ext);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm());
                imageView.setImage(image);
                
                // Ajustar la imagen al contenedor
                imageView.setPreserveRatio(true);
                imageView.fitWidthProperty().bind(mediaContainer.widthProperty());
                imageView.fitHeightProperty().bind(mediaContainer.heightProperty());
                
                mediaContainer.getChildren().add(imageView);
                return;
            }
        }
    }

    private static void loadVideo(String relativePath, MediaView mediaView, StackPane mediaContainer, Consumer<MediaPlayer> mediaPlayerConsumer) {
        URL videoUrl = MediaLoader.class.getResource(relativePath + ".mp4");
        if (videoUrl != null) {
            Media media = new Media(videoUrl.toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            
            // Ajustar el video al contenedor
            mediaView.fitWidthProperty().bind(mediaContainer.widthProperty());
            mediaView.fitHeightProperty().bind(mediaContainer.heightProperty());
            mediaView.setPreserveRatio(true);
            
            mediaContainer.getChildren().add(mediaView);
            mediaPlayer.play();

            // Pasar el MediaPlayer de vuelta al controlador
            mediaPlayerConsumer.accept(mediaPlayer);
        }
    }

    private static void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}




