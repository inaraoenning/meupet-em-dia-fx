package clinicaveterinaria.meupetemdia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Parent root = FXMLLoader.load(
                getClass().getResource("/view/login.fxml")
        );

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
        );

        stage.setTitle("Clínica Veterinária - Sistema de Gestão");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}