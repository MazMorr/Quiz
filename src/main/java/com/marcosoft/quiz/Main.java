package com.marcosoft.quiz;

import com.marcosoft.quiz.utils.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.marcosoft.quiz", exclude = {CacheAutoConfiguration.class,SecurityAutoConfiguration.class})
public class Main extends Application {

	private static ConfigurableApplicationContext context;
	@Autowired
	private static SpringFXMLLoader springFXMLLoader;

	@Getter
	private static Stage primaryStage;
	private static Scene scene;

    public static String getBaseDirectory() {
        try {
            // Obtiene la ruta del ejecutable (JAR) o del directorio de trabajo
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParentFile()
                    .getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo determinar la ruta base del ejecutable.", e);
        }
    }

    @Override
	public void init() throws Exception {

		context = SpringApplication.run(Main.class);
		springFXMLLoader = context.getBean(SpringFXMLLoader.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Main.primaryStage = primaryStage;
		Parent root = (Parent) springFXMLLoader.load("/menuView.fxml");
		scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResource("/images/cho_logo.png").toString()));
		primaryStage.setResizable(false);
		primaryStage.setTitle("Quiz by MazMorr");
		primaryStage.centerOnScreen();
		primaryStage.show();

		//Load all the fonts
		Font.loadFont(getClass().getResourceAsStream("/fonts/Helvetica-Bold.ttf"), 20);
		Font.loadFont(getClass().getResourceAsStream("/fonts/mk2.ttf"), 20);
		Font.loadFont(getClass().getResourceAsStream("/fonts/Helvetica.ttf"), 30);
		Font.loadFont(getClass().getResourceAsStream("/fonts/Helvetica-BoldOblique.ttf"), 20);
		Font.loadFont(getClass().getResourceAsStream("/fonts/Helvetica-Oblique.ttf"), 20);
		Font.loadFont(getClass().getResourceAsStream("/fonts/helvetica-rounded-bold.otf"), 20);

	}

	@Override
	public void stop() throws Exception {
		context.close();
	}

	public static void setRoot(String fxml) {
		try {
			Parent root = (Parent) springFXMLLoader.load("/" + fxml + ".fxml");
			scene.setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
