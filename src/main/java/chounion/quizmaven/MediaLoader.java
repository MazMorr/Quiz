package chounion.quizmaven;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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

    private static String resourcesBasePath = null; // Variable para almacenar la ruta base de recursos

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

        // Obtener la ruta base de recursos solo una vez
        if (resourcesBasePath == null) {
            getResourcesBasePath();
        }

        // Construir la ruta completa a la carpeta de la pregunta actual
        String questionFolderPath = resourcesBasePath + relativePath;

        // Verificar si existe un archivo de imagen
        String[] imageExtensions = {".png", ".jpg", ".gif"};
        for (String ext : imageExtensions) {
            File imageFile = new File(questionFolderPath + ext);
            if (imageFile.exists()) {
                imageFound = true;
                break;
            }
        }

        // Verificar si existe un archivo de video
        File videoFile = new File(questionFolderPath + ".mp4");
        if (videoFile.exists()) {
            videoFound = true;
        }

        if (imageFound && videoFound) {
            showAlert("Se encontraron múltiples archivos multimedia en la ruta especificada.");
            return;
        }

        if (imageFound) {
            loadImage(questionFolderPath, imageView, mediaContainer);
        } else if (videoFound) {
            loadVideo(questionFolderPath, mediaView, mediaContainer, mediaPlayerConsumer);
        }
    }

    private static void loadImage(String questionFolderPath, ImageView imageView, StackPane mediaContainer) {
        String[] imageExtensions = {".png", ".jpg", ".gif"};
        for (String ext : imageExtensions) {
            File imageFile = new File(questionFolderPath + ext);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
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

    private static void loadVideo(String questionFolderPath, MediaView mediaView, StackPane mediaContainer, Consumer<MediaPlayer> mediaPlayerConsumer) {
        File videoFile = new File(questionFolderPath + ".mp4");
        if (videoFile.exists()) {
            Media media = new Media(videoFile.toURI().toString());
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

    // Método para obtener la ruta base de recursos
    private static void getResourcesBasePath() {
        try {
            URL resource = MediaLoader.class.getResource(""); // Obtiene la URL del paquete actual
            if (resource != null) {
                // Obtiene la ruta absoluta a la carpeta "resources"
                resourcesBasePath = Paths.get(resource.toURI()).getParent().getParent().resolve("resources").toFile().getAbsolutePath() + File.separator;
            } else {
                showAlert("No se pudo determinar la ruta base de recursos.");
            }
        } catch (URISyntaxException e) {
            showAlert("Error al obtener la ruta base de recursos: " + e.getMessage());
        }
    }
}
