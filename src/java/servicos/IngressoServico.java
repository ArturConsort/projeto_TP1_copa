package src.java.servicos;

import src.java.modelo.classes.Ingresso;
import src.java.modelo.classes.Partida;
import src.java.modelo.classes.Usuario;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.modelo.excecoes.AcessoNegadoException;
import src.java.persistencia.IngressoDAO;
import src.java.servicos.usuario.SessaoUsuario;

import java.util.ArrayList;
import java.util.List;

public class IngressoServico {

    private IngressoDAO dao;

    public IngressoServico() {
        this.dao = new IngressoDAO();
    }

    // ------- cadastro e remocao ------- //

    public void cadastrar(Ingresso novoIngresso) throws AcessoNegadoException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR);

        if (novoIngresso.getCategoria() == null) {
            throw new IllegalArgumentException("O ingresso deve ter uma categoria associada");
        }

        if (novoIngresso.getPartida() == null) {
            throw new IllegalArgumentException("O ingresso deve ter uma partida associada");
        }

        if (!novoIngresso.getCategoria().temVagasDisponiveis()) {
            throw new IllegalStateException("Categoria sem vagas disponíveis: " + novoIngresso.getCategoria().getNome());
        }

        dao.salvar(novoIngresso);
    }


    public void remover(int idIngresso) throws AcessoNegadoException {
        verificarPermissao(TipoPerfil.ADMINISTRADOR);
        dao.remover(idIngresso);
    }

    // ------- validacao ------- //

    public boolean validarEntrada(int idIngresso) throws AcessoNegadoException {
        verificarPermissaoMultipla(TipoPerfil.OPERADOR, TipoPerfil.ADMINISTRADOR);

        Ingresso ingresso = dao.buscarPorId(idIngresso);
        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado: " + idIngresso);
        }

        boolean resultado = ingresso.validarEntrada();
        dao.atualizar(ingresso);
        return resultado;
    }

    // ------- metodos de busca ------- //

    public Ingresso buscarPorId(int idIngresso) {
        return dao.buscarPorId(idIngresso);
    }

    public List<Ingresso> pesquisar(Partida partida, Boolean foiValidado) { // criterios opcionais, passar null para ignorar
        List<Ingresso> resultado = new ArrayList<>();

        for (Ingresso i : dao.carregaLista()) {
            boolean partidaOk  = partida     == null || partida.equals(i.getPartida());
            boolean validadoOk = foiValidado == null || foiValidado.equals(i.isFoiValidado());

            if (partidaOk && validadoOk) resultado.add(i);
        }

        return resultado;
    }

    // lista os ingressos comprados pelo usuario logado.
    // nao requer perfil de administrador ou operador: qualquer usuario autenticado pode ver os proprios ingressos.
    public List<Ingresso> listarMeusIngressos() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null) {
            throw new AcessoNegadoException("Nenhum usuario logado");
        }
        return dao.buscarPorUsuario(logado.getLogin());
    }

    // ------- metodos privados auxiliares ------- //

    private void verificarPermissao(TipoPerfil perfilRequisitado) {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null || logado.getPerfil() != perfilRequisitado) {
            throw new AcessoNegadoException("Acesso negado: esse usuario não tem permissao para fazer essa ação");
        }
    }

    // verifica se o usuario logado possui qualquer um dos perfis informados
    private void verificarPermissaoMultipla(TipoPerfil... perfisAceitos) {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null) {
            throw new AcessoNegadoException("Nenhum usuario logado");
        }
        for (TipoPerfil perfil : perfisAceitos) {
            if (logado.getPerfil() == perfil) return;
        }
        throw new AcessoNegadoException("Acesso negado: esse usuario não tem permissao para fazer essa ação");
    }
}