package clinicaveterinaria.meupetemdia.dao;

import clinicaveterinaria.meupetemdia.config.DatabaseConfig;
import clinicaveterinaria.meupetemdia.model.Vacina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


 // DAO para operações com Vacinas (tipos de vacinas disponíveis)

public class VacinaDAO {

    public int insert(Vacina vacina) {
        String sql = "INSERT INTO vacinas (nome, descricao, intervalo_dias_reforco) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, vacina.getNome());
            pstmt.setString(2, vacina.getDescricao());
            pstmt.setObject(3, vacina.getIntervaloDiasReforco());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir vacina: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Vacina> findAll() {
        List<Vacina> vacinas = new ArrayList<>();
        String sql = "SELECT * FROM vacinas ORDER BY nome";

        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vacinas.add(resultSetToVacina(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar vacinas: " + e.getMessage());
            e.printStackTrace();
        }
        return vacinas;
    }

    public Vacina findById(int id) {
        String sql = "SELECT * FROM vacinas WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToVacina(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar vacina: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    //Cria vacinas padrão
    public void criarVacinasPadrao() {
        List<Vacina> vacinas = findAll();
        if (vacinas.isEmpty()) {

            // Vacinas para cães
            insert(new Vacina("V8 (Cães)", "Vacina óctupla para cães", 365));
            insert(new Vacina("V10 (Cães)", "Vacina déctupla para cães", 365));
            insert(new Vacina("Antirrábica (Cães)", "Proteção contra raiva", 365));
            insert(new Vacina("Giárdia (Cães)", "Contra giardíase", 365));
            insert(new Vacina("Gripe Canina", "Tosse dos canis", 180));
            insert(new Vacina("Leishmaniose", "Prevenção da leishmaniose (Leish-Tec)", 365));
            insert(new Vacina("Lyme", "Proteção contra Borrelia burgdorferi", 365));
            insert(new Vacina("Coronavírus Canino", "Proteção contra coronavírus canino", 365));
            insert(new Vacina("Parainfluenza", "Proteção contra Parainfluenza tipo 3", 365));
            insert(new Vacina("Hepatite Infecciosa", "Proteção contra hepatite canina", 365));

            // Vacinas para gatos
            insert(new Vacina("V3 (Gatos)", "Rinotraqueíte, calicivirose e panleucopenia", 365));
            insert(new Vacina("V4 (Gatos)", "V3 + clamidiose", 365));
            insert(new Vacina("V5 (Gatos)", "V4 + FeLV", 365));
            insert(new Vacina("FeLV (Gatos)", "Proteção contra leucemia felina", 365));
            insert(new Vacina("Antirrábica (Gatos)", "Proteção contra raiva", 365));
            insert(new Vacina("FIV (Gatos)", "Contra imunodeficiência felina", 365));
            System.out.println("✅ Vacinas padrão criadas");
        }
    }

    private Vacina resultSetToVacina(ResultSet rs) throws SQLException {
        Vacina vacina = new Vacina();
        vacina.setId(rs.getInt("id"));
        vacina.setNome(rs.getString("nome"));
        vacina.setDescricao(rs.getString("descricao"));

        Object intervalo = rs.getObject("intervalo_dias_reforco");
        vacina.setIntervaloDiasReforco(intervalo != null ? (Integer) intervalo : null);

        return vacina;
    }
}