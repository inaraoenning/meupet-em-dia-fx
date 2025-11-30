package clinicaveterinaria.meupetemdia.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import clinicaveterinaria.meupetemdia.model.Dono;
import clinicaveterinaria.meupetemdia.util.NavigationUtil;

import java.util.Optional;

/**
 * Controller da tela de Cadastro de Donos
 * Responsável pelo CRUD de proprietários de pets
 */
public class CadastroDonosController {

    // ========== COMPONENTES DO FORMULÁRIO ==========
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private TextArea txtEndereco;

    @FXML private Label lblErroNome;
    @FXML private Label lblErroTelefone;
    @FXML private Label lblErroEmail;
    @FXML private Label lblErroEndereco;

    @FXML private Button btnSalvar;
    @FXML private Button btnLimpar;
    @FXML private Button btnCancelar;

    // ========== COMPONENTES DA TABELA ==========
    @FXML private TextField txtBuscar;
    @FXML private TableView<Dono> tblDonos;
    @FXML private TableColumn<Dono, Integer> colId;
    @FXML private TableColumn<Dono, String> colNome;
    @FXML private TableColumn<Dono, String> colTelefone;
    @FXML private TableColumn<Dono, String> colEmail;
    @FXML private TableColumn<Dono, String> colEndereco;

    @FXML private Button btnEditar;
    @FXML private Button btnExcluir;

    // ========== DADOS ==========
    private ObservableList<Dono> listaDonos;
    private FilteredList<Dono> donosFiltrados;
    private Dono donoEmEdicao;
    private boolean modoEdicao = false;

    // ========== INICIALIZAÇÃO ==========
    @FXML
    private void initialize() {
        configurarTabela();
        configurarBusca();
        configurarMascaras();
        configurarListeners();
        carregarDonos();
    }

