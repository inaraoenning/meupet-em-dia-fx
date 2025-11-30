package clinicaveterinaria.meupetemdia.dao;

import clinicaveterinaria.meupetemdia.config.DatabaseConfig;
import clinicaveterinaria.meupetemdia.model.Consulta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com Consultas
 */
public class ConsultaDAO {

    public int insert(Consulta consulta) {
        String sql = "INSERT INTO consultas (pet_id, data_consulta, tipo, veterinario, observacoes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, consulta.getPetId());
            pstmt.setDate(2, Date.valueOf(consulta.getDataConsulta()));
            pstmt.setString(3, consulta.getTipo());
            pstmt.setString(4, consulta.getVeterinario());
            pstmt.setString(5, consulta.getObservacoes());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir consulta: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM consultas WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir consulta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Consulta> findAll() {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pet_nome FROM consultas c " +
                "INNER JOIN pets p ON c.pet_id = p.id ORDER BY c.data_consulta DESC";

        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                consultas.add(resultSetToConsulta(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar consultas: " + e.getMessage());
            e.printStackTrace();
        }
        return consultas;
    }

    public List<Consulta> findByPet(int petId) {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pet_nome FROM consultas c " +
                "INNER JOIN pets p ON c.pet_id = p.id " +
                "WHERE c.pet_id = ? ORDER BY c.data_consulta DESC";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, petId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                consultas.add(resultSetToConsulta(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar consultas do pet: " + e.getMessage());
            e.printStackTrace();
        }
        return consultas;
    }

    private Consulta resultSetToConsulta(ResultSet rs) throws SQLException {
        Consulta consulta = new Consulta();
        consulta.setId(rs.getInt("id"));
        consulta.setPetId(rs.getInt("pet_id"));
        consulta.setPetNome(rs.getString("pet_nome"));
        consulta.setDataConsulta(rs.getDate("data_consulta").toLocalDate());
        consulta.setTipo(rs.getString("tipo"));
        consulta.setVeterinario(rs.getString("veterinario"));
        consulta.setObservacoes(rs.getString("observacoes"));
        return consulta;
    }
}