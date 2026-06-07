package modelo.classes;

import modelo.enumerations.TipoGramado;
import java.io.Serializable;

public class Estadio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome, cidade, estado;
    private int capacidade;
    private TipoGramado tipoGramado;

    // constructor
    public Estadio(String nome, String cidade, String estado, int capacidade, TipoGramado tipoGramado){
        setNome(nome);
        setCidade(cidade);
        setEstado(estado);
        setCapacidade(capacidade);
        setTipoGramado(tipoGramado);
    }

    // getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null){
            throw new IllegalArgumentException("Nome não pode ser nulo");
        }
        else if (nome.trim().isEmpty()){
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        this.nome = nome;
    }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) {
        if (cidade == null){
            throw new IllegalArgumentException("Cidade não pode ser nula");
        }
        else if (cidade.trim().isEmpty()){
            throw new IllegalArgumentException("Cidade não pode ser vazia");
        }
        this.cidade = cidade;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) {
        if (estado == null){
            throw new IllegalArgumentException("Estado não pode ser nulo");
        }
        else if (estado.trim().isEmpty()){
            throw new IllegalArgumentException("Estado não pode ser vazio");
        }
        this.estado = estado;
    }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
        this.capacidade = capacidade;
    }

    public TipoGramado getTipoGramado() { return this.tipoGramado; }
    public void setTipoGramado(TipoGramado tipoGramado) {
        if (tipoGramado == null) {
            throw new IllegalArgumentException("O tipo de gramado não pode ser nulo.");
        }
        this.tipoGramado = tipoGramado;
    }

    // functions
    public String getLocalizacao(){
        return String.format("Localização: %s, %s", this.cidade, this.estado);

    }
    public boolean aceitaPublico(int quantidade) {
        return quantidade <= this.capacidade;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Estadio)) return false;
        Estadio outro = (Estadio) obj;
        return this.nome.equalsIgnoreCase(outro.nome);
    }

    @Override
    public String toString() {
        return nome + " - " + cidade;
    }

    @Override
    public int hashCode() {
        return nome.toLowerCase().hashCode();
    }

}