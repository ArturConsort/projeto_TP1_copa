package src.java.servicos;

import src.java.modelo.classes.perfisDeAcesso.Usuario;
import src.java.modelo.excecoes.LoginNaoEncontradoExeption;
import src.java.persistencia.UsuarioDAO;

import java.util.List;

public class UsuarioServico {

    UsuarioDAO dao;

    public UsuarioServico(){
        this.dao =  new UsuarioDAO();
    }

    public Usuario login(String login, String senha){
        List<Usuario> listaUsuarios = dao.carregaLista();
        for(Usuario u : listaUsuarios){
            if(u.getLogin().equals(login) && u.getSenha().equals(senha)){
                SessaoUsuario.getInstancia().iniciarSessao(u);
                return u;
            }
        }
        throw new LoginNaoEncontradoExeption("Login ou senha inválidos");
    }

    public void logout(){
        SessaoUsuario.getInstancia().encerrarSessao();
    }

}
