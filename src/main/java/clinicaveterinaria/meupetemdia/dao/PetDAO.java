package clinicaveterinaria.meupetemdia.dao;

import clinicaveterinaria.meupetemdia.config.DatabaseConfig;
import clinicaveterinaria.meupetemdia.model.Pet;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações CRUD de Pets no SQLite
 */
public class PetDAO {

    /**
     * Insere um novo pet no banco
     * @param pet Pet a ser inserido
     * @return ID gerado ou -1 em caso de erro
     */
    public int insert(Pet pet) {
        String sql = "INSERT INTO pets (nome, especie, raca, data_nascimento, dono_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pet.getNome());
            pstmt.setString(2, pet.getEspecie());
            pstmt.setString(3, pet.getRaca());
            pstmt.setDate(4, pet.getDataNascimento() != null ?
                    Date.valueOf(pet.getDataNascimento()) : null);
            pstmt.setInt(5, pet.getDonoId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir pet: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Atualiza um pet existente
     * @param pet Pet com dados atualizados
     * @return true se atualizado com sucesso
     */
    public boolean update(Pet pet) {
        String sql = "UPDATE pets SET nome = ?, especie = ?, raca = ?, " +
                "data_nascimento = ?, dono_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pet.getNome());
            pstmt.setString(2, pet.getEspecie());
            pstmt.setString(3, pet.getRaca());
            pstmt.setDate(4, pet.getDataNascimento() != null ?
                    Date.valueOf(pet.getDataNascimento()) : null);
            pstmt.setInt(5, pet.getDonoId());
            pstmt.setInt(6, pet.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar pet: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exclui um pet pelo ID
     * @param id ID do pet
     * @return true se excluído com sucesso
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM pets WHERE id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir pet: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca um pet pelo ID
     * @param id ID do pet
     * @return Pet encontrado ou null
     */
    public Pet findById(int id) {
        String sql = "SELECT p.*, d.nome as dono_nome FROM pets p " +
                "INNER JOIN donos d ON p.dono_id = d.id WHERE p.id = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToPet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar pet: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retorna todos os pets cadastrados
     * @return Lista de pets
     */
    public List<Pet> findAll() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, d.nome as dono_nome FROM pets p " +
                "INNER JOIN donos d ON p.dono_id = d.id ORDER BY p.nome";

        try (Connection conn = DatabaseConfig.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pets.add(resultSetToPet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar pets: " + e.getMessage());
            e.printStackTrace();
        }

        return pets;
    }

    /**
     * Busca pets por nome (parcial)
     * @param nome Nome ou parte do nome
     * @return Lista de pets encontrados
     */
    public List<Pet> findByNome(String nome) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, d.nome as dono_nome FROM pets p " +
                "INNER JOIN donos d ON p.dono_id = d.id " +
                "WHERE p.nome LIKE ? ORDER BY p.nome";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nome + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                pets.add(resultSetToPet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar pets por nome: " + e.getMessage());
            e.printStackTrace();
        }

        return pets;
    }

    /**
     * Busca pets por dono
     * @param donoId ID do dono
     * @return Lista de pets do dono
     */
    public List<Pet> findByDono(int donoId) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, d.nome as dono_nome FROM pets p " +
                "INNER JOIN donos d ON p.dono_id = d.id " +
                "WHERE p.dono_id = ? ORDER BY p.nome";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, donoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                pets.add(resultSetToPet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar pets por dono: " + e.getMessage());
            e.printStackTrace();
        }

        return pets;
    }

    /**
     * Busca pets por espécie
     * @param especie Espécie do pet
     * @return Lista de pets da espécie
     */
    public List<Pet> findByEspecie(String especie) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, d.nome as dono_nome FROM pets p " +
                "INNER JOIN donos d ON p.dono_id = d.id " +
                "WHERE p.especie = ? ORDER BY p.nome";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, especie);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                pets.add(resultSetToPet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar pets por espécie: " + e.getMessage());
            e.printStackTrace();
        }

        return pets;
    }

    /**
     * Converte ResultSet em objeto Pet
     * @param rs ResultSet do banco
     * @return Objeto Pet
     */
    private Pet resultSetToPet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getInt("id"));
        pet.setNome(rs.getString("nome"));
        pet.setEspecie(rs.getString("especie"));
        pet.setRaca(rs.getString("raca"));

        Date dataNasc = rs.getDate("data_nascimento");
        if (dataNasc != null) {
            pet.setDataNascimento(dataNasc.toLocalDate());
        }

        pet.setDonoId(rs.getInt("dono_id"));
        pet.setDonoNome(rs.getString("dono_nome"));

        return pet;
    }
}