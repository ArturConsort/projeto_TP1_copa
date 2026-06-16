package servicos;

import modelo.classes.Estadio;
import modelo.classes.Partida;
import modelo.enumerations.TipoGramado;
import modelo.enumerations.TipoPerfil;
import modelo.excecoes.AcessoNegadoException;
import modelo.excecoes.estadio.EstadioIndisponivelException;
import modelo.excecoes.estadio.EstadioJaCadastradoException;
import modelo.excecoes.estadio.EstadioNaoEncontradoException;
import modelo.classes.Usuario;
import persistencia.EstadioDAO;
import persistencia.PartidaDAO;
import servicos.usuario.SessaoUsuario;
import servicos.Servico;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EstadioServico extends Servico{

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

    public List<Estadio> filtrar(String nome, String cidade, String estado, TipoGramado tipoGramado) throws IOException {

        verificarPermissao(TipoPerfil.ORGANIZADOR, TipoPerfil.ADMINISTRADOR);

        final String filtroNome = nome != null ? nome.trim() : "";
        final String filtroCidade = cidade != null ? cidade.trim() : "";
        final String filtroEstado = estado != null ? estado.trim() : "";

        return estadioDAO.carregaLista()
                .stream()
                .filter(e -> filtroNome.isEmpty()
                        || e.getNome().toLowerCase().contains(filtroNome.toLowerCase()))
                .filter(e -> filtroCidade.isEmpty()
                        || e.getCidade().toLowerCase().contains(filtroCidade.toLowerCase()))
                .filter(e -> filtroEstado.isEmpty()
                        || e.getEstado().toLowerCase().contains(filtroEstado.toLowerCase()))
                .filter(e -> tipoGramado == null
                        || e.getTipoGramado() == tipoGramado)
                .collect(Collectors.toList());
    }

}