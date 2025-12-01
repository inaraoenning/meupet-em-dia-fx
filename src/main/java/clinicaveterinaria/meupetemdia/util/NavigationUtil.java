package clinicaveterinaria.meupetemdia.util;

import clinicaveterinaria.meupetemdia.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Classe utilitária para navegação entre telas do sistema
public class NavigationUtil {

    // Navega para uma nova tela FXML
    // @param fxmlFileName Nome do arquivo FXML (ex: "login.fxml")

    public static void navigateTo(String fxmlFileName) {
        try {
            String fxmlPath = "/view/" + fxmlFileName;

            Parent root = FXMLLoader.load(
                    NavigationUtil.class.getResource(fxmlPath)
            );

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    NavigationUtil.class.getResource("/css/style.css").toExternalForm()
            );

            Stage stage = Main.getPrimaryStage();
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao navegar para: " + fxmlFileName);
        }
    }


    // Navega para o menu principal
    public static void navigateToMenu() {
        navigateTo("menuPrincipal.fxml");
    }


    // Navega para a tela de login
    public static void navigateToLogin() {
        navigateTo("login.fxml");
    }


    // Navega para cadastro de donos
    public static void navigateToCadastroDonos() {
        navigateTo("cadastroDonos.fxml");
    }


    // Navega para cadastro de pets
    public static void navigateToCadastroPets() {
        navigateTo("cadastroPets.fxml");
    }


    // Navega para cadastro de vacinas e consultas
    public static void navigateToCadastroVacinasConsultas() {
        navigateTo("cadastroVacinasConsultas.fxml");
    }


    // Navega para tela de próximas vacinas
    public static void navigateToProximasVacinas() {
        navigateTo("proximasVacinas.fxml");
    }


    // Navega para tela de relatórios
    public static void navigateToRelatorios() {
        navigateTo("relatorios.fxml");
    }
}