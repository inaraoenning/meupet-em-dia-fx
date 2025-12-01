package clinicaveterinaria.meupetemdia.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import clinicaveterinaria.meupetemdia.model.Pet;
import clinicaveterinaria.meupetemdia.model.RegistroVacina;
import clinicaveterinaria.meupetemdia.dao.PetDAO;
import clinicaveterinaria.meupetemdia.dao.RegistroVacinaDAO;
import clinicaveterinaria.meupetemdia.util.NavigationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller da tela de Próximas Vacinas
 */
public class ProximasVacinasController {

    // ========== COMPONENTES ==========
    @FXML private ComboBox<String> cmbFiltroStatus;
    @FXML private ComboBox<Pet> cmbFiltroPet;
    @FXML private Button btnAtualizar;

    @FXML private TableView<VacinaInfo> tblProximasVacinas;
    @FXML private TableColumn<VacinaInfo, String> colStatus;
    @FXML private TableColumn<VacinaInfo, String> colPet;
    @FXML private TableColumn<VacinaInfo, String> colDono;
    @FXML private TableColumn<VacinaInfo, String> colVacina;
    @FXML private TableColumn<VacinaInfo, String> colUltimaDose;
    @FXML private TableColumn<VacinaInfo, String> colProximaDose;
    @FXML private TableColumn<VacinaInfo, String> colDiasRestantes;

    @FXML private Label lblTotal;
    @FXML private Label lblVencidas;
    @FXML private Label lblProximas;

    // ========== DADOS ==========
    private ObservableList<VacinaInfo> listaVacinas;
    private FilteredList<VacinaInfo> vacinasFiltradas;
    private ObservableList<Pet> listaPets;

    private RegistroVacinaDAO registroVacinaDAO;
    private PetDAO petDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ========== INICIALIZAÇÃO ==========
    @FXML
    private void initialize() {
        registroVacinaDAO = new RegistroVacinaDAO();
        petDAO = new PetDAO();

        configurarComboBoxes();
        configurarTabela();
        configurarFiltros();
        carregarDados();
    }

