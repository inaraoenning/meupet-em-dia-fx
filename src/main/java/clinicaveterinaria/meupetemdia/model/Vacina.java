package clinicaveterinaria.meupetemdia.model;


// Classe tipo de vacina disponível
public class Vacina {

    private Integer id;
    private String nome;
    private String descricao;
    private Integer intervaloDiasReforco; // Dias até próxima dose

    // CONSTRUTORES
    public Vacina() {
    }

    public Vacina(Integer id, String nome, String descricao, Integer intervaloDiasReforco) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.intervaloDiasReforco = intervaloDiasReforco;
    }

    public Vacina(String nome, String descricao, Integer intervaloDiasReforco) {
        this.nome = nome;
        this.descricao = descricao;
        this.intervaloDiasReforco = intervaloDiasReforco;
    }

    // GETTERS E SETTERS
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getIntervaloDiasReforco() {
        return intervaloDiasReforco;
    }

    public void setIntervaloDiasReforco(Integer intervaloDiasReforco) {
        this.intervaloDiasReforco = intervaloDiasReforco;
    }

    // MÉTODOS AUXILIARES

    @Override
    public String toString() {
        return nome; // Para exibição no ComboBox
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacina vacina = (Vacina) o;
        return id != null && id.equals(vacina.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}