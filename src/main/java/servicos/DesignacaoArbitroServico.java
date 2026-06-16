package servicos;

import modelo.classes.Arbitro;
import modelo.classes.DesignacaoArbitro;
import modelo.classes.Partida;
import modelo.classes.Usuario;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.designacaoarbitro.DesignacaoJaCadastradaException;
import modelo.excecoes.designacaoarbitro.DesignacaoNaoEncontradaException;
import modelo.excecoes.designacaoarbitro.NacionalidadeConflitanteException;
import modelo.excecoes.designacaoarbitro.ArbitroIguaisException;
import persistencia.DesignacaoArbitroDAO;
import servicos.usuario.SessaoUsuario;
import modelo.classes.Partida;
import servicos.Servico;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DesignacaoArbitroServico extends Servico{

    private final DesignacaoArbitroDAO designacaoDAO;

    public DesignacaoArbitroServico(DesignacaoArbitroDAO designacaoDAO) {
        this.designacaoDAO = designacaoDAO;
    }

    public void criarDesignacao(Partida partida, Arbitro principal, List<Arbitro> assistentes) throws NacionalidadeConflitanteException, DesignacaoJaCadastradaException, ArbitroIguaisException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR);

        // não permite designação duplicada para a mesma partida
        Optional<DesignacaoArbitro> existente = designacaoDAO.buscarPorPartida(partida.getNumeroPartidas());
        if (existente.isPresent()) {
            throw new DesignacaoJaCadastradaException(partida.getNumeroPartidas());
        }

        DesignacaoArbitro designacao = new DesignacaoArbitro(partida, principal, assistentes);

        // Entidade verifica conflito de nacionalidade com os dados que ela já possui
        designacao.verificarConflitosNacionalidade();
        // verificar se os arbitros colocados são iguais
        if (principal.equals(assistentes.get(0)) ||
                principal.equals(assistentes.get(1)) ||
                (assistentes.get(0)).equals(assistentes.get(1))) {
            throw new ArbitroIguaisException();
        }
        designacaoDAO.salvar(designacao);
    }

    public DesignacaoArbitro buscarPorPartida(int numeroPartida) throws DesignacaoNaoEncontradaException, IOException {

        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR, TipoPerfil.ARBITRO);

        return designacaoDAO.buscarPorPartida(numeroPartida).orElseThrow(() -> new DesignacaoNaoEncontradaException(numeroPartida));
    }

    public List<DesignacaoArbitro> listarDesignacoes() throws IOException {
        verificarPermissao(TipoPerfil.ADMINISTRADOR, TipoPerfil.ORGANIZADOR, TipoPerfil.ARBITRO);
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

}