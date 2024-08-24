package chounion.quizmaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Usuario
 */
public class FileReader {
        public static String readTextFile(String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        
        try (InputStream is = FileReader.class.getResourceAsStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return contentBuilder.toString();
    }
}
