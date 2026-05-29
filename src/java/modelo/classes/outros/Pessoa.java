package src.java.modelo.classes.outros;

import java.io.Serializable;

public abstract class  Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;    // essa parte nao eh obrigatoria, mas eh recomendada pra manter a leitura e escrita funcionando mesmo com alteracoes na classe

    //atributos
    private String nome;
    private String cpf;
    private String email;

    //construtor
    public Pessoa(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    //get set
    public String getCpf() {return cpf;}
    public void setCpf(String cpf) {this.cpf = cpf;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}



}