    /**
     * Configura os ComboBoxes
     */
    private void configurarComboBoxes() {
        // ComboBox de Status
        cmbFiltroStatus.setItems(FXCollections.observableArrayList(
                "Todos", "Vencidas", "Próximas (30 dias)", "Em dia"
        ));
        cmbFiltroStatus.setValue("Todos");

        // ComboBox de Pets
        cmbFiltroPet.setConverter(new StringConverter<Pet>() {
            @Override
            public String toString(Pet pet) {
                return pet == null ? "Todos os pets" : pet.getNome() + " (" + pet.getDonoNome() + ")";
            }

            @Override
            public Pet fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Configura a tabela
     */
    private void configurarTabela() {
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatusTexto()));
        colPet.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPetNome()));
        colDono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDonoNome()));
        colVacina.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVacinaNome()));
        colUltimaDose.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUltimaDoseFormatada()));
        colProximaDose.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProximaDoseFormatada()));
        colDiasRestantes.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDiasRestantesTexto()));

        // Estilizar linhas baseado no status
        tblProximasVacinas.setRowFactory(tv -> new TableRow<VacinaInfo>() {
            @Override
            protected void updateItem(VacinaInfo item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else {
                    String corFundo = "";
                    switch (item.getStatus()) {
                        case VENCIDA:
                            corFundo = "-fx-background-color: #ffebee;"; // Vermelho claro
                            break;
                        case PROXIMA:
                            corFundo = "-fx-background-color: #fff3e0;"; // Laranja claro
                            break;
                        case EM_DIA:
                            corFundo = "-fx-background-color: #e8f5e9;"; // Verde claro
                            break;
                    }
                    setStyle(corFundo);
                }
            }
        });

        // Estilizar coluna de status
        colStatus.setCellFactory(column -> new TableCell<VacinaInfo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    VacinaInfo vacina = getTableView().getItems().get(getIndex());
                    String cor = "";
                    switch (vacina.getStatus()) {
                        case VENCIDA:
                            cor = "-fx-text-fill: #d32f2f; -fx-font-weight: bold;";
                            break;
                        case PROXIMA:
                            cor = "-fx-text-fill: #f57c00; -fx-font-weight: bold;";
                            break;
                        case EM_DIA:
                            cor = "-fx-text-fill: #388e3c; -fx-font-weight: bold;";
                            break;
                    }
                    setStyle(cor);
                }
            }
        });
    }

    /**
     * Configura filtros
     */
    private void configurarFiltros() {
        cmbFiltroStatus.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cmbFiltroPet.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (vacinasFiltradas != null) {
            vacinasFiltradas.setPredicate(vacina -> {
                // Filtro de status
                String filtroStatus = cmbFiltroStatus.getValue();
                if (filtroStatus != null && !filtroStatus.equals("Todos")) {
                    switch (filtroStatus) {
                        case "Vencidas":
                            if (vacina.getStatus() != StatusVacina.VENCIDA) return false;
                            break;
                        case "Próximas (30 dias)":
                            if (vacina.getStatus() != StatusVacina.PROXIMA) return false;
                            break;
                        case "Em dia":
                            if (vacina.getStatus() != StatusVacina.EM_DIA) return false;
                            break;
                    }
                }

                // Filtro de pet
                Pet filtroPet = cmbFiltroPet.getValue();
                if (filtroPet != null) {
                    if (!vacina.getPetNome().equals(filtroPet.getNome())) {
                        return false;
                    }
                }

                return true;
            });

            atualizarContadores();
        }
    }

    /**
     * Carrega dados
     */
    private void carregarDados() {
        try {
            // Carregar pets para o filtro
            listaPets = FXCollections.observableArrayList(petDAO.findAll());
            cmbFiltroPet.setItems(listaPets);

            // Carregar vacinas
            carregarVacinas();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar dados: " + e.getMessage());
        }
    }

    /**
     * Carrega vacinas e calcula status
     */
    private void carregarVacinas() {
        try {
            List<RegistroVacina> registros = registroVacinaDAO.findAll();

            // Agrupar por pet + vacina para pegar apenas a última dose
            Map<String, RegistroVacina> ultimasDoses = new HashMap<>();
            for (RegistroVacina reg : registros) {
                String chave = reg.getPetId() + "-" + reg.getVacinaId();
                RegistroVacina existente = ultimasDoses.get(chave);

                if (existente == null || reg.getDataAplicacao().isAfter(existente.getDataAplicacao())) {
                    ultimasDoses.put(chave, reg);
                }
            }

            // Converter para VacinaInfo
            listaVacinas = FXCollections.observableArrayList();
            LocalDate hoje = LocalDate.now();

            for (RegistroVacina reg : ultimasDoses.values()) {
                if (reg.getDataProximaDose() != null) {
                    VacinaInfo info = new VacinaInfo(reg, hoje);
                    listaVacinas.add(info);
                }
            }

            // Ordenar por data próxima dose
            listaVacinas.sort((v1, v2) -> v1.getRegistro().getDataProximaDose()
                    .compareTo(v2.getRegistro().getDataProximaDose()));

            vacinasFiltradas = new FilteredList<>(listaVacinas, p -> true);
            tblProximasVacinas.setItems(vacinasFiltradas);

            atualizarContadores();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar vacinas: " + e.getMessage());
        }
    }

    /**
     * Atualiza contadores do dashboard
     */
    private void atualizarContadores() {
        int total = vacinasFiltradas.size();
        int vencidas = 0;
        int proximas = 0;

        for (VacinaInfo vacina : vacinasFiltradas) {
            switch (vacina.getStatus()) {
                case VENCIDA:
                    vencidas++;
                    break;
                case PROXIMA:
                    proximas++;
                    break;
            }
        }

        lblTotal.setText(String.valueOf(total));
        lblVencidas.setText(String.valueOf(vencidas));
        lblProximas.setText(String.valueOf(proximas));
    }

    // ========== AÇÕES ==========

    @FXML
    private void handleVoltar() {
        NavigationUtil.navigateToMenu();
    }

    @FXML
    private void handleAtualizar() {
        carregarVacinas();
        mostrarSucesso("Dados atualizados!");
    }

    // ========== AUXILIARES ==========

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

    // ========== CLASSES AUXILIARES ==========

    /**
     * Enum para status da vacina
     */
    public enum StatusVacina {
        VENCIDA, PROXIMA, EM_DIA
    }

    /**
     * Classe para encapsular informações da vacina com status calculado
     */
    public static class VacinaInfo {
        private final RegistroVacina registro;
        private final StatusVacina status;
        private final long diasRestantes;
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        public VacinaInfo(RegistroVacina registro, LocalDate hoje) {
            this.registro = registro;
            this.diasRestantes = ChronoUnit.DAYS.between(hoje, registro.getDataProximaDose());

            if (registro.getDataProximaDose().isBefore(hoje)) {
                this.status = StatusVacina.VENCIDA;
            } else if (diasRestantes <= 30) {
                this.status = StatusVacina.PROXIMA;
            } else {
                this.status = StatusVacina.EM_DIA;
            }
        }

        public RegistroVacina getRegistro() {
            return registro;
        }

        public StatusVacina getStatus() {
            return status;
        }

        public String getStatusTexto() {
            switch (status) {
                case VENCIDA: return "❌ Vencida";
                case PROXIMA: return "⚠️ Próxima";
                case EM_DIA: return "✅ Em dia";
                default: return "";
            }
        }

        public String getPetNome() {
            return registro.getPetNome();
        }

        public String getDonoNome() {
            return registro.getDonoNome() != null ? registro.getDonoNome() : "";
        }

        public String getVacinaNome() {
            return registro.getVacinaNome();
        }

        public String getUltimaDoseFormatada() {
            return dateFormatter.format(registro.getDataAplicacao());
        }

        public String getProximaDoseFormatada() {
            return dateFormatter.format(registro.getDataProximaDose());
        }

        public String getDiasRestantesTexto() {
            if (diasRestantes < 0) {
                return Math.abs(diasRestantes) + " dias atrás";
            } else if (diasRestantes == 0) {
                return "Hoje";
            } else {
                return diasRestantes + " dias";
            }
        }
    }
}