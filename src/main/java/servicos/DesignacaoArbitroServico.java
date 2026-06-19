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
import java.util.stream.Collectors;

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

        List<DesignacaoArbitro> todas = designacaoDAO.carregaLista();

        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado.getPerfil() == TipoPerfil.ARBITRO) {
            String nomeArbitro = logado.getNome().toLowerCase();
            return todas.stream()
                    .filter(d -> d.getPrincipalArbitro().getNome().toLowerCase().equals(nomeArbitro)
                            || (d.getAssistentes().size() > 0 && d.getAssistentes().get(0).getNome().toLowerCase().equals(nomeArbitro))
                            || (d.getAssistentes().size() > 1 && d.getAssistentes().get(1).getNome().toLowerCase().equals(nomeArbitro)))
                    .collect(Collectors.toList());
        }

        return todas;
    }

    public List<DesignacaoArbitro> pesquisarDesignacoes(String filtroPartida, String filtroArbitroPrincipal, String filtroAssistente1, String filtroAssistente2) throws IOException {
        List<DesignacaoArbitro> base = listarDesignacoes();

        final String fPartida = filtroPartida != null ? filtroPartida.trim().toLowerCase() : "";
        final String fPrincipal = filtroArbitroPrincipal != null ? filtroArbitroPrincipal.trim().toLowerCase() : "";
        final String fAss1 = filtroAssistente1 != null ? filtroAssistente1.trim().toLowerCase() : "";
        final String fAss2 = filtroAssistente2 != null ? filtroAssistente2.trim().toLowerCase() : "";

        return base.stream()
                .filter(d -> fPartida.isEmpty() || d.getPartida().toString().toLowerCase().contains(fPartida))
                .filter(d -> fPrincipal.isEmpty() || d.getPrincipalArbitro().getNome().toLowerCase().contains(fPrincipal))
                .filter(d -> fAss1.isEmpty() || (d.getAssistentes().size() > 0 && d.getAssistentes().get(0).getNome().toLowerCase().contains(fAss1)))
                .filter(d -> fAss2.isEmpty() || (d.getAssistentes().size() > 1 && d.getAssistentes().get(1).getNome().toLowerCase().contains(fAss2)))
                .collect(Collectors.toList());
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