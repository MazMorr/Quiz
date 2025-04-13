package com.marcosoft.quiz.utils;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.function.Consumer;

import com.marcosoft.quiz.Main;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

/**
 * La clase {@code MediaLoader} proporciona métodos estáticos para cargar y gestionar
 * contenido multimedia (imágenes y videos) en una aplicación JavaFX.
 * 
 * Esta clase permite cargar archivos multimedia desde una ruta relativa, ajustarlos
 * a un contenedor y manejar la reproducción de videos mediante un {@link MediaPlayer}.
 * También incluye métodos para mostrar alertas en caso de errores.
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Carga de imágenes y videos desde rutas relativas.</li>
 *   <li>Gestión de recursos multimedia, incluyendo limpieza de recursos previos.</li>
 *   <li>Soporte para múltiples extensiones de imágenes.</li>
 *   <li>Alertas de error en caso de problemas al cargar archivos multimedia.</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * MediaLoader.loadMedia("ruta/relativa", imageView, mediaView, mediaContainer, mediaPlayer -> {
 *     // Configurar el MediaPlayer si es necesario
 * });
 * }</pre>
 * 
 * @author MazMorr
 * @version 1.0
 */
public class MediaLoader {

    /**
     * Extensiones de archivo soportadas para imágenes.
     */
    private static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".gif"};

    /**
     * Extensión de archivo soportada para videos.
     */
    private static final String VIDEO_EXTENSION = ".mp4";

    /**
     * Ruta base de los recursos multimedia.
     */
    private static String resourcesBasePath = null;

    /**
     * Carga un archivo multimedia (imagen o video) desde una ruta relativa y lo ajusta
     * al contenedor especificado.
     * 
     * @param relativePath La ruta relativa al archivo multimedia.
     * @param imageView El {@link ImageView} donde se mostrará la imagen (si aplica).
     * @param mediaView El {@link MediaView} donde se reproducirá el video (si aplica).
     * @param mediaContainer El contenedor donde se ajustará el contenido multimedia.
     * @param mediaPlayerConsumer Un consumidor que recibe el {@link MediaPlayer} del video cargado.
     */
    public static void loadMedia(String relativePath, ImageView imageView, MediaView mediaView, StackPane mediaContainer, Consumer<MediaPlayer> mediaPlayerConsumer) {
        // Construir la ruta completa usando el directorio base
        String fullPath = Paths.get(Main.getBaseDirectory(), relativePath).toString();

        mediaContainer.getChildren().clear();
        imageView.setImage(null);
        mediaView.setMediaPlayer(null);

        boolean imageFound = false;
        boolean videoFound = false;

        // Verificar si existe un archivo de imagen
        for (String ext : IMAGE_EXTENSIONS) {
            File imageFile = new File(fullPath + ext);
            if (imageFile.exists()) {
                imageFound = true;
                break;
            }
        }

        // Verificar si existe un archivo de video
        File videoFile = new File(fullPath + VIDEO_EXTENSION);
        if (videoFile.exists()) {
            videoFound = true;
        }

        if (videoFound) {
            loadVideo(fullPath, mediaView, mediaContainer, mediaPlayerConsumer);
        } else if (imageFound) {
            loadImage(fullPath, imageView, mediaContainer);
        } else {
            showAlert("No se encontraron archivos multimedia en la ruta especificada.");
        }
    }

    /**
     * Carga una imagen desde la ruta especificada y la ajusta al contenedor.
     * 
     * @param questionFolderPath La ruta completa a la carpeta que contiene la imagen.
     * @param imageView El {@link ImageView} donde se mostrará la imagen.
     * @param mediaContainer El contenedor donde se ajustará la imagen.
     */
    private static void loadImage(String questionFolderPath, ImageView imageView, StackPane mediaContainer) {
        for (String ext : IMAGE_EXTENSIONS) {
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

    /**
     * Carga un video desde la ruta especificada y lo ajusta al contenedor.
     * 
     * @param questionFolderPath La ruta completa a la carpeta que contiene el video.
     * @param mediaView El {@link MediaView} donde se reproducirá el video.
     * @param mediaContainer El contenedor donde se ajustará el video.
     * @param mediaPlayerConsumer Un consumidor que recibe el {@link MediaPlayer} del video cargado.
     */
    private static void loadVideo(String questionFolderPath, MediaView mediaView, StackPane mediaContainer, Consumer<MediaPlayer> mediaPlayerConsumer) {
        try {
            File videoFile = new File(questionFolderPath + VIDEO_EXTENSION);
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
            } else {
                showAlert("Archivo de video no encontrado: " + videoFile.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert("Error al cargar el video: " + e.getMessage());
        }
    }

    /**
     * Muestra una alerta de error con el mensaje especificado.
     * 
     * @param message El mensaje de error a mostrar.
     */
    private static void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Obtiene la ruta base de los recursos multimedia. Este método se asegura de que
     * la ruta base se inicialice solo una vez.
     */
    private static synchronized void getResourcesBasePath() {
        if (resourcesBasePath != null) {
            return; // Ya está inicializado
        }

        try {
            URL resource = MediaLoader.class.getResource("/chounion/quizmaven/resources");
            if (resource != null) {
                resourcesBasePath = Paths.get(resource.toURI()).toFile().getAbsolutePath() + File.separator;
            } else {
                showAlert("No se pudo determinar la ruta base de recursos.");
            }
        } catch (URISyntaxException e) {
            showAlert("Error al obtener la ruta base de recursos: " + e.getMessage());
        }
    }
}
