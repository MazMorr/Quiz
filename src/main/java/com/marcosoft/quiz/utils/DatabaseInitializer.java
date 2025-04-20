package com.marcosoft.quiz.utils;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DatabaseInitializer {

    public void init() {
        // Ruta de la carpeta donde se almacenará la base de datos
        File quizDir = new File("C:/Quiz");
        
        // Verificar si la carpeta existe, si no, crearla
        if (!quizDir.exists()) {
            if (quizDir.mkdirs()) {
                System.out.println("Directorio 'C:/Quiz' creado exitosamente.");
            } else {
                throw new RuntimeException("No se pudo crear el directorio 'C:/Quiz'.");
            }
        }
    }
}