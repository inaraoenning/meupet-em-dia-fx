package clinicaveterinaria.meupetemdia.model;

import java.time.LocalDate;

/**
 * Classe que representa uma aplicação de vacina em um pet
 */
public class RegistroVacina {

    private Integer id;
    private Integer petId;
    private String petNome; // Para exibição
    private Integer vacinaId;
    private String vacinaNome; // Para exibição
    private LocalDate dataAplicacao;
    private LocalDate dataProximaDose;
    private String veterinario;
    private String observacoes;

    // ========== CONSTRUTORES ==========

    public RegistroVacina() {
    }

    public RegistroVacina(Integer id, Integer petId, Integer vacinaId, LocalDate dataAplicacao,
                          LocalDate dataProximaDose, String veterinario, String observacoes) {
        this.id = id;
        this.petId = petId;
        this.vacinaId = vacinaId;
        this.dataAplicacao = dataAplicacao;
        this.dataProximaDose = dataProximaDose;
        this.veterinario = veterinario;
        this.observacoes = observacoes;
    }

    public RegistroVacina(Integer petId, Integer vacinaId, LocalDate dataAplicacao,
                          LocalDate dataProximaDose, String veterinario, String observacoes) {
        this.petId = petId;
        this.vacinaId = vacinaId;
        this.dataAplicacao = dataAplicacao;
        this.dataProximaDose = dataProximaDose;
        this.veterinario = veterinario;
        this.observacoes = observacoes;
    }

    // ========== GETTERS E SETTERS ==========

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public String getPetNome() {
        return petNome;
    }

    public void setPetNome(String petNome) {
        this.petNome = petNome;
    }

    public Integer getVacinaId() {
        return vacinaId;
    }

    public void setVacinaId(Integer vacinaId) {
        this.vacinaId = vacinaId;
    }

    public String getVacinaNome() {
        return vacinaNome;
    }

    public void setVacinaNome(String vacinaNome) {
        this.vacinaNome = vacinaNome;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public void setDataAplicacao(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
    }

    public LocalDate getDataProximaDose() {
        return dataProximaDose;
    }

    public void setDataProximaDose(LocalDate dataProximaDose) {
        this.dataProximaDose = dataProximaDose;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Verifica se a vacina está vencida (próxima dose passou)
     */
    public boolean isVencida() {
        if (dataProximaDose == null) {
            return false;
        }
        return dataProximaDose.isBefore(LocalDate.now());
    }

    /**
     * Verifica se a vacina está próxima do vencimento (30 dias)
     */
    public boolean isProximaVencimento() {
        if (dataProximaDose == null) {
            return false;
        }
        LocalDate daquiA30Dias = LocalDate.now().plusDays(30);
        return dataProximaDose.isAfter(LocalDate.now()) &&
                dataProximaDose.isBefore(daquiA30Dias);
    }

    @Override
    public String toString() {
        return "RegistroVacina{" +
                "id=" + id +
                ", petNome='" + petNome + '\'' +
                ", vacinaNome='" + vacinaNome + '\'' +
                ", dataAplicacao=" + dataAplicacao +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroVacina that = (RegistroVacina) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}