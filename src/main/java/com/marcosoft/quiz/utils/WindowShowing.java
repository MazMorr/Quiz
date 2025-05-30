package com.marcosoft.quiz.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MazMorr
 */
@Component
public class WindowShowing {

    private static WindowShowing instance;

    //ExistencyView booleans
    @Getter
    @Setter
    private boolean sellViewShowing;
    @Getter
    @Setter
    private boolean buyViewShowing;
    @Getter
    @Setter
    private boolean filterViewShowing;
    @Setter
    @Getter
    private boolean configurationShowing;
    @Getter
    @Setter
    private boolean registryFilterViewShowing;

    private final List<Stage> openStages = new ArrayList<>();

    public WindowShowing() {
    }

    public WindowShowing(boolean sellViewShowing, boolean buyViewShowing, boolean filterViewShowing, boolean configurationShowing, boolean registryFilterViewShowing) {
        this.sellViewShowing = sellViewShowing;
        this.buyViewShowing = buyViewShowing;
        this.filterViewShowing = filterViewShowing;
        this.configurationShowing = configurationShowing;
        this.registryFilterViewShowing = registryFilterViewShowing;
    }

    public static WindowShowing getInstance() {
        if (instance == null) {
            instance = new WindowShowing();
        }
        return instance;
    }

    public void displayAssistance(boolean viewShowing, String fxmlPath, String errorMessage, int auxView) throws IOException {
        if (!viewShowing) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.getIcons().add(new Image("file:resources/images/RTS_logo.png"));
            stage.show();
            openStages.add(stage);

        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }
    }

    public void closeAllWindows() {
        try {
            if (!openStages.isEmpty()) {
                for (Stage stage : openStages) {
                    stage.close();
                }
                openStages.clear();
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setContentText("openStages está vacía, hay un problema tanke");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
