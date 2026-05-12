package src.java.modelo.classes.outros;

enum TipoGramado{
    SINTETICO,
    NATURAL
}
public class Estadio{
    private String nome, cidade, estado;
    private int capacidade;
    private TipoGramado tipoGramado;
    // private String image_path; --> será discutido

    // constructor
    public Estadio(String nome, String cidade, String estado, int capacidade, TipoGramado tipoGramado){
        this.nome = nome;
        this.cidade = cidade;
        this.estado = estado;
        this.capacidade = capacidade;
        this.tipoGramado = tipoGramado;
    }

    // getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    public TipoGramado getTipoGramado() { return this.tipoGramado; }
    public void setTipoGramado(TipoGramado tipoGramado) { this.tipoGramado = tipoGramado; }

    // functions
    public String getLocalizacao(){
        return String.format("Localização: %s, %s", this.cidade, this.estado);

    }
    public boolean aceitaPublico(int quantidade) {
        return quantidade <= this.capacidade;
    }


}