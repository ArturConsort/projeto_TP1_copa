package src.java.servicos.Partida;
import src.java.modelo.enumerations.FasePartida;
import src.java.modelo.classes.Estadio;
import src.java.modelo.classes.*;
import src.java.persistencia.EstadioDAO;
import src.java.persistencia.PartidaDAO;
import src.java.persistencia.SelecaoDAO;

import java.util.List;

public class PartidaService {

    private PartidaDAO partidaDAO;
    private SelecaoDAO selecaoDAO;
    private EstadioDAO estadioDAO; // ← adicionado

    public PartidaService() {
        this.partidaDAO = new PartidaDAO();
        this.selecaoDAO = new SelecaoDAO();
        this.estadioDAO = new EstadioDAO(); // ← adicionado
    }

    public List<Selecao> listarSelecoes() {
        return selecaoDAO.carregaLista();
    }

    public List<Estadio> listarEstadios() { 
        return estadioDAO.carregaLista();
    }

    public void cadastrarPartida(Selecao timeCasa,
                                 Selecao timeVisitante,
                                 Estadio estadio,
                                 String cidade,
                                 String data,
                                 String horario,
                                 String rodada,
                                 FasePartida fase) throws Exception {

        if (timeCasa == null || timeVisitante == null) {
            throw new Exception("Selecione os dois times!");
        }
        if (!listarSelecoes().contains(timeCasa)) {
            throw new Exception("Time da casa não cadastrado!");
        }
        if (!listarSelecoes().contains(timeVisitante)) {
            throw new Exception("Time visitante não cadastrado!");
        }
        if (timeCasa.equals(timeVisitante)) {
            throw new Exception("Os dois times não podem ser iguais!");
        }
        if (estadio == null) {
            throw new Exception("O estádio é obrigatório!");
        }
        if (!listarEstadios().contains(estadio)) {
            throw new Exception("Estádio não cadastrado!");
        }
        if (cidade.trim().isEmpty()) {
            throw new Exception("A cidade é obrigatória!");
        }
        if (data.trim().isEmpty()) {
            throw new Exception("A data é obrigatória!");
        }
        if (horario.trim().isEmpty()) {
            throw new Exception("O horário é obrigatório!");
        }

        int numero = Partida.getContPartidas() + 1;
        Partida.setContPartidas(numero);

        Partida partida = new Partida(
                timeCasa, timeVisitante, numero,
                cidade, data, horario, estadio, fase
        );

        partidaDAO.salvar(partida);
    }

    public void removerPartida(int numero) throws Exception {
        if (partidaDAO.buscarPorNumero(numero) == null) {
            throw new Exception("Partida número " + numero + " não encontrada!");
        }
        partidaDAO.remover(numero);
    }

    public void atualizarPartida(Partida partida) throws Exception {
        if (partidaDAO.buscarPorNumero(partida.getNumeroPartidas()) == null) {
            throw new Exception("Partida não encontrada para atualização!");
        }
        partidaDAO.atualizar(partida);
    }

    public List<Partida> listarPartidas() {
        return partidaDAO.carregaLista();
    }
}