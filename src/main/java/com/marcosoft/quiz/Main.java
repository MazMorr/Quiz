package com.marcosoft.quiz;

import com.marcosoft.quiz.utils.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.marcosoft.quiz")
public class Main extends Application {

	private static ConfigurableApplicationContext context;
	@Autowired
	private static SpringFXMLLoader springFXMLLoader;
	private static Stage primaryStage;
	private static Scene scene;

    public static String getBaseDirectory() {
		//aquí tiene que devolver la ruta del jar
        return "";
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
		primaryStage.setResizable(true);
		primaryStage.setTitle("Quiz by MazMorr");
		primaryStage.centerOnScreen();
		primaryStage.show();
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
			// Puedes agregar un manejo de errores más sofisticado aquí si es necesario
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
