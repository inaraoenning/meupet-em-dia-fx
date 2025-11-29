package clinicaveterinaria.meupetemdia.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller da tela de Login
 * Responsável pela autenticação via Firebase Authentication
 */
public class LoginController {

    // COMPONENTES
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtSenha;
    @FXML
    private Label lblErroEmail;
    @FXML
    private Label lblErroSenha;
    @FXML
    private Label lblErroGeral;
    @FXML
    private Button btnEntrar;
    @FXML
    private ProgressIndicator progressIndicator;

    //  INICIALIZAÇÃO
    @FXML
    private void initialize() {
        // Configurações iniciais
        // Adicionar listeners para limpar erros ao digitar
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            limparErro(txtEmail, lblErroEmail);
        });

        txtSenha.textProperty().addListener((obs, oldVal, newVal) -> {
            limparErro(txtSenha, lblErroSenha);
        });
    }

    //  AÇÃO DE LOGIN
    @FXML
    private void handleLogin() {
        // Limpar erros anteriores
        limparTodosErros();

        // Obter valores dos campos
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();

        // Validar campos
        if (!validarCampos(email, senha)) {
            return;
        }

        // TODO: Integrar com FirebaseAuthService
        // FirebaseAuthService.login(email, senha)
        //     .thenAccept(sucesso -> {
        //         if (sucesso) {
        //             NavigationUtil.navigateToMenu();
        //         } else {
        //             mostrarErroGeral("E-mail ou senha incorretos");
        //         }
        //         mostrarCarregamento(false);
        //     })
        //     .exceptionally(e -> {
        //         mostrarErroGeral("Erro ao conectar: " + e.getMessage());
        //         mostrarCarregamento(false);
        //         return null;
        //     });

    }

    //  VALIDAÇÕES
    private boolean validarCampos(String email, String senha) {
        boolean valido = true;

        // Validar e-mail vazio
        if (email.isEmpty()) {
            mostrarErro(txtEmail, lblErroEmail, "O e-mail é obrigatório");
            valido = false;
        }
        // Validar formato do e-mail
        else if (!validarFormatoEmail(email)) {
            mostrarErro(txtEmail, lblErroEmail, "E-mail inválido");
            valido = false;
        }

        // Validar senha vazia
        if (senha.isEmpty()) {
            mostrarErro(txtSenha, lblErroSenha, "A senha é obrigatória");
            valido = false;
        }
        // Validar tamanho mínimo da senha
        else if (senha.length() < 6) {
            mostrarErro(txtSenha, lblErroSenha, "A senha deve ter no mínimo 6 caracteres");
            valido = false;
        }

        return valido;
    }

    private boolean validarFormatoEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    //  MÉTODOS AUXILIARES
    private void mostrarErro(TextField campo, Label labelErro, String mensagem) {
        if (!campo.getStyleClass().contains("error")) {
            campo.getStyleClass().add("error");
        }
        labelErro.setText(mensagem);
        labelErro.setVisible(true);
        labelErro.setManaged(true);
    }

    private void limparErro(TextField campo, Label labelErro) {
        campo.getStyleClass().remove("error");
        labelErro.setVisible(false);
        labelErro.setManaged(false);
    }

    private void limparTodosErros() {
        limparErro(txtEmail, lblErroEmail);
        limparErro(txtSenha, lblErroSenha);
        lblErroGeral.setVisible(false);
        lblErroGeral.setManaged(false);
    }

    private void mostrarErroGeral(String mensagem) {
        lblErroGeral.setText(mensagem);
        lblErroGeral.setVisible(true);
        lblErroGeral.setManaged(true);
    }

}