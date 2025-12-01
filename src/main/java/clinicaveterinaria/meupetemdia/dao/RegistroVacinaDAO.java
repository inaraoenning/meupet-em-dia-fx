package clinicaveterinaria.meupetemdia.dao;

import clinicaveterinaria.meupetemdia.config.DatabaseConfig;
import clinicaveterinaria.meupetemdia.model.RegistroVacina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// DAO para operações com Registros de Vacinas (aplicações)

public class RegistroVacinaDAO {

    public int insert(RegistroVacina registro) {
        String sql = "INSERT INTO registros_vacinas (pet_id, vacina_id, data_aplicacao, " +
                "data_proxima_dose, veterinario, observacoes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, registro.getPetId());
            pstmt.setInt(2, registro.getVacinaId());
            pstmt.setDate(3, Date.valueOf(registro.getDataAplicacao()));
            pstmt.setDate(4, registro.getDataProximaDose() != null ?
                    Date.valueOf(registro.getDataProximaDose()) : null);
            pstmt.setString(5, registro.getVeterinario());
            pstmt.setString(6, registro.getObservacoes());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir registro de vacina: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM registros_vacinas WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir registro de vacina: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<RegistroVacina> findAll() {
        List<RegistroVacina> registros = new ArrayList<>();
        String sql = "SELECT rv.*, p.nome AS pet_nome, v.nome AS vacina_nome, " +
                "d.nome AS dono_nome, d.telefone AS dono_telefone " +
                "FROM registros_vacinas rv " +
                "INNER JOIN pets p ON rv.pet_id = p.id " +
                "INNER JOIN vacinas v ON rv.vacina_id = v.id " +
                "INNER JOIN donos d ON p.dono_id = d.id " +
                "ORDER BY rv.data_aplicacao DESC";


        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                registros.add(resultSetToRegistroVacina(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar registros de vacinas: " + e.getMessage());
            e.printStackTrace();
        }
        return registros;
    }

    public List<RegistroVacina> findByPet(int petId) {
        List<RegistroVacina> registros = new ArrayList<>();
        String sql = "SELECT rv.*, p.nome as pet_nome, v.nome as vacina_nome " +
                "FROM registros_vacinas rv " +
                "INNER JOIN pets p ON rv.pet_id = p.id " +
                "INNER JOIN vacinas v ON rv.vacina_id = v.id " +
                "WHERE rv.pet_id = ? ORDER BY rv.data_aplicacao DESC";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, petId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                registros.add(resultSetToRegistroVacina(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar registros de vacina do pet: " + e.getMessage());
            e.printStackTrace();
        }
        return registros;
    }

    /**
     * Busca próximas vacinas (que vencerão nos próximos 30 dias ou já venceram)
     */
    public List<RegistroVacina> findProximasVacinas() {
        List<RegistroVacina> registros = new ArrayList<>();
        String sql = "SELECT rv.*, p.nome as pet_nome, v.nome as vacina_nome " +
                "FROM registros_vacinas rv " +
                "INNER JOIN pets p ON rv.pet_id = p.id " +
                "INNER JOIN vacinas v ON rv.vacina_id = v.id " +
                "WHERE rv.data_proxima_dose IS NOT NULL " +
                "AND rv.data_proxima_dose <= date('now', '+30 days') " +
                "ORDER BY rv.data_proxima_dose ASC";

        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                registros.add(resultSetToRegistroVacina(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar próximas vacinas: " + e.getMessage());
            e.printStackTrace();
        }
        return registros;
    }

    private RegistroVacina resultSetToRegistroVacina(ResultSet rs) throws SQLException {
        RegistroVacina registro = new RegistroVacina();
        registro.setId(rs.getInt("id"));
        registro.setPetId(rs.getInt("pet_id"));
        registro.setPetNome(rs.getString("pet_nome"));
        registro.setDonoNome(rs.getString("dono_nome"));
        registro.setTelefone(rs.getString("dono_telefone"));
        registro.setVacinaId(rs.getInt("vacina_id"));
        registro.setVacinaNome(rs.getString("vacina_nome"));
        registro.setDataAplicacao(rs.getDate("data_aplicacao").toLocalDate());

        Date dataProxima = rs.getDate("data_proxima_dose");
        if (dataProxima != null) {
            registro.setDataProximaDose(dataProxima.toLocalDate());
        }

        registro.setVeterinario(rs.getString("veterinario"));
        registro.setObservacoes(rs.getString("observacoes"));
        return registro;
    }
}