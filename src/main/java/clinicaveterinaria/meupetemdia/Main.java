package clinicaveterinaria.meupetemdia;

import clinicaveterinaria.meupetemdia.config.DatabaseConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Inicializar banco de dados SQLite
        try {
            DatabaseConfig.initialize();

            // Criar vacinas padrão se não existirem
            new clinicaveterinaria.meupetemdia.dao.VacinaDAO().criarVacinasPadrao();

        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar banco: " + e.getMessage());
            e.printStackTrace();
        }

        primaryStage = stage;


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
        );

        stage.setTitle("Meu Pet em Dia - Sistema de Gestão Veterinária");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Fechar conexão com banco ao encerrar aplicação
        DatabaseConfig.close();
        super.stop();
    }
}