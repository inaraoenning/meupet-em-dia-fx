package clinicaveterinaria.meupetemdia.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import clinicaveterinaria.meupetemdia.model.*;
import clinicaveterinaria.meupetemdia.dao.*;
import clinicaveterinaria.meupetemdia.util.NavigationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller da tela de Vacinas e Consultas
 */
public class CadastroVacinasConsultasController {

    // ========== COMPONENTES DO FORMULÁRIO ==========
    @FXML private RadioButton rbConsulta;
    @FXML private RadioButton rbVacina;
    @FXML private ToggleGroup toggleTipo;

    @FXML private ComboBox<Pet> cmbPet;
    @FXML private DatePicker dtpData;
    @FXML private TextField txtVeterinario;
    @FXML private ComboBox<String> cmbTipoConsulta;
    @FXML private ComboBox<Vacina> cmbVacina;
    @FXML private DatePicker dtpProximaDose;
    @FXML private TextArea txtObservacoes;

    @FXML private Label lblErroPet;
    @FXML private Label lblErroData;
    @FXML private Label lblErroTipoConsulta;
    @FXML private Label lblErroVacina;

    // Labels e containers específicos
    @FXML private Label lblTipoConsulta;
    @FXML private VBox vboxTipoConsulta;
    @FXML private Label lblVacina;
    @FXML private VBox vboxVacina;
    @FXML private Label lblProximaDose;
    @FXML private VBox vboxProximaDose;

    @FXML private Button btnSalvar;
    @FXML private Button btnLimpar;

    // ========== COMPONENTES DA TABELA ==========
    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private TextField txtBuscar;
    @FXML private TableView<RegistroAtendimento> tblRegistros;
    @FXML private TableColumn<RegistroAtendimento, Integer> colId;
    @FXML private TableColumn<RegistroAtendimento, String> colTipo;
    @FXML private TableColumn<RegistroAtendimento, String> colPet;
    @FXML private TableColumn<RegistroAtendimento, String> colData;
    @FXML private TableColumn<RegistroAtendimento, String> colDescricao;
    @FXML private TableColumn<RegistroAtendimento, String> colVeterinario;
    @FXML private TableColumn<RegistroAtendimento, String> colProximaDose;
    @FXML private TableColumn<RegistroAtendimento, String> colObservacoes;

    @FXML private Button btnExcluir;

    // ========== DADOS ==========
    private ObservableList<Pet> listaPets;
    private ObservableList<Vacina> listaVacinas;
    private ObservableList<RegistroAtendimento> listaRegistros;
    private FilteredList<RegistroAtendimento> registrosFiltrados;

    private PetDAO petDAO;
    private VacinaDAO vacinaDAO;
    private ConsultaDAO consultaDAO;
    private RegistroVacinaDAO registroVacinaDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ========== INICIALIZAÇÃO ==========
    @FXML
    private void initialize() {
        petDAO = new PetDAO();
//        vacinaDAO = new VacinaDAO();
        consultaDAO = new ConsultaDAO();
        registroVacinaDAO = new RegistroVacinaDAO();

        configurarToggleTipo();
        configurarComboBoxes();
        configurarTabela();
        configurarBusca();
        configurarListeners();
        carregarDados();
        atualizarCamposVisiveis();
    }

    /**
     * Configura o toggle entre Consulta e Vacina
     */
    private void configurarToggleTipo() {
        toggleTipo.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            atualizarCamposVisiveis();
            limparTodosErros();
        });
    }

    /**
     * Atualiza visibilidade dos campos baseado no tipo selecionado
     */
    private void atualizarCamposVisiveis() {
        boolean isConsulta = rbConsulta.isSelected();

        // Campos de consulta
        lblTipoConsulta.setVisible(isConsulta);
        lblTipoConsulta.setManaged(isConsulta);
        vboxTipoConsulta.setVisible(isConsulta);
        vboxTipoConsulta.setManaged(isConsulta);

        // Campos de vacina
        lblVacina.setVisible(!isConsulta);
        lblVacina.setManaged(!isConsulta);
        vboxVacina.setVisible(!isConsulta);
        vboxVacina.setManaged(!isConsulta);
        lblProximaDose.setVisible(!isConsulta);
        lblProximaDose.setManaged(!isConsulta);
        vboxProximaDose.setVisible(!isConsulta);
        vboxProximaDose.setManaged(!isConsulta);
    }

    /**
     * Configura os ComboBoxes
     */
    private void configurarComboBoxes() {
        // ComboBox de Pets
        cmbPet.setConverter(new StringConverter<Pet>() {
            @Override
            public String toString(Pet pet) {
                return pet == null ? "" : pet.getNome() + " (" + pet.getDonoNome() + ")";
            }

            @Override
            public Pet fromString(String string) {
                return null;
            }
        });

        // ComboBox de Tipos de Consulta
        cmbTipoConsulta.setItems(FXCollections.observableArrayList(
                "Rotina", "Emergência", "Retorno", "Cirurgia", "Exame", "Outro"
        ));

        // ComboBox de Vacinas
        cmbVacina.setConverter(new StringConverter<Vacina>() {
            @Override
            public String toString(Vacina vacina) {
                return vacina == null ? "" : vacina.getNome();
            }

            @Override
            public Vacina fromString(String string) {
                return null;
            }
        });

        // Listener para calcular próxima dose automaticamente
        cmbVacina.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dtpData.getValue() != null && newVal.getIntervaloDiasReforco() != null) {
                LocalDate proximaDose = dtpData.getValue().plusDays(newVal.getIntervaloDiasReforco());
                dtpProximaDose.setValue(proximaDose);
            }
        });

        dtpData.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && cmbVacina.getValue() != null &&
                    cmbVacina.getValue().getIntervaloDiasReforco() != null) {
                LocalDate proximaDose = newVal.plusDays(cmbVacina.getValue().getIntervaloDiasReforco());
                dtpProximaDose.setValue(proximaDose);
            }
        });

        // ComboBox de filtro
        cmbFiltroTipo.setItems(FXCollections.observableArrayList(
                "Todos", "Consultas", "Vacinas"
        ));
        cmbFiltroTipo.setValue("Todos");
    }

    /**
     * Configura a tabela
     */
    private void configurarTabela() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipo()));
        colPet.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPetNome()));
        colData.setCellValueFactory(data ->
                new SimpleStringProperty(dateFormatter.format(data.getValue().getData())));
        colDescricao.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescricao()));
        colVeterinario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVeterinario()));
        colProximaDose.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProximaDoseFormatada()));
        colObservacoes.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getObservacoes()));

        // Listener para seleção
        tblRegistros.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            btnExcluir.setDisable(newVal == null);
        });
    }

    /**
     * Configura busca e filtros
     */
    private void configurarBusca() {
        // Busca por texto
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        // Filtro por tipo
        cmbFiltroTipo.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (registrosFiltrados != null) {
            registrosFiltrados.setPredicate(registro -> {
                // Filtro de tipo
                String filtroTipo = cmbFiltroTipo.getValue();
                if (filtroTipo != null && !filtroTipo.equals("Todos")) {
                    if (filtroTipo.equals("Consultas") && !registro.getTipo().equals("Consulta")) {
                        return false;
                    }
                    if (filtroTipo.equals("Vacinas") && !registro.getTipo().equals("Vacina")) {
                        return false;
                    }
                }

                // Busca por texto
                String busca = txtBuscar.getText();
                if (busca == null || busca.isEmpty()) {
                    return true;
                }

                String buscaLower = busca.toLowerCase();
                return registro.getPetNome().toLowerCase().contains(buscaLower) ||
                        registro.getDescricao().toLowerCase().contains(buscaLower) ||
                        (registro.getVeterinario() != null &&
                                registro.getVeterinario().toLowerCase().contains(buscaLower));
            });
        }
    }

    /**
     * Configura listeners
     */
    private void configurarListeners() {
        cmbPet.valueProperty().addListener((obs, oldVal, newVal) ->
                limparErro(cmbPet, lblErroPet));
        dtpData.valueProperty().addListener((obs, oldVal, newVal) ->
                limparErro(dtpData, lblErroData));
        cmbTipoConsulta.valueProperty().addListener((obs, oldVal, newVal) ->
                limparErro(cmbTipoConsulta, lblErroTipoConsulta));
        cmbVacina.valueProperty().addListener((obs, oldVal, newVal) ->
                limparErro(cmbVacina, lblErroVacina));
    }

    /**
     * Carrega dados iniciais
     */
    private void carregarDados() {
        try {
            listaPets = FXCollections.observableArrayList(petDAO.findAll());
            cmbPet.setItems(listaPets);

            listaVacinas = FXCollections.observableArrayList(vacinaDAO.findAll());
            cmbVacina.setItems(listaVacinas);

            carregarRegistros();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar dados: " + e.getMessage());
        }
    }

    /**
     * Carrega registros (consultas + vacinas)
     */
    private void carregarRegistros() {
        try {
            listaRegistros = FXCollections.observableArrayList();

            // Carregar consultas
            List<Consulta> consultas = consultaDAO.findAll();
            for (Consulta c : consultas) {
                listaRegistros.add(new RegistroAtendimento(c));
            }

            // Carregar vacinas
            List<RegistroVacina> vacinas = registroVacinaDAO.findAll();
            for (RegistroVacina v : vacinas) {
                listaRegistros.add(new RegistroAtendimento(v));
            }

            // Ordenar por data decrescente
            listaRegistros.sort((r1, r2) -> r2.getData().compareTo(r1.getData()));

            registrosFiltrados = new FilteredList<>(listaRegistros, p -> true);
            tblRegistros.setItems(registrosFiltrados);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar registros: " + e.getMessage());
        }
    }

    // ========== AÇÕES ==========

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
            if (rbConsulta.isSelected()) {
                salvarConsulta();
            } else {
                salvarVacina();
            }

            carregarRegistros();
            limparFormulario();
            mostrarSucesso("Registro salvo com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar: " + e.getMessage());
        }
    }

    private void salvarConsulta() {
        Consulta consulta = new Consulta(
                cmbPet.getValue().getId(),
                dtpData.getValue(),
                cmbTipoConsulta.getValue(),
                txtVeterinario.getText().trim().isEmpty() ? null : txtVeterinario.getText().trim(),
                txtObservacoes.getText().trim().isEmpty() ? null : txtObservacoes.getText().trim()
        );

        int id = consultaDAO.insert(consulta);
        if (id <= 0) {
            throw new RuntimeException("Falha ao inserir consulta");
        }
    }

    private void salvarVacina() {
        RegistroVacina registro = new RegistroVacina(
                cmbPet.getValue().getId(),
                cmbVacina.getValue().getId(),
                dtpData.getValue(),
                dtpProximaDose.getValue(),
                txtVeterinario.getText().trim().isEmpty() ? null : txtVeterinario.getText().trim(),
                txtObservacoes.getText().trim().isEmpty() ? null : txtObservacoes.getText().trim()
        );

        int id = registroVacinaDAO.insert(registro);
        if (id <= 0) {
            throw new RuntimeException("Falha ao inserir registro de vacina");
        }
    }

    @FXML
    private void handleLimpar() {
        limparFormulario();
    }

    @FXML
    private void handleExcluir() {
        RegistroAtendimento selecionado = tblRegistros.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deseja realmente excluir este registro?");
        alert.setContentText(selecionado.getTipo() + ": " + selecionado.getPetNome() +
                "\nData: " + dateFormatter.format(selecionado.getData()));

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            excluirRegistro(selecionado);
        }
    }

    private void excluirRegistro(RegistroAtendimento registro) {
        try {
            boolean sucesso;
            if (registro.getTipo().equals("Consulta")) {
                sucesso = consultaDAO.delete(registro.getId());
            } else {
                sucesso = registroVacinaDAO.delete(registro.getId());
            }

            if (sucesso) {
                listaRegistros.remove(registro);
                mostrarSucesso("Registro excluído com sucesso!");
            } else {
                throw new RuntimeException("Falha ao excluir registro");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao excluir: " + e.getMessage());
        }
    }

    // ========== VALIDAÇÕES ==========

    private boolean validarCampos() {
        boolean valido = true;

        // Pet obrigatório
        if (cmbPet.getValue() == null) {
            mostrarErro(cmbPet, lblErroPet, "Selecione o pet");
            valido = false;
        }

        // Data obrigatória
        if (dtpData.getValue() == null) {
            mostrarErro(dtpData, lblErroData, "Selecione a data");
            valido = false;
        } else if (dtpData.getValue().isAfter(LocalDate.now())) {
            mostrarErro(dtpData, lblErroData, "A data não pode ser futura");
            valido = false;
        }

        // Validações específicas
        if (rbConsulta.isSelected()) {
            if (cmbTipoConsulta.getValue() == null) {
                mostrarErro(cmbTipoConsulta, lblErroTipoConsulta, "Selecione o tipo de consulta");
                valido = false;
            }
        } else {
            if (cmbVacina.getValue() == null) {
                mostrarErro(cmbVacina, lblErroVacina, "Selecione a vacina");
                valido = false;
            }
        }

        return valido;
    }

    // ========== AUXILIARES ==========

    private void limparFormulario() {
        cmbPet.setValue(null);
        dtpData.setValue(LocalDate.now());
        txtVeterinario.clear();
        cmbTipoConsulta.setValue(null);
        cmbVacina.setValue(null);
        dtpProximaDose.setValue(null);
        txtObservacoes.clear();
        tblRegistros.getSelectionModel().clearSelection();
        limparTodosErros();
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
        limparErro(cmbPet, lblErroPet);
        limparErro(dtpData, lblErroData);
        limparErro(cmbTipoConsulta, lblErroTipoConsulta);
        limparErro(cmbVacina, lblErroVacina);
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

    // ========== CLASSE INTERNA PARA UNIFICAR TABELA ==========

    /**
     * Classe auxiliar para unificar Consultas e Vacinas na mesma tabela
     */
    public static class RegistroAtendimento {
        private int id;
        private String tipo;
        private String petNome;
        private LocalDate data;
        private String descricao;
        private String veterinario;
        private LocalDate proximaDose;
        private String observacoes;
        private Object registroOriginal; // Consulta ou RegistroVacina

        public RegistroAtendimento(Consulta consulta) {
            this.id = consulta.getId();
            this.tipo = "Consulta";
            this.petNome = consulta.getPetNome();
            this.data = consulta.getDataConsulta();
            this.descricao = consulta.getTipo();
            this.veterinario = consulta.getVeterinario();
            this.observacoes = consulta.getObservacoes();
            this.registroOriginal = consulta;
        }

        public RegistroAtendimento(RegistroVacina vacina) {
            this.id = vacina.getId();
            this.tipo = "Vacina";
            this.petNome = vacina.getPetNome();
            this.data = vacina.getDataAplicacao();
            this.descricao = vacina.getVacinaNome();
            this.veterinario = vacina.getVeterinario();
            this.proximaDose = vacina.getDataProximaDose();
            this.observacoes = vacina.getObservacoes();
            this.registroOriginal = vacina;
        }

        public String getProximaDoseFormatada() {
            if (proximaDose == null) return "";
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(proximaDose);
        }

        // Getters
        public int getId() { return id; }
        public String getTipo() { return tipo; }
        public String getPetNome() { return petNome; }
        public LocalDate getData() { return data; }
        public String getDescricao() { return descricao; }
        public String getVeterinario() { return veterinario != null ? veterinario : ""; }
        public String getObservacoes() { return observacoes != null ? observacoes : ""; }
    }
}