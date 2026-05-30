package src.java.servicos;

import src.java.modelo.classes.Ingresso;
import src.java.modelo.classes.CategoriaIngresso;
import src.java.modelo.classes.Partida;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.persistencia.IngressoDAO;
import src.java.servicos.usuario.SessaoUsuario;
import src.java.modelo.classes.Usuario;
import src.java.modelo.excecoes.AcessoNegadoException;

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

        if (!novoIngresso.getCategoria().temVagasDisponiveis()) {
            throw new IllegalStateException("Categoria sem vagas disponíveis: " + novoIngresso.getCategoria().getNome());
        }

        dao.salvar(novoIngresso);
    }


    public void remover(String idIngresso) throws AcessoNegadoException {
        verificarPermissao(TipoPerfil.ADMINISTRADOR);
        dao.remover(idIngresso);
    }

    // ------- validacao ------- //

    public boolean validarEntrada(String idIngresso) throws AcessoNegadoException {
        verificarPermissao(TipoPerfil.OPERADOR);

        Ingresso ingresso = dao.buscarPorId(idIngresso);
        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado: " + idIngresso);
        }

        boolean resultado = ingresso.validarEntrada();
        dao.atualizar(ingresso);
        return resultado;
    }

    // ------- metodos de busca ------- //

    public Ingresso buscarPorId(String idIngresso) {
        return dao.buscarPorId(idIngresso);
    }

    public List<Ingresso> pesquisar(Partida partida, Boolean foiValidado) { // esses criterios sao opcionais, passar null para ignorar algum deles
        List<Ingresso> resultado = new ArrayList<>();

        for (Ingresso i : dao.carregaLista()) {
            boolean partidaOk     = partida     == null   ||   partida.equals(i.getPartida());
            boolean validadoOk    = foiValidado == null   ||   foiValidado.equals(i.isFoiValidado());

            if (partidaOk && validadoOk) resultado.add(i);
        }

        return resultado;
    }

    // ------- metodos privados auxiliares ------- //

    private void verificarPermissao(TipoPerfil perfilRequisitado) {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null || logado.getPerfil() != perfilRequisitado) {
            throw new AcessoNegadoException("Acesso negado: esse usuario não tem permissao para fazer essa ação");
        }
    }
}