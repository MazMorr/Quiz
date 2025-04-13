package com.marcosoft.quiz.utils;

import com.marcosoft.quiz.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileReader {

    /**
     * Lee un archivo de texto desde el classpath o el sistema de archivos.
     * 
     * @param relativePath La ruta del archivo (relativa).
     * @return El contenido del archivo como una cadena.
     */
    public static String readTextFile(String relativePath) {
        StringBuilder contentBuilder = new StringBuilder();

        // Construir la ruta completa usando el directorio base
        String fullPath = Paths.get(Main.getBaseDirectory(), relativePath).toString();

        try (InputStream is = getInputStream(fullPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo: " + fullPath, e);
        }

        return contentBuilder.toString();
    }

    /**
     * Obtiene un InputStream desde el classpath o el sistema de archivos.
     * 
     * @param filePath La ruta del archivo.
     * @return Un InputStream para leer el archivo.
     * @throws FileNotFoundException Si el archivo no se encuentra.
     */
    private static InputStream getInputStream(String filePath) throws FileNotFoundException {
        // Intentar cargar desde el classpath
        InputStream is = FileReader.class.getResourceAsStream(filePath);
        if (is != null) {
            return is;
        }

        // Si no está en el classpath, intentar cargar desde el sistema de archivos
        File file = new File(filePath);
        if (file.exists()) {
            return new FileInputStream(file);
        }

        throw new FileNotFoundException("Archivo no encontrado: " + filePath);
    }
}
