package com.marcosoft.quiz.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectoriesCreator {

    // Ruta base relativa al ejecutable
    private static final String BASE_PATH = getBasePath();

    public static String getBasePath() {
        try {
            // Obtiene la ruta del ejecutable (JAR) o del directorio de trabajo
            return new File(DirectoriesCreator.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParentFile()
                    .getAbsolutePath() + "/ArchivosQuiz";
        } catch (Exception e) {
            throw new RuntimeException("No se pudo determinar la ruta base del ejecutable.", e);
        }
    }

    public void createDirectory(String path) {
        File file = new File(path);
        if (!file.exists() && file.mkdirs()) {
            System.out.println("Directorio creado: " + path);
        } else if (!file.exists()) {
            System.err.println("No se pudo crear el directorio: " + path);
        }
    }

    public void createAllDirectoriesForTheQuiz() {
        try {
            List<String> thematicPaths = List.of("Temática1", "Temática2", "Temática3", "Temática4");
            List<String> subPaths = List.of("ChiviTemática", "ImagenTemática");
            List<String> questionPaths = List.of("Pregunta1", "Pregunta2", "Pregunta3", "Pregunta4");

            List<File> directories = new ArrayList<>();
            List<File> files = new ArrayList<>();

            // Crear directorios y archivos para cada temática
            for (String thematic : thematicPaths) {
                // Crear subdirectorios (ChiviTemática, ImagenTemática)
                for (String subPath : subPaths) {
                    directories.add(new File(BASE_PATH + "/" + thematic + "/" + subPath));
                }

                // Crear directorios de preguntas (Pregunta1, Pregunta2, etc.)
                for (String question : questionPaths) {
                    String questionPath = BASE_PATH + "/" + thematic + "/" + question;
                    directories.add(new File(questionPath));

                    // Archivos dentro de cada carpeta de pregunta
                    files.add(new File(questionPath + "/Pregunta.txt"));
                    files.add(new File(questionPath + "/Respuesta1.txt"));
                    files.add(new File(questionPath + "/Respuesta2.txt"));
                    files.add(new File(questionPath + "/Respuesta3.txt"));
                    files.add(new File(questionPath + "/NúmeroRespuestaCorrecta.txt"));
                }

                // Crear directorio "NombreTemática" con archivo nombre.txt
                String thematicNamePath = BASE_PATH + "/" + thematic + "/NombreTemática";
                directories.add(new File(thematicNamePath));
                files.add(new File(thematicNamePath + "/nombre.txt"));
            }

            // Crear directorios
            for (File dir : directories) {
                if (!dir.exists() && dir.mkdirs()) {
                    System.out.println("Directorio creado: " + dir.getAbsolutePath());
                } else if (!dir.exists()) {
                    System.err.println("No se pudo crear el directorio: " + dir.getAbsolutePath());
                }
            }

            // Crear archivos
            for (File file : files) {
                if (!file.exists() && file.createNewFile()) {
                    System.out.println("Archivo creado: " + file.getAbsolutePath());

                    // Si es un archivo NúmeroRespuestaCorrecta.txt, escribe un valor predeterminado
                    if (file.getName().equals("NúmeroRespuestaCorrecta.txt")) {
                        try (FileWriter writer = new FileWriter(file)) {
                            writer.write("1"); // Por defecto, la respuesta correcta es la 1
                        }
                    }
                } else if (!file.exists()) {
                    System.err.println("No se pudo crear el archivo: " + file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al crear directorios o archivos: " + e.getMessage());
        }
    }
}
