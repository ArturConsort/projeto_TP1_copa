package modelo.classes;

import java.io.Serializable;

public class CategoriaIngresso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private double preco;
    private int estoque;

    public CategoriaIngresso(String nome, double preco, int estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    // ------- metodos do diagrama ------- //

    public void atualizarPreco(double novoPreco) {
        this.preco = novoPreco;
    }

    public boolean temVagasDisponiveis() {
        return estoque > 0;
    }

    public void reduzirEstoque(int quantidade) {
        if (quantidade > estoque) {
            throw new IllegalArgumentException("Quantidade solicitada (" + quantidade + ") maior que o estoque disponível (" + estoque + ")");
        }
        this.estoque -= quantidade;
    }

    // ------- getters e setters ------- //

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getEstoque() {
        return estoque;
    }
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
}