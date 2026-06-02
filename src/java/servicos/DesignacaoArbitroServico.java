package src.java.servicos;

import src.java.modelo.classes.Arbitro;
import src.java.modelo.classes.DesignacaoArbitro;
import src.java.modelo.classes.Partida;
import src.java.modelo.classes.Usuario;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.modelo.excecoes.AcessoNegadoException;
import src.java.modelo.excecoes.designacaoarbitro.DesignacaoJaCadastradaException;
import src.java.modelo.excecoes.designacaoarbitro.DesignacaoNaoEncontradaException;
import src.java.modelo.excecoes.designacaoarbitro.NacionalidadeConflitanteException;
import src.java.persistencia.DesignacaoArbitroDAO;
import src.java.servicos.usuario.SessaoUsuario;
import src.java.modelo.classes.Partida;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DesignacaoArbitroServico {

    private final DesignacaoArbitroDAO designacaoDAO;

    public DesignacaoArbitroServico(DesignacaoArbitroDAO designacaoDAO) {
        this.designacaoDAO = designacaoDAO;
    }

    public void criarDesignacao(Partida partida, Arbitro principal, List<Arbitro> assistentes) throws NacionalidadeConflitanteException, DesignacaoJaCadastradaException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        // não permite designação duplicada para a mesma partida
        Optional<DesignacaoArbitro> existente = designacaoDAO.buscarPorPartida(partida.getNumeroPartidas());
        if (existente.isPresent()) {
            throw new DesignacaoJaCadastradaException(partida.getNumeroPartidas());
        }

        DesignacaoArbitro designacao = new DesignacaoArbitro(partida, principal, assistentes);

        // Entidade verifica conflito de nacionalidade com os dados que ela já possui
        designacao.verificarConflitosNacionalidade();

        designacaoDAO.salvar(designacao);
    }

    public DesignacaoArbitro buscarPorPartida(int numeroPartida) throws DesignacaoNaoEncontradaException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        return designacaoDAO.buscarPorPartida(numeroPartida).orElseThrow(() -> new DesignacaoNaoEncontradaException(numeroPartida));
    }

    public List<DesignacaoArbitro> listarDesignacoes() throws IOException {verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);
        return designacaoDAO.carregaLista();
    }

    public void removerDesignacao(int numeroPartida) throws DesignacaoNaoEncontradaException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        Optional<DesignacaoArbitro> existente = designacaoDAO.buscarPorPartida(numeroPartida);
        if (existente.isEmpty()) {
            throw new DesignacaoNaoEncontradaException(numeroPartida);
        }

        designacaoDAO.remover(numeroPartida);
    }

    private void verificarPermissao(TipoPerfil... perfisAceitos) {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado == null) {
            throw new AcessoNegadoException("Nenhum usuário logado.");
        }

        for (TipoPerfil perfil : perfisAceitos) {
            if (logado.getPerfil() == perfil) return;
        }

        throw new AcessoNegadoException("Acesso negado: permissão insuficiente.");
    }
}