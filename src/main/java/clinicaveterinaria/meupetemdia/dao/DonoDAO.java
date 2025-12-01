package clinicaveterinaria.meupetemdia.dao;

import clinicaveterinaria.meupetemdia.config.DatabaseConfig;
import clinicaveterinaria.meupetemdia.model.Dono;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// DAO para operações CRUD de Donos no SQLite

public class DonoDAO {

    /**
     * Insere um novo dono no banco
     *
     * @param dono Dono a ser inserido
     * @return ID gerado ou -1 em caso de erro
     */
    public int insert(Dono dono) {
        String sql = "INSERT INTO donos (nome, telefone, email, endereco) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, dono.getNome());
            pstmt.setString(2, dono.getTelefone());
            pstmt.setString(3, dono.getEmail());
            pstmt.setString(4, dono.getEndereco());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir dono: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Atualiza um dono existente
     *
     * @param dono Dono com dados atualizados
     * @return true se atualizado com sucesso
     */
    public boolean update(Dono dono) {
        String sql = "UPDATE donos SET nome = ?, telefone = ?, email = ?, endereco = ?, " +
                "data_atualizacao = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dono.getNome());
            pstmt.setString(2, dono.getTelefone());
            pstmt.setString(3, dono.getEmail());
            pstmt.setString(4, dono.getEndereco());
            pstmt.setInt(5, dono.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar dono: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exclui um dono pelo ID
     *
     * @param id ID do dono
     * @return true se excluído com sucesso
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM donos WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir dono: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca um dono pelo ID
     *
     * @param id ID do dono
     * @return Dono encontrado ou null
     */
    public Dono findById(int id) {
        String sql = "SELECT * FROM donos WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToDono(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar dono: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retorna todos os donos cadastrados
     *
     * @return Lista de donos
     */
    public List<Dono> findAll() {
        List<Dono> donos = new ArrayList<>();
        String sql = "SELECT * FROM donos ORDER BY nome";

        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                donos.add(resultSetToDono(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar donos: " + e.getMessage());
            e.printStackTrace();
        }

        return donos;
    }

    /**
     * Busca donos por nome (parcial)
     *
     * @param nome Nome ou parte do nome
     * @return Lista de donos encontrados
     */
    public List<Dono> findByNome(String nome) {
        List<Dono> donos = new ArrayList<>();
        String sql = "SELECT * FROM donos WHERE nome LIKE ? ORDER BY nome";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nome + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                donos.add(resultSetToDono(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar donos por nome: " + e.getMessage());
            e.printStackTrace();
        }

        return donos;
    }

    /**
     * Busca dono por e-mail
     *
     * @param email E-mail do dono
     * @return Dono encontrado ou null
     */
    public Dono findByEmail(String email) {
        String sql = "SELECT * FROM donos WHERE email = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToDono(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar dono por e-mail: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verifica se um e-mail já está cadastrado
     *
     * @param email E-mail a verificar
     * @return true se existe, false caso contrário
     */
    public boolean emailExiste(String email) {
        return findByEmail(email) != null;
    }

    /**
     * Verifica se um e-mail já está cadastrado (excluindo um ID específico)
     * Útil para validação na edição
     *
     * @param email     E-mail a verificar
     * @param excludeId ID a excluir da verificação
     * @return true se existe, false caso contrário
     */
    public boolean emailExiste(String email, int excludeId) {
        String sql = "SELECT COUNT(*) FROM donos WHERE email = ? AND id != ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao verificar e-mail: " + e.getMessage());
        }

        return false;
    }

    /**
     * Converte ResultSet em objeto Dono
     *
     * @param rs ResultSet do banco
     * @return Objeto Dono
     */
    private Dono resultSetToDono(ResultSet rs) throws SQLException {
        Dono dono = new Dono();
        dono.setId(rs.getInt("id"));
        dono.setNome(rs.getString("nome"));
        dono.setTelefone(rs.getString("telefone"));
        dono.setEmail(rs.getString("email"));
        dono.setEndereco(rs.getString("endereco"));
        return dono;
    }
}