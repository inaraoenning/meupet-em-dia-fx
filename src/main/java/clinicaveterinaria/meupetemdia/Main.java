package clinicaveterinaria.meupetemdia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    //  referência global da janela principal
    private static Stage primaryStage;

    // permitir que qualquer controller  consiga acessar a janela principal
    public static Stage getPrimaryStage() {
        return primaryStage;
    }


    @Override
    public void start(Stage stage) throws Exception {

        // método para conseguir essa referência em qualquer controller
        primaryStage = stage;
        System.out.println(Main.class.getResource("/view/login.fxml"));

        // Carrega a tela de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));

        // Carrega o CSS corretamente

        Scene scene = new Scene(loader.load());
        stage.setTitle("Meu Pet em Dia - Sistema de Gestão Veterinária");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}
