package clinicaveterinaria.meupetemdia.model;

import java.time.LocalDate;

/**
 * Classe que representa um Pet
 */
public class Pet {

    private Integer id;
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;
    private Integer donoId;
    private String donoNome; // Para exibição na tabela

    // ========== CONSTRUTORES ==========

    public Pet() {
    }

    public Pet(Integer id, String nome, String especie, String raca,
               LocalDate dataNascimento, Integer donoId) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.donoId = donoId;
    }

    public Pet(String nome, String especie, String raca,
               LocalDate dataNascimento, Integer donoId) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.donoId = donoId;
    }

    // ========== GETTERS E SETTERS ==========

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

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getDonoId() {
        return donoId;
    }

    public void setDonoId(Integer donoId) {
        this.donoId = donoId;
    }

    public String getDonoNome() {
        return donoNome;
    }

    public void setDonoNome(String donoNome) {
        this.donoNome = donoNome;
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Calcula a idade do pet em anos
     * @return Idade em anos ou 0 se data de nascimento não informada
     */
    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dataNascimento.getYear();
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especie='" + especie + '\'' +
                ", raca='" + raca + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", donoId=" + donoId +
                ", donoNome='" + donoNome + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id != null && id.equals(pet.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}