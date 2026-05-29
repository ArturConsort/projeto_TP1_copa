package src.java.modelo.classes;

enum CategoriaArbitro{
    FIFA,
    NACIONAL
}

public class Arbitro{
    private String nome;
    private int idade;
    private CategoriaArbitro categoria;
    private int experiencia;
    private String nacionalidade; // nome do pais de origem

    public Arbitro(String nome, int idade, CategoriaArbitro categoria, int experiencia, String nacionalidade){
        this.nome = nome;
        this.idade = idade;
        this.categoria = categoria;
        this.experiencia = experiencia;
        this.nacionalidade = nacionalidade;
    }

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public int getIdade() {return idade;}
    public void setIdade(int idade) {this.idade = idade;}

    public CategoriaArbitro getCategoria() {return categoria;}
    public void setCategoria(CategoriaArbitro categoria) {this.categoria = categoria;}

    public int getExperiencia() {return experiencia;}
    public void setExperiencia(int experiencia) {this.experiencia = experiencia;}

    public String getNacionalidade() {return nacionalidade;}
    public void setNacionalidade(String nacionalidade) {this.nacionalidade = nacionalidade;}
}