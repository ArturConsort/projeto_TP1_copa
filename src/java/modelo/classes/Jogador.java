package src.java.modelo.classes;

public class Jogador {
    private String nome;
    private int idade;
    private String numeracao;
    private String posicao;
    private Selecao selecao;

    public Jogador(String nome, int idade, String numeracao, String posicao, Selecao selecao) {
        this.nome = nome;
        this.idade = idade;
        this.numeracao = numeracao;
        this.posicao = posicao;
        this.selecao = selecao;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }
    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getNumeracao() {
        return numeracao;
    }
    public void setNumeracao(String numeracao) {
        this.numeracao = numeracao;
    }

    public String getPosicao() {
        return posicao;
    }
    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public Selecao getSelecao() {
        return selecao;
    }
    public void setSelecao(Selecao selecao) {
        this.selecao = selecao;
    }
}
