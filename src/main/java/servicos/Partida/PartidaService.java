package servicos.Partida;
import modelo.enumerations.FasePartida;
import modelo.classes.Estadio;
import modelo.classes.*;
import modelo.enumerations.StatusPartida;
import persistencia.ArbitroDAO;
import persistencia.EstadioDAO;
import persistencia.PartidaDAO;
import persistencia.SelecaoDAO;

import java.util.List;

public class PartidaService {

    private PartidaDAO partidaDAO;
    private SelecaoDAO selecaoDAO;
    private EstadioDAO estadioDAO; // ← adicionado
    private ArbitroDAO arbitroDAO;

    public PartidaService() {
        this.partidaDAO = new PartidaDAO();
        this.selecaoDAO = new SelecaoDAO();
        this.estadioDAO = new EstadioDAO();
        this.arbitroDAO = new ArbitroDAO();
    }

    public List<Selecao> listarSelecoes() {
        return selecaoDAO.carregaLista();
    }

    public List<Estadio> listarEstadios() {
        return estadioDAO.carregaLista();
    }

    public List<Arbitro> listarArbitros() {
        return arbitroDAO.carregaLista();
    }

    public void cadastrarPartida(Selecao timeCasa,
                                 Selecao timeVisitante,
                                 Estadio estadio,
                                 String cidade,
                                 String data,
                                 String horario,
                                 Arbitro arbitro,
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
        partida.setArbitroPrincipal(arbitro);
        validarConflitoHorario(timeCasa, timeVisitante, data, horario);
        validarConflitoEstadio(estadio, data, horario);

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

    // Adicione esses métodos no PartidaService existente:

    // Regra: "Uma seleção não pode jogar duas partidas no mesmo horário"
    private void validarConflitoHorario(Selecao timeCasa, Selecao timeVisitante,
                                        String data, String horario) throws Exception {
        for (Partida p : partidaDAO.carregaLista()) {
            if (p.getData().equals(data) && p.getHorario().equals(horario)) {
                if (p.getTimeCasa().equals(timeCasa) || p.getTimeVisitante().equals(timeCasa)) {
                    throw new Exception(timeCasa.getPais() + " já tem partida nesse horário!");
                }
                if (p.getTimeCasa().equals(timeVisitante) || p.getTimeVisitante().equals(timeVisitante)) {
                    throw new Exception(timeVisitante.getPais() + " já tem partida nesse horário!");
                }
            }
        }
    }

    // Regra: "Um estádio não pode sediar duas partidas no mesmo horário"
    private void validarConflitoEstadio(Estadio estadio, String data, String horario) throws Exception {
        for (Partida p : partidaDAO.carregaLista()) {
            if (p.getEstadio().equals(estadio) && p.getData().equals(data) && p.getHorario().equals(horario)) {
                throw new Exception("Este estádio já tem partida nesse horário!");
            }
        }
    }

    // Métodos de consulta expostos para a tela
    public List<Partida> buscarPorFase(FasePartida fase) {
        return partidaDAO.buscarPorFase(fase);
    }

    public List<Partida> buscarPorData(String data) {
        return partidaDAO.buscarPorData(data);
    }

    public List<Partida> buscarPorSelecao(Selecao selecao) {
        return partidaDAO.buscarPorSelecao(selecao);
    }

    // Atualiza o status da partida — ex: AGENDADA → EM_ANDAMENTO → FINALIZADA
    public void atualizarStatus(int numeroPartida, StatusPartida novoStatus) throws Exception {
        Partida partida = partidaDAO.buscarPorNumero(numeroPartida);
        if (partida == null) {
            throw new Exception("Partida não encontrada!");
        }
        partida.setStatus(novoStatus);
        partidaDAO.atualizar(partida);
    }

    public List<Partida> listarPartidas() {
        return partidaDAO.carregaLista();
    }
}