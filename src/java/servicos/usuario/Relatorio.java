package src.java.servicos.usuario;

import src.java.modelo.classes.Usuario;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.persistencia.UsuarioDAO;

import java.util.List;

public class Relatorio {

    private UsuarioDAO dao;

    public Relatorio(){
        this.dao = new UsuarioDAO();
    }

    public void exibirRelatorioUsuarios(){

        List<Usuario> todosUsuarios = dao.carregaLista();

        long administrador = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.ADMINISTRADOR).count();
        long organizador = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.ORGANIZADOR).count();
        long operador = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.OPERADOR).count();
        long arbitro = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.ARBITRO).count();

        System.out.println("=== RELATÓrio de USUÁRIOS ===");
        System.out.println("Quantidade total de usuários: " + todosUsuarios.size());
        System.out.println("Administradores: " + administrador);
        System.out.println("Organizadores: " + organizador);
        System.out.println("Operadores: " + operador);
        System.out.println("Árbitros: " + arbitro);

    }




    public void exibirRelatorioGeral(){
        // relatorio com:
        // numero de partidas
        // publico
        // desempenho das selecoes
    }



}
