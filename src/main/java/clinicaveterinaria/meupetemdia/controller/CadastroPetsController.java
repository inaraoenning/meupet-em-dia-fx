package clinicaveterinaria.meupetemdia.controller;

import clinicaveterinaria.meupetemdia.dao.DonoDAO;
import clinicaveterinaria.meupetemdia.dao.PetDAO;
import clinicaveterinaria.meupetemdia.model.Dono;
import clinicaveterinaria.meupetemdia.model.Pet;
import clinicaveterinaria.meupetemdia.util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


// Controller da tela de Cadastro de Pets
public class CadastroPetsController {

    // ========== COMPONENTES DO FORMULÁRIO ==========
    @FXML
    private TextField txtNome;
    @FXML
    private ComboBox<String> cmbEspecie;
    @FXML
    private TextField txtRaca;
    @FXML
    private DatePicker dtpDataNascimento;
    @FXML
    private ComboBox<Dono> cmbDono;

    @FXML
    private Label lblErroNome;
    @FXML
    private Label lblErroEspecie;
    @FXML
    private Label lblErroData;
    @FXML
    private Label lblErroDono;

    @FXML
    private Button btnSalvar;
    @FXML
    private Button btnLimpar;
    @FXML
    private Button btnCancelar;

    // ========== COMPONENTES DA TABELA ==========
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Pet> tblPets;
    @FXML
    private TableColumn<Pet, Integer> colId;
    @FXML
    private TableColumn<Pet, String> colNome;
    @FXML
    private TableColumn<Pet, String> colEspecie;
    @FXML
    private TableColumn<Pet, String> colRaca;
    @FXML
    private TableColumn<Pet, LocalDate> colDataNascimento;
    @FXML
    private TableColumn<Pet, String> colDono;

    @FXML
    private Button btnEditar;
    @FXML
    private Button btnExcluir;

    // ========== DADOS ==========
    private ObservableList<Pet> listaPets;
    private FilteredList<Pet> petsFiltrados;
    private ObservableList<Dono> listaDonos;
    private Pet petEmEdicao;
    private boolean modoEdicao = false;

    private PetDAO petDAO;
    private DonoDAO donoDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ========== INICIALIZAÇÃO ==========
    @FXML
    private void initialize() {
        petDAO = new PetDAO();
        donoDAO = new DonoDAO();

        configurarTabela();
        configurarComboBoxes();
        configurarBusca();
        configurarListeners();
        carregarDonos();
        carregarPets();
    }


    // Configura as colunas da tabela
    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaca.setCellValueFactory(new PropertyValueFactory<>("raca"));
        colDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colDono.setCellValueFactory(new PropertyValueFactory<>("donoNome"));

        // Formatar data na tabela
        colDataNascimento.setCellFactory(column -> new TableCell<Pet, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(item));
                }
            }
        });

        // Listener para seleção na tabela
        tblPets.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean itemSelecionado = newVal != null;
            btnEditar.setDisable(!itemSelecionado);
            btnExcluir.setDisable(!itemSelecionado);
        });
    }


    // Configura os ComboBoxes
    private void configurarComboBoxes() {
        // ComboBox de Espécies
        cmbEspecie.setItems(FXCollections.observableArrayList(
                "Cachorro", "Gato", "Pássaro", "Coelho", "Hamster", "Peixe", "Outro"
        ));

        // ComboBox de Donos - Configurar StringConverter
        cmbDono.setConverter(new StringConverter<Dono>() {
            @Override
            public String toString(Dono dono) {
                return dono == null ? "" : dono.getNome();
            }

            @Override
            public Dono fromString(String string) {
                return cmbDono.getItems().stream()
                        .filter(dono -> dono.getNome().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }


    // Configura a busca em tempo real
    private void configurarBusca() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (petsFiltrados != null) {
                petsFiltrados.setPredicate(pet -> {
                    if (newVal == null || newVal.isEmpty()) {
                        return true;
                    }
                    String busca = newVal.toLowerCase();
                    return pet.getNome().toLowerCase().contains(busca) ||
                            pet.getEspecie().toLowerCase().contains(busca) ||
                            (pet.getRaca() != null && pet.getRaca().toLowerCase().contains(busca)) ||
                            pet.getDonoNome().toLowerCase().contains(busca);
                });
            }
        });
    }


    // Configura listeners para limpar erros
    private void configurarListeners() {
        txtNome.textProperty().addListener((obs, oldVal, newVal) ->
                limparErro(txtNome, lblErroNome));
        cmbEspecie.valueProperty().addListener((obs, oldVal, newVal) ->
                limparErro(cmbEspecie, lblErroEspecie));
        dtpDataNascimento.valueProperty().addListener((obs, oldVal, newVal) ->
                limparErro(dtpDataNascimento, lblErroData));
        cmbDono.valueProperty().addListener((obs, oldVal, newVal) ->
                limparErro(cmbDono, lblErroDono));
    }


    // Carrega donos para o ComboBox
    private void carregarDonos() {
        try {
            listaDonos = FXCollections.observableArrayList(donoDAO.findAll());
            cmbDono.setItems(listaDonos);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar donos: " + e.getMessage());
        }
    }


    // Carrega pets do banco
    private void carregarPets() {
        try {
            listaPets = FXCollections.observableArrayList(petDAO.findAll());
            petsFiltrados = new FilteredList<>(listaPets, p -> true);
            tblPets.setItems(petsFiltrados);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar pets: " + e.getMessage());
            listaPets = FXCollections.observableArrayList();
            petsFiltrados = new FilteredList<>(listaPets, p -> true);
            tblPets.setItems(petsFiltrados);
        }
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
                atualizarPet();
            } else {
                inserirPet();
            }

            carregarPets();
            limparFormulario();
            mostrarSucesso(modoEdicao ? "Pet atualizado com sucesso!" : "Pet cadastrado com sucesso!");

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
        Pet petSelecionado = tblPets.getSelectionModel().getSelectedItem();
        if (petSelecionado != null) {
            preencherFormulario(petSelecionado);
            ativarModoEdicao();
        }
    }

    @FXML
    private void handleExcluir() {
        Pet petSelecionado = tblPets.getSelectionModel().getSelectedItem();
        if (petSelecionado == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deseja realmente excluir este pet?");
        alert.setContentText("Nome: " + petSelecionado.getNome() + "\n" +
                "Dono: " + petSelecionado.getDonoNome() + "\n" +
                "Esta ação não poderá ser desfeita.");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            excluirPet(petSelecionado);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private boolean validarCampos() {
        boolean valido = true;

        // Validar nome
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            mostrarErro(txtNome, lblErroNome, "O nome do pet é obrigatório");
            valido = false;
        } else if (nome.length() < 2) {
            mostrarErro(txtNome, lblErroNome, "O nome deve ter pelo menos 2 caracteres");
            valido = false;
        }

        // Validar espécie
        if (cmbEspecie.getValue() == null) {
            mostrarErro(cmbEspecie, lblErroEspecie, "Selecione uma espécie");
            valido = false;
        }

        // Validar data de nascimento
        LocalDate dataNasc = dtpDataNascimento.getValue();
        if (dataNasc != null && dataNasc.isAfter(LocalDate.now())) {
            mostrarErro(dtpDataNascimento, lblErroData, "A data não pode ser futura");
            valido = false;
        }

        // Validar dono
        if (cmbDono.getValue() == null) {
            mostrarErro(cmbDono, lblErroDono, "Selecione o proprietário");
            valido = false;
        }

        return valido;
    }

    private void inserirPet() {
        Pet novoPet = new Pet(
                txtNome.getText().trim(),
                cmbEspecie.getValue(),
                txtRaca.getText().trim().isEmpty() ? null : txtRaca.getText().trim(),
                dtpDataNascimento.getValue(),
                cmbDono.getValue().getId()
        );

        int id = petDAO.insert(novoPet);
        if (id > 0) {
            novoPet.setId(id);
            novoPet.setDonoNome(cmbDono.getValue().getNome());
            listaPets.add(novoPet);
        } else {
            throw new RuntimeException("Falha ao inserir pet no banco");
        }
    }

    private void atualizarPet() {
        petEmEdicao.setNome(txtNome.getText().trim());
        petEmEdicao.setEspecie(cmbEspecie.getValue());
        petEmEdicao.setRaca(txtRaca.getText().trim().isEmpty() ? null : txtRaca.getText().trim());
        petEmEdicao.setDataNascimento(dtpDataNascimento.getValue());
        petEmEdicao.setDonoId(cmbDono.getValue().getId());
        petEmEdicao.setDonoNome(cmbDono.getValue().getNome());

        boolean sucesso = petDAO.update(petEmEdicao);
        if (!sucesso) {
            throw new RuntimeException("Falha ao atualizar pet no banco");
        }

        tblPets.refresh();
    }

    private void excluirPet(Pet pet) {
        try {
            boolean sucesso = petDAO.delete(pet.getId());
            if (sucesso) {
                listaPets.remove(pet);
                mostrarSucesso("Pet excluído com sucesso!");
            } else {
                throw new RuntimeException("Falha ao excluir pet do banco");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao excluir pet: " + e.getMessage());
        }
    }

    private void preencherFormulario(Pet pet) {
        petEmEdicao = pet;
        txtNome.setText(pet.getNome());
        cmbEspecie.setValue(pet.getEspecie());
        txtRaca.setText(pet.getRaca());
        dtpDataNascimento.setValue(pet.getDataNascimento());

        // Selecionar dono no ComboBox
        for (Dono dono : cmbDono.getItems()) {
            if (dono.getId().equals(pet.getDonoId())) {
                cmbDono.setValue(dono);
                break;
            }
        }
    }

    private void limparFormulario() {
        txtNome.clear();
        cmbEspecie.setValue(null);
        txtRaca.clear();
        dtpDataNascimento.setValue(null);
        cmbDono.setValue(null);
        txtBuscar.clear();
        tblPets.getSelectionModel().clearSelection();
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
        petEmEdicao = null;
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
        limparErro(cmbEspecie, lblErroEspecie);
        limparErro(dtpDataNascimento, lblErroData);
        limparErro(cmbDono, lblErroDono);
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