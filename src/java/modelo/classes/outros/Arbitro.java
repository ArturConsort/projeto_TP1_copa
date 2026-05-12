package src.java.modelo.classes.outros;

enum Categoria{
    FIFA,
    NACIONAL
}

public class Arbitro{
    private String nome;
    private int idade;
    private Categoria categoria;
    private int experiencia;
    private String nacionalidade;

    public Arbitro(String nome, int idade, Categoria categoria, int experiencia){
        this.nome = nome;
        this.idade = idade;
        this.categoria = categoria;
        this.experiencia = experiencia;
    }

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public int getIdade() {return idade;}
    public void setIdade(int idade) {this.idade = idade;}

    public Categoria getCategoria() {return categoria;}
    public void setCategoria(Categoria categoria) {this.categoria = categoria;}

    public int getExperiencia() {return experiencia;}
    public void setExperiencia(int experiencia) {this.experiencia = experiencia;}

    public String getNacionalidade() {return nacionalidade;}
    public void setNacionalidade(String nacionalidade) {this.nacionalidade = nacionalidade;}
}