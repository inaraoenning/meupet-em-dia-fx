package clinicaveterinaria.meupetemdia.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private static final String URL = "jdbc:sqlite:mydatabase.db";

    // Abre a conexão
    public static Connection connect() {
        Connection conn = null;
        try {
            System.out.println("Banco real: " + new java.io.File("mydatabase.db").getAbsolutePath());
            conn = DriverManager.getConnection(URL);
            System.out.println("Conectado ao SQLite!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
        return conn;
    }

    // Criação das tabelas
    public static void initialize() {

        String tableDonos = """
                    CREATE TABLE IF NOT EXISTS donos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        telefone TEXT,
                        email TEXT,
                        endereco TEXT
                    );
                """;

        String tablePets = """
                    CREATE TABLE IF NOT EXISTS pets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        especie TEXT,
                        raca TEXT,
                        data_nascimento DATE,
                        dono_id INTEGER,
                        FOREIGN KEY (dono_id) REFERENCES donos(id)
                    );
                """;

        String tableVacinas = """
                   CREATE TABLE IF NOT EXISTS registros_vacinas (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     pet_id INTEGER NOT NULL,
                                     vacina_id INTEGER NOT NULL,
                                     data_aplicacao DATE NOT NULL,
                                     data_proxima_dose DATE,
                                     veterinario TEXT,
                                     observacoes TEXT,
                                     FOREIGN KEY (pet_id) REFERENCES pets(id),
                                     FOREIGN KEY (vacina_id) REFERENCES vacinas(id)
                                 );                                 
                """;


        String registro = """
                    CREATE TABLE IF NOT EXISTS vacinas (
                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                   nome TEXT NOT NULL,
                                   descricao TEXT,
                                   intervalo_dias_reforco INTEGER
                               );
                """;

        String consultas = """
                    CREATE TABLE IF NOT EXISTS consultas (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     pet_id INTEGER NOT NULL,
                                     data_consulta DATE NOT NULL,
                                     tipo TEXT NOT NULL,
                                     veterinario TEXT,
                                     observacoes TEXT,
                                     FOREIGN KEY (pet_id) REFERENCES pets(id)
                                 );                                                 
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(tableDonos);
            stmt.execute(tablePets);
            stmt.execute(tableVacinas);
            stmt.execute(registro);
            stmt.execute(consultas);

            System.out.println("Tabelas criadas/verificadas com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    public static void close() {
    }
}
