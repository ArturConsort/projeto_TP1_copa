package src.java.servicos;

import src.java.modelo.classes.Estadio;
import src.java.modelo.classes.Partida;
import src.java.modelo.enumerations.TipoGramado;
import src.java.modelo.enumerations.TipoPerfil;
import src.java.modelo.excecoes.estadio.EstadioIndisponivelException;
import src.java.modelo.excecoes.estadio.EstadioJaCadastradoException;
import src.java.modelo.excecoes.estadio.EstadioNaoEncontradoException;
import src.java.persistencia.EstadioDAO;
import src.java.persistencia.PartidaDAO;
import src.java.servicos.usuario.SessaoUsuario;
import src.java.modelo.classes.Usuario;
import src.java.modelo.excecoes.AcessoNegadoException;



import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EstadioServico {

    private final EstadioDAO estadioDAO;
    private final PartidaDAO partidaDAO;

    public EstadioServico(EstadioDAO estadioDAO, PartidaDAO partidaDAO) {
        this.estadioDAO = estadioDAO;
        this.partidaDAO = partidaDAO;
    }

    public void cadastrarEstadio(String nome, String cidade, String estado, int capacidade, TipoGramado tipoGramado) throws EstadioJaCadastradoException, IOException {

        verificarPermissao(TipoPerfil.ORGANIZADOR, TipoPerfil.ADMINISTRADOR);

        Estadio verificar = estadioDAO.buscarPorNome(nome);

        if (verificar != null) {
            throw new EstadioJaCadastradoException(nome);
        }


        Estadio estadio = new Estadio(nome, cidade, estado, capacidade, tipoGramado);
        estadioDAO.salvar(estadio);
    }

    public Estadio buscarPorNome(String nome) throws EstadioNaoEncontradoException, IOException {

        verificarPermissao(TipoPerfil.ORGANIZADOR, TipoPerfil.ADMINISTRADOR);

        Estadio verificar = estadioDAO.buscarPorNome(nome);

        if (verificar == null) {
            throw new EstadioNaoEncontradoException(nome);
        }

        return verificar;
    }

    public List<Estadio> listarEstadios() throws IOException {
        verificarPermissao(TipoPerfil.ORGANIZADOR, TipoPerfil.ADMINISTRADOR);

        return estadioDAO.carregaLista();
    }

    public void removerEstadio(String nome) throws EstadioNaoEncontradoException, IOException {

        verificarPermissao(TipoPerfil.ORGANIZADOR, TipoPerfil.ADMINISTRADOR);
        Estadio verificar = estadioDAO.buscarPorNome(nome);
        if (verificar == null) {
            throw new EstadioNaoEncontradoException(nome);
        }

        estadioDAO.remover(nome);
    }

    // estádio não pode sediar duas partidas no mesmo horário
    public void verificarDisponibilidade(Estadio estadio, String data, String horario) throws EstadioIndisponivelException, IOException {

        List<Partida> partidas = partidaDAO.carregaLista();

        for (Partida partida : partidas) {
            boolean mesmoEstadio = partida.getEstadio().getNome().equalsIgnoreCase(estadio.getNome());
            boolean mesmaData    = partida.getData().equals(data);
            boolean mesmoHorario = partida.getHorario().equals(horario);

            if (mesmoEstadio && mesmaData && mesmoHorario) {
                throw new EstadioIndisponivelException(estadio.getNome(), data, horario);
            }
        }
    }
    // verifica se o usuario logado possui qualquer um dos perfis informados
    private void verificarPermissao(TipoPerfil... perfisAceitos) {
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