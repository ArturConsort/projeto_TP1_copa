package src.java.modelo.classes.perfisDeAcesso;

import src.java.modelo.classes.outros.Pessoa;

public class Usuario extends Pessoa {

    //atributos
    protected String login;
    protected String senha;
    protected TipoPerfil perfil;

    //construtores
    public Usuario(String nome, String cpf, String email, String login, String senha, TipoPerfil perfil) {
        super(nome, cpf, email);
        this.senha = senha;
        this.login = login;
        this.perfil = perfil;
    }

    //get set
    public String getLogin() {return login;}

    public void setLogin(String login) {this.login = login;}

    public String getSenha() {return senha;}

    public void setSenha(String senha) {this.senha = senha;}

    public TipoPerfil getPerfil() {return perfil;}

    public void setPerfil(TipoPerfil perfil) {this.perfil = perfil;}

}
