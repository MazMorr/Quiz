package com.marcosoft.quiz.utils;

import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.services.impl.ClientServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Directories creator.
 */
@Component
public class DirectoriesCreator {

    // Ruta base relativa al ejecutable
    private final String BASE_PATH = getBasePath();

    /**
     * The Client service.
     */
    @Autowired
    ClientServiceImpl clientService;

    @Setter
    @Getter
    private int thematicCount = 4;    // Valor por defecto
    @Setter
    @Getter
    private int questionsPerThematic = 6; // Valor por defecto


    /**
     * Gets base path.
     *
     * @return the base path
     */
    public static String getBasePath() {
        try {
            // Intenta obtener la ruta del ejecutable (JAR) o del directorio de trabajo
            java.net.URI uri = DirectoriesCreator.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File baseFile;
            try {
                baseFile = new File(uri);
                if (!baseFile.isDirectory()) {
                    baseFile = baseFile.getParentFile();
                }
            } catch (IllegalArgumentException e) {
                // Si el URI no es jerárquico (ejecución desde jar spring-boot), usa user.dir
                baseFile = new File(System.getProperty("user.dir"));
            }
            return new File(baseFile, "ArchivosQuiz").getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo determinar la ruta base del ejecutable.", e);
        }
    }

    /**
     * Create directory.
     *
     * @param path the path
     */
    public void createDirectory(String path) {
        File file = new File(path);
        if (!file.exists() && file.mkdirs()) {
            System.out.println("Directorio creado: " + path);
        } else if (!file.exists()) {
            System.err.println("No se pudo crear el directorio: " + path);
        }
    }

    /**
     * Create all directories for the quiz.
     */
    public void createAllDirectoriesForTheQuiz() {
        thematicCount = clientService.getClientById(1).getThematicNumber();
        questionsPerThematic = clientService.getClientById(1).getQuestionNumber();
        try {
            // Crear lista de temáticas según configuración
            List<String> thematicPaths = new ArrayList<>();
            for (int i = 1; i <= thematicCount; i++) {
                thematicPaths.add("Temática" + i);
            }

            List<String> subPaths = List.of("ChiviTemática", "ImagenTemática");

            // Crear lista de preguntas según configuración
            List<String> questionPaths = new ArrayList<>();
            for (int i = 1; i <= questionsPerThematic; i++) {
                questionPaths.add("Pregunta" + i);
            }

            List<File> directories = new ArrayList<>();
            List<File> files = new ArrayList<>();

            createQuestionsAndFilesForEachThematic(thematicPaths, subPaths, directories, questionPaths, files);

            createDirs(directories);

            // Crear archivos
            for (File file : files) {
                if (!file.exists() && file.createNewFile()) {
                    System.out.println("Archivo creado: " + file.getAbsolutePath());
                    if (file.getName().equals("NúmeroRespuestaCorrecta.txt")) {
                        try (FileWriter writer = new FileWriter(file)) {
                            writer.write("1");
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

    private void createQuestionsAndFilesForEachThematic(List<String> thematicPaths, List<String> subPaths, List<File> directories, List<String> questionPaths, List<File> files) {
        // Crear directorios y archivos para cada temática
        for (String thematic : thematicPaths) {
            // Subdirectorios
            for (String subPath : subPaths) {
                directories.add(new File(BASE_PATH + "/" + thematic + "/" + subPath));
            }

            // Preguntas
            for (String question : questionPaths) {
                String questionPath = BASE_PATH + "/" + thematic + "/" + question;
                directories.add(new File(questionPath));
                files.add(new File(questionPath + "/Pregunta.txt"));
                files.add(new File(questionPath + "/Respuesta1.txt"));
                files.add(new File(questionPath + "/Respuesta2.txt"));
                files.add(new File(questionPath + "/Respuesta3.txt"));
                files.add(new File(questionPath + "/NúmeroRespuestaCorrecta.txt"));
            }

            // Directorio y archivo de nombre de temática
            String thematicNamePath = BASE_PATH + "/" + thematic + "/NombreTemática";
            directories.add(new File(thematicNamePath));
            files.add(new File(thematicNamePath + "/nombre.txt"));
        }
    }

    private void createDirs(List<File> directories) {
        for (File dir : directories) {
            if (!dir.exists() && dir.mkdirs()) {
                System.out.println("Directorio creado: " + dir.getAbsolutePath());
            } else if (!dir.exists()) {
                System.err.println("No se pudo crear el directorio: " + dir.getAbsolutePath());
            }
        }
    }

}
