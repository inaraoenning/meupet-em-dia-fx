package clinicaveterinaria.meupetemdia.controller;

import clinicaveterinaria.meupetemdia.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Optional;

//Controller da tela de Menu Principal
//Responsável pela navegação entre as funcionalidades do sistema

public class MenuPrincipalController {

    // COMPONENTES FXML
    @FXML
    private Label lblUsuario;
    @FXML
    private Button btnLogout;
    @FXML
    private VBox btnCadastroDonos;
    @FXML
    private VBox btnCadastroPets;
    @FXML
    private VBox btnVacinasConsultas;
    @FXML
    private VBox btnProximasVacinas;
    @FXML
    private VBox btnRelatorios;

    // INICIALIZAÇÃO
    @FXML
    private void initialize() {
        // Carregar informações do usuário logado
        carregarInformacoesUsuario();

        // Adicionar efeitos visuais aos botões (opcional)
        adicionarEfeitosVisuais();
    }


    // Carrega o nome do usuário logado
    private void carregarInformacoesUsuario() {
        // TODO: Integrar com AuthService para pegar o usuário atual
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if (user != null) {
        //     lblUsuario.setText("Bem-vindo, " + user.getDisplayName());
        // }

        // SIMULAÇÃO TEMPORÁRIA
        lblUsuario.setText("Bem-vindo");
    }


    // Adiciona efeitos visuais adicionais aos cards (opcional)
    private void adicionarEfeitosVisuais() {
        // Aqui você pode adicionar animações ou efeitos extras
        // Por exemplo: animação de entrada dos cards
    }

    // NAVEGAÇÃO PARA TELAS


    // Navega para a tela de Cadastro de Donos
    @FXML
    private void handleCadastroDonos() {
        NavigationUtil.navigateToCadastroDonos();
    }


    // Navega para a tela de Cadastro de Pets
    @FXML
    private void handleCadastroPets() {
        NavigationUtil.navigateToCadastroPets();
    }


    // Navega para a tela de Vacinas e Consultas
    @FXML
    private void handleVacinasConsultas() {
        NavigationUtil.navigateToCadastroVacinasConsultas();
    }


    // Navega para a tela de Próximas Vacinas
    @FXML
    private void handleProximasVacinas() {
        NavigationUtil.navigateToProximasVacinas();
    }


    // Navega para a tela de Relatórios
    @FXML
    private void handleRelatorios() {
        NavigationUtil.navigateToRelatorios();
    }

    // LOGOUT
    // Realiza o logout do usuárioExibe confirmação antes de sair
    @FXML
    private void handleLogout() {
        // Mostrar confirmação
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Saída");
        alert.setHeaderText("Deseja realmente sair do sistema?");
        alert.setContentText("Você será redirecionado para a tela de login.");

        // Aplicar estilo personalizado (opcional)
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
        );

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            realizarLogout();
        }
    }

    // Executa o processo de logout
    private void realizarLogout() {
        try {
            // TODO: Integrar com AuthService
            // FirebaseAuth.getInstance().signOut();

            // Limpar dados da sessão (se houver cache local)
            limparSessao();

            // Navegar para tela de login
            NavigationUtil.navigateToLogin();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao fazer logout: " + e.getMessage());
        }
    }


    // TODO: Limpa dados da sessão local
    private void limparSessao() {
        // Limpar preferências, cache, ou dados temporários
    }

    // Exibe mensagem de erro
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro");
        alert.setContentText(mensagem);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
        );

        alert.showAndWait();
    }
}