package src.java.modelo.classes;

import src.java.modelo.enumerations.CategoriaArbitro;

public class Arbitro implements Serializable{

    private static final long serialVersionUID = 1L;

    private String nome;
    private int idade;
    private CategoriaArbitro categoria;
    private int experiencia;
    private String nacionalidade; // nome do país de origem

    public Arbitro(String nome, int idade, CategoriaArbitro categoria, int experiencia, String nacionalidade) {
        setNome(nome);
        setIdade(idade);
        setCategoria(categoria);
        setExperiencia(experiencia);
        setNacionalidade(nacionalidade);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        if (idade < 0) {
            throw new IllegalArgumentException("A idade não pode ser negativa.");
        }
        this.idade = idade;
    }

    public CategoriaArbitro getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaArbitro categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria não pode ser nula.");
        }
        this.categoria = categoria;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        if (experiencia < 0) {
            throw new IllegalArgumentException("A experiência não pode ser negativa.");
        }

        this.experiencia = experiencia;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        if (nacionalidade == null || nacionalidade.trim().isEmpty()) {
            throw new IllegalArgumentException("A nacionalidade não pode ser vazia.");
        }
        this.nacionalidade = nacionalidade;
    }
}