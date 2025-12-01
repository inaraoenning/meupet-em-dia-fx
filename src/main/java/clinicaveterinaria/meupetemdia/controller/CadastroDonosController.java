package clinicaveterinaria.meupetemdia.controller;

import clinicaveterinaria.meupetemdia.dao.DonoDAO;
import clinicaveterinaria.meupetemdia.model.Dono;
import clinicaveterinaria.meupetemdia.util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;


// Controller da tela de Cadastro de Donos

public class CadastroDonosController {

    // COMPONENTES DO FORMULÁRIO
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtTelefone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtEndereco;

    @FXML
    private Label lblErroNome;
    @FXML
    private Label lblErroTelefone;
    @FXML
    private Label lblErroEmail;
    @FXML
    private Label lblErroEndereco;

    @FXML
    private Button btnSalvar;
    @FXML
    private Button btnLimpar;
    @FXML
    private Button btnCancelar;

    // COMPONENTES DA TABELA
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Dono> tblDonos;
    @FXML
    private TableColumn<Dono, Integer> colId;
    @FXML
    private TableColumn<Dono, String> colNome;
    @FXML
    private TableColumn<Dono, String> colTelefone;
    @FXML
    private TableColumn<Dono, String> colEmail;
    @FXML
    private TableColumn<Dono, String> colEndereco;

    @FXML
    private Button btnEditar;
    @FXML
    private Button btnExcluir;

    // DADOS
    private ObservableList<Dono> listaDonos;
    private FilteredList<Dono> donosFiltrados;

    private Dono donoEmEdicao;
    private boolean modoEdicao = false;

    private final DonoDAO donoDAO = new DonoDAO();

    // INICIALIZAÇÃO
    @FXML
    private void initialize() {
        configurarTabela();
        configurarBusca();
        configurarMascaras();
        configurarListeners();
        carregarDonos();
    }


    // Configura as colunas da tabela
    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        tblDonos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean itemSelecionado = newVal != null;
            btnEditar.setDisable(!itemSelecionado);
            btnExcluir.setDisable(!itemSelecionado);
        });
    }

    //Configura a busca em tempo real
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
                            dono.getEmail().toLowerCase().contains(busca) ||
                            dono.getEndereco().toLowerCase().contains(busca);
                });
            }
        });
    }

    //Máscaras para telefone
    private void configurarMascaras() {
        txtTelefone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            String numeros = newVal.replaceAll("[^0-9]", "");
            if (numeros.length() > 11) numeros = numeros.substring(0, 11);

            StringBuilder f = new StringBuilder();

            if (numeros.length() > 0) {
                f.append("(").append(numeros.substring(0, Math.min(2, numeros.length())));
            }
            if (numeros.length() >= 3) {
                f.append(") ").append(numeros.substring(2, Math.min(7, numeros.length())));
            }
            if (numeros.length() >= 8) {
                f.append("-").append(numeros.substring(7));
            }

            if (!newVal.equals(f.toString())) {
                txtTelefone.setText(f.toString());
            }
        });
    }

    //Limpa erros ao digitar
    private void configurarListeners() {
        txtNome.textProperty().addListener((obs, o, n) -> limparErro(txtNome, lblErroNome));
        txtTelefone.textProperty().addListener((obs, o, n) -> limparErro(txtTelefone, lblErroTelefone));
        txtEmail.textProperty().addListener((obs, o, n) -> limparErro(txtEmail, lblErroEmail));
        txtEndereco.textProperty().addListener((obs, o, n) -> limparErro(txtEndereco, lblErroEndereco));
    }

    //CARREGA DADOS DO BANCO
    private void carregarDonos() {
        listaDonos = FXCollections.observableArrayList(donoDAO.findAll());
        donosFiltrados = new FilteredList<>(listaDonos, p -> true);
        tblDonos.setItems(donosFiltrados);
    }

    // AÇÕES
    @FXML
    private void handleVoltar() {
        NavigationUtil.navigateToMenu();
    }

    @FXML
    private void handleSalvar() {
        limparTodosErros();
        if (!validarCampos()) return;

        try {
            if (modoEdicao) {
                atualizarDonoBanco();
            } else {
                inserirDonoBanco();
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
        Dono d = tblDonos.getSelectionModel().getSelectedItem();
        if (d == null) return;

        donoEmEdicao = d;
        preencherFormulario(d);
        ativarModoEdicao();
    }

    @FXML
    private void handleExcluir() {
        Dono d = tblDonos.getSelectionModel().getSelectedItem();
        if (d == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir Dono");
        alert.setHeaderText("Deseja realmente excluir?");
        alert.setContentText("Nome: " + d.getNome());

        Optional<ButtonType> r = alert.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) {
            donoDAO.delete(d.getId());
            listaDonos.remove(d);
            mostrarSucesso("Dono excluído!");
        }
    }

    // CRUD REAL NO BANCO

    private void inserirDonoBanco() {
        Dono novo = new Dono();
        novo.setNome(txtNome.getText().trim());
        novo.setTelefone(txtTelefone.getText().trim());
        novo.setEmail(txtEmail.getText().trim());
        novo.setEndereco(txtEndereco.getText().trim());

        int id = donoDAO.insert(novo);
        novo.setId(id);
        listaDonos.add(novo);
    }

    private void atualizarDonoBanco() {
        donoEmEdicao.setNome(txtNome.getText().trim());
        donoEmEdicao.setTelefone(txtTelefone.getText().trim());
        donoEmEdicao.setEmail(txtEmail.getText().trim());
        donoEmEdicao.setEndereco(txtEndereco.getText().trim());

        donoDAO.update(donoEmEdicao);
    }

    // AUXILIARES

    private void preencherFormulario(Dono d) {
        txtNome.setText(d.getNome());
        txtTelefone.setText(d.getTelefone());
        txtEmail.setText(d.getEmail());
        txtEndereco.setText(d.getEndereco());
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

    private boolean validarCampos() {
        boolean valido = true;

        if (txtNome.getText().trim().isEmpty()) {
            mostrarErro(txtNome, lblErroNome, "Nome obrigatório");
            valido = false;
        }

        if (txtTelefone.getText().trim().isEmpty()) {
            mostrarErro(txtTelefone, lblErroTelefone, "Telefone obrigatório");
            valido = false;
        }

        if (txtEmail.getText().trim().isEmpty()) {
            mostrarErro(txtEmail, lblErroEmail, "E-mail obrigatório");
            valido = false;
        }

        if (txtEndereco.getText().trim().isEmpty()) {
            mostrarErro(txtEndereco, lblErroEndereco, "Endereço obrigatório");
            valido = false;
        }

        return valido;
    }

    private void mostrarErro(Control campo, Label label, String msg) {
        campo.getStyleClass().add("error");
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void limparErro(Control campo, Label label) {
        campo.getStyleClass().remove("error");
        label.setVisible(false);
        label.setManaged(false);
    }

    private void limparTodosErros() {
        limparErro(txtNome, lblErroNome);
        limparErro(txtTelefone, lblErroTelefone);
        limparErro(txtEmail, lblErroEmail);
        limparErro(txtEndereco, lblErroEndereco);
    }

    private void mostrarSucesso(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
