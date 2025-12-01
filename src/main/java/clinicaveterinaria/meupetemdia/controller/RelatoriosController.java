package clinicaveterinaria.meupetemdia.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import clinicaveterinaria.meupetemdia.dao.*;
import clinicaveterinaria.meupetemdia.model.*;
import clinicaveterinaria.meupetemdia.util.NavigationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Controller da tela de Relatórios e Estatísticas
 */
public class RelatoriosController {

    // ========== COMPONENTES ==========
    @FXML
    private Label lblTotalPets;
    @FXML
    private Label lblTotalDonos;
    @FXML
    private Label lblTotalConsultas;
    @FXML
    private Label lblTotalVacinas;

    @FXML
    private PieChart chartVacinas;
    @FXML
    private BarChart<String, Number> chartConsultas;

    // ========== DAOs ==========
    private DonoDAO donoDAO;
    private PetDAO petDAO;
    private ConsultaDAO consultaDAO;
    private RegistroVacinaDAO registroVacinaDAO;

    // ========== INICIALIZAÇÃO ==========
    @FXML
    private void initialize() {
        donoDAO = new DonoDAO();
        petDAO = new PetDAO();
        consultaDAO = new ConsultaDAO();
        registroVacinaDAO = new RegistroVacinaDAO();

        carregarDados();
    }

    /**
     * Carrega todos os dados e atualiza gráficos
     */
    private void carregarDados() {
        try {
            carregarEstatisticasGerais();
            carregarGraficoVacinas();
            carregarGraficoConsultas();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao carregar dados dos relatórios: " + e.getMessage());
        }
    }

    /**
     * Carrega estatísticas gerais (cards)
     */
    private void carregarEstatisticasGerais() {
        try {
            int totalPets = petDAO.findAll().size();
            int totalDonos = donoDAO.findAll().size();
            int totalConsultas = consultaDAO.findAll().size();
            int totalVacinas = registroVacinaDAO.findAll().size();

            lblTotalPets.setText(String.valueOf(totalPets));
            lblTotalDonos.setText(String.valueOf(totalDonos));
            lblTotalConsultas.setText(String.valueOf(totalConsultas));
            lblTotalVacinas.setText(String.valueOf(totalVacinas));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao carregar estatísticas gerais: " + e.getMessage());
        }
    }

    /**
     * Carrega gráfico de pizza - Status das vacinas
     */
    private void carregarGraficoVacinas() {
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

            // Contar status
            int emDia = 0;
            int proximas = 0;
            int vencidas = 0;
            LocalDate hoje = LocalDate.now();
            LocalDate limite30Dias = hoje.plusDays(30);

            for (RegistroVacina reg : ultimasDoses.values()) {
                if (reg.getDataProximaDose() != null) {
                    if (reg.getDataProximaDose().isBefore(hoje)) {
                        vencidas++;
                    } else if (reg.getDataProximaDose().isBefore(limite30Dias)) {
                        proximas++;
                    } else {
                        emDia++;
                    }
                }
            }

            // Criar dados do gráfico
            chartVacinas.getData().clear();

            if (vencidas > 0) {
                PieChart.Data dataVencidas = new PieChart.Data(
                        "Vencidas (" + vencidas + ")", vencidas);
                chartVacinas.getData().add(dataVencidas);
            }

            if (proximas > 0) {
                PieChart.Data dataProximas = new PieChart.Data(
                        "Próximas 30 dias (" + proximas + ")", proximas);
                chartVacinas.getData().add(dataProximas);
            }

            if (emDia > 0) {
                PieChart.Data dataEmDia = new PieChart.Data(
                        "Em dia (" + emDia + ")", emDia);
                chartVacinas.getData().add(dataEmDia);
            }

            // Se não houver dados
            if (chartVacinas.getData().isEmpty()) {
                PieChart.Data dataSemDados = new PieChart.Data(
                        "Sem dados", 1);
                chartVacinas.getData().add(dataSemDados);
            }

            // Aplicar cores
            aplicarCoresGraficoPizza();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao carregar gráfico de vacinas: " + e.getMessage());
        }
    }

    /**
     * Aplica cores personalizadas ao gráfico de pizza
     */
    private void aplicarCoresGraficoPizza() {
        // Aguardar renderização
        chartVacinas.applyCss();
        chartVacinas.layout();

        for (PieChart.Data data : chartVacinas.getData()) {
            String label = data.getName().toLowerCase();
            String cor = "";

            if (label.contains("vencida")) {
                cor = "-fx-pie-color: #d32f2f;"; // Vermelho
            } else if (label.contains("próxima")) {
                cor = "-fx-pie-color: #f57c00;"; // Laranja
            } else if (label.contains("em dia")) {
                cor = "-fx-pie-color: #388e3c;"; // Verde
            } else {
                cor = "-fx-pie-color: #cccccc;"; // Cinza para "Sem dados"
            }

            data.getNode().setStyle(cor);
        }
    }

    /**
     * Carrega gráfico de barras - Consultas por mês
     */
    private void carregarGraficoConsultas() {
        try {
            List<Consulta> consultas = consultaDAO.findAll();

            // Obter últimos 6 meses
            LocalDate hoje = LocalDate.now();
            List<String> meses = new ArrayList<>();
            Map<String, Integer> consultasPorMes = new LinkedHashMap<>();

            DateTimeFormatter formatadorMes = DateTimeFormatter.ofPattern("MMM/yyyy",
                    new Locale("pt", "BR"));

            for (int i = 5; i >= 0; i--) {
                LocalDate mes = hoje.minusMonths(i);
                String mesFormatado = mes.format(formatadorMes);
                meses.add(mesFormatado);
                consultasPorMes.put(mesFormatado, 0);
            }

            // Contar consultas por mês
            for (Consulta consulta : consultas) {
                String mesConsulta = consulta.getDataConsulta().format(formatadorMes);
                if (consultasPorMes.containsKey(mesConsulta)) {
                    consultasPorMes.put(mesConsulta, consultasPorMes.get(mesConsulta) + 1);
                }
            }

            // Criar série do gráfico
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Consultas");

            for (Map.Entry<String, Integer> entry : consultasPorMes.entrySet()) {
                serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            chartConsultas.getData().clear();
            chartConsultas.getData().add(serie);
            chartConsultas.setLegendVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao carregar gráfico de consultas: " + e.getMessage());
        }
    }

    // ========== AÇÕES ==========

    @FXML
    private void handleVoltar() {
        NavigationUtil.navigateToMenu();
    }

    @FXML
    private void handleAtualizar() {
        carregarDados();
        System.out.println("✅ Dados atualizados!");
    }
}