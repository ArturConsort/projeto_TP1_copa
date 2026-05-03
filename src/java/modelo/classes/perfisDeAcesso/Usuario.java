package modelo.classes.perfisDeAcesso;

import modelo.classes.outros.Pessoa;

public class Usuario extends Pessoa {

    //atributos
    protected String login;
    protected String senha;

    //construtores
    public Usuario(String nome, String cpf, String email, String login, String senha) {
        super(nome, cpf, email);
        this.senha = senha;
        this.login = login;
    }

    //get set
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }


    //metodos
    //Boolean login(){}
    //void logout(){}
    //void listarUsuario(){}
    //pesquisar usuario(){}




}