    /**
     * Configura as colunas da tabela
     */
    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        // Listener para seleção na tabela
        tblDonos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean itemSelecionado = newVal != null;
            btnEditar.setDisable(!itemSelecionado);
            btnExcluir.setDisable(!itemSelecionado);
        });
    }

    /**
     * Configura a busca em tempo real
     */
    private void configurarBusca() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (donosFiltrados != null) {
                donosFiltrados.setPredicate(dono -> {
                    if (newVal == null || newVal.isEmpty()) {
                        return true;
                    }
                    String busca = newVal.toLowerCase();
                    return dono.getNome().toLowerCase().contains(busca) ||
                            dono.getTelefone().contains(busca) ||
                            dono.getEmail().toLowerCase().contains(busca);
                });
            }
        });
    }

    /**
     * Configura máscaras nos campos
     */
    private void configurarMascaras() {
        // Máscara de telefone: (00) 00000-0000
        txtTelefone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String numeros = newVal.replaceAll("[^0-9]", "");
                if (numeros.length() > 11) {
                    numeros = numeros.substring(0, 11);
                }

                StringBuilder formatado = new StringBuilder();
                if (numeros.length() > 0) {
                    formatado.append("(");
                    formatado.append(numeros.substring(0, Math.min(2, numeros.length())));
                }
                if (numeros.length() >= 3) {
                    formatado.append(") ");
                    formatado.append(numeros.substring(2, Math.min(7, numeros.length())));
                }
                if (numeros.length() >= 8) {
                    formatado.append("-");
                    formatado.append(numeros.substring(7));
                }

                if (!newVal.equals(formatado.toString())) {
                    txtTelefone.setText(formatado.toString());
                }
            }
        });
    }

    /**
     * Configura listeners para limpar erros ao digitar
     */
    private void configurarListeners() {
        txtNome.textProperty().addListener((obs, oldVal, newVal) ->
                limparErro(txtNome, lblErroNome));
        txtTelefone.textProperty().addListener((obs, oldVal, newVal) ->
                limparErro(txtTelefone, lblErroTelefone));
        txtEmail.textProperty().addListener((obs, oldVal, newVal) ->
                limparErro(txtEmail, lblErroEmail));
        txtEndereco.textProperty().addListener((obs, oldVal, newVal) ->
                limparErro(txtEndereco, lblErroEndereco));
    }

    /**
     * Carrega donos do banco de dados
     */
    private void carregarDonos() {
        // TODO: Integrar com DonoDAO
        // listaDonos = FXCollections.observableArrayList(DonoDAO.findAll());

        // DADOS SIMULADOS
        listaDonos = FXCollections.observableArrayList(
                new Dono(1, "João Silva", "(11) 98765-4321", "joao@email.com", "Rua A, 123"),
                new Dono(2, "Maria Santos", "(11) 91234-5678", "maria@email.com", "Av. B, 456"),
                new Dono(3, "Pedro Oliveira", "(11) 99999-8888", "pedro@email.com", "Rua C, 789")
        );

        donosFiltrados = new FilteredList<>(listaDonos, p -> true);
        tblDonos.setItems(donosFiltrados);
    }

    // ========== AÇÕES DOS BOTÕES ==========

    @FXML
    private void handleVoltar() {
        NavigationUtil.navigateToMenu();
    }

    @FXML
    private void handleSalvar() {
        limparTodosErros();

        if (!validarCampos()) {
            return;
        }

        try {
            if (modoEdicao) {
                atualizarDono();
            } else {
                inserirDono();
            }

            carregarDonos();
            limparFormulario();
            mostrarSucesso(modoEdicao ? "Dono atualizado com sucesso!" : "Dono cadastrado com sucesso!");

        } catch (Exception e) {
            mostrarErro("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpar() {
        limparFormulario();
    }

    @FXML
    private void handleCancelar() {
        limparFormulario();
        desativarModoEdicao();
    }

    @FXML
    private void handleEditar() {
        Dono donoSelecionado = tblDonos.getSelectionModel().getSelectedItem();
        if (donoSelecionado != null) {
            preencherFormulario(donoSelecionado);
            ativarModoEdicao();
        }
    }

    @FXML
    private void handleExcluir() {
        Dono donoSelecionado = tblDonos.getSelectionModel().getSelectedItem();
        if (donoSelecionado == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deseja realmente excluir este dono?");
        alert.setContentText("Nome: " + donoSelecionado.getNome() + "\n" +
                "Esta ação não poderá ser desfeita.");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            excluirDono(donoSelecionado);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private boolean validarCampos() {
        boolean valido = true;

        // Validar nome
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            mostrarErro(txtNome, lblErroNome, "O nome é obrigatório");
            valido = false;
        } else if (nome.length() < 3) {
            mostrarErro(txtNome, lblErroNome, "O nome deve ter pelo menos 3 caracteres");
            valido = false;
        }

        // Validar telefone
        String telefone = txtTelefone.getText().trim();
        if (telefone.isEmpty()) {
            mostrarErro(txtTelefone, lblErroTelefone, "O telefone é obrigatório");
            valido = false;
        } else if (!telefone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
            mostrarErro(txtTelefone, lblErroTelefone, "Telefone inválido");
            valido = false;
        }

        // Validar e-mail
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            mostrarErro(txtEmail, lblErroEmail, "O e-mail é obrigatório");
            valido = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarErro(txtEmail, lblErroEmail, "E-mail inválido");
            valido = false;
        }

        // Validar endereço
        String endereco = txtEndereco.getText().trim();
        if (endereco.isEmpty()) {
            mostrarErro(txtEndereco, lblErroEndereco, "O endereço é obrigatório");
            valido = false;
        }

        return valido;
    }

    private void inserirDono() {
        Dono novoDono = new Dono(
                listaDonos.size() + 1, // Simular ID auto-incremento
                txtNome.getText().trim(),
                txtTelefone.getText().trim(),
                txtEmail.getText().trim(),
                txtEndereco.getText().trim()
        );

        // TODO: DonoDAO.insert(novoDono);
        listaDonos.add(novoDono);
    }

    private void atualizarDono() {
        donoEmEdicao.setNome(txtNome.getText().trim());
        donoEmEdicao.setTelefone(txtTelefone.getText().trim());
        donoEmEdicao.setEmail(txtEmail.getText().trim());
        donoEmEdicao.setEndereco(txtEndereco.getText().trim());

        // TODO: DonoDAO.update(donoEmEdicao);
        tblDonos.refresh();
    }

    private void excluirDono(Dono dono) {
        // TODO: DonoDAO.delete(dono.getId());
        listaDonos.remove(dono);
        mostrarSucesso("Dono excluído com sucesso!");
    }

    private void preencherFormulario(Dono dono) {
        donoEmEdicao = dono;
        txtNome.setText(dono.getNome());
        txtTelefone.setText(dono.getTelefone());
        txtEmail.setText(dono.getEmail());
        txtEndereco.setText(dono.getEndereco());
    }

    private void limparFormulario() {
        txtNome.clear();
        txtTelefone.clear();
        txtEmail.clear();
        txtEndereco.clear();
        txtBuscar.clear();
        tblDonos.getSelectionModel().clearSelection();
        limparTodosErros();
        desativarModoEdicao();
    }

    private void ativarModoEdicao() {
        modoEdicao = true;
        btnSalvar.setText("Atualizar");
        btnCancelar.setVisible(true);
        btnCancelar.setManaged(true);
    }

    private void desativarModoEdicao() {
        modoEdicao = false;
        donoEmEdicao = null;
        btnSalvar.setText("Salvar");
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
    }

    private void mostrarErro(Control campo, Label labelErro, String mensagem) {
        if (!campo.getStyleClass().contains("error")) {
            campo.getStyleClass().add("error");
        }
        labelErro.setText(mensagem);
        labelErro.setVisible(true);
        labelErro.setManaged(true);
    }

    private void limparErro(Control campo, Label labelErro) {
        campo.getStyleClass().remove("error");
        labelErro.setVisible(false);
        labelErro.setManaged(false);
    }

    private void limparTodosErros() {
        limparErro(txtNome, lblErroNome);
        limparErro(txtTelefone, lblErroTelefone);
        limparErro(txtEmail, lblErroEmail);
        limparErro(txtEndereco, lblErroEndereco);
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}