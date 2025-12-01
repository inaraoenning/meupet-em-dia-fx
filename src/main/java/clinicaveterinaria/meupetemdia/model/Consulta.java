package clinicaveterinaria.meupetemdia.model;

import java.time.LocalDate;


// Representa uma Consulta veterinária
public class Consulta {

    private Integer id;
    private Integer petId;
    private String petNome; // Para exibição
    private LocalDate dataConsulta;
    private String tipo; // Rotina, Emergência, Retorno, etc.
    private String veterinario;
    private String observacoes;

    // CONSTRUTORES

    public Consulta() {
    }

    public Consulta(Integer id, Integer petId, LocalDate dataConsulta, String tipo,
                    String veterinario, String observacoes) {
        this.id = id;
        this.petId = petId;
        this.dataConsulta = dataConsulta;
        this.tipo = tipo;
        this.veterinario = veterinario;
        this.observacoes = observacoes;
    }

    public Consulta(Integer petId, LocalDate dataConsulta, String tipo,
                    String veterinario, String observacoes) {
        this.petId = petId;
        this.dataConsulta = dataConsulta;
        this.tipo = tipo;
        this.veterinario = veterinario;
        this.observacoes = observacoes;
    }

    // GETTERS E SETTERS

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

    public LocalDate getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDate dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    // MÉTODOS AUXILIARES

    @Override
    public String toString() {
        return "Consulta{" +
                "id=" + id +
                ", petNome='" + petNome + '\'' +
                ", data=" + dataConsulta +
                ", tipo='" + tipo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consulta consulta = (Consulta) o;
        return id != null && id.equals(consulta.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}