package com.marcosoft.quiz.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SoundPlayer {

    MediaPlayer mediaPlayerMusic;
    MediaPlayer mediaPlayerSound;

    public void playMusic(String absolutePath) {
        try {
            // 1. Detener música anterior si existe
            if (mediaPlayerMusic != null) {
                mediaPlayerMusic.stop();
                mediaPlayerMusic.dispose();
            }

            // 2. Crear Media con la ruta ABSOLUTA del archivo
            Media sound = new Media(new File(absolutePath).toURI().toString());
            mediaPlayerMusic = new MediaPlayer(sound);

            // 3. Reproducir en bucle
            mediaPlayerMusic.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayerMusic.play();

        } catch (Exception e) {
            System.err.println("Error al reproducir: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (mediaPlayerMusic != null) {
            mediaPlayerMusic.stop();
        }
    }

    public void pauseMusic() {
        if (mediaPlayerMusic != null) {
            mediaPlayerMusic.pause();
        }
    }

    public void keepPlayingMusic() {
        if (mediaPlayerMusic != null) {
            mediaPlayerMusic.play();
        }
    }


    public void playSound(String soundPath) {
        try {
            // Crear un objeto Media
            Media sound = new Media(soundPath);
            // Crear un MediaPlayer
            mediaPlayerSound = new MediaPlayer(sound);

            mediaPlayerSound.play();
            mediaPlayerSound.setOnEndOfMedia(() -> {
                mediaPlayerSound.stop();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseSound() {
        if (mediaPlayerSound != null) {
            mediaPlayerSound.pause();
        }
    }

    public void keepPlayingSound() {
        if (mediaPlayerSound != null) {
            mediaPlayerSound.play();
        }
    }

    public void stopSound() {
        if (mediaPlayerSound != null) {
            mediaPlayerSound.stop();
        }
    }

}
