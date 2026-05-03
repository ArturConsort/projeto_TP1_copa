package modelo.classes.perfisDeAcesso;

public class Usuario {

    //atributos
    protected String nome;
    protected String email;
    protected String senha;

    //construtores
    public Usuario(){}
    public Usuario(String nome, String email, String senha){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    //get set
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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



}
