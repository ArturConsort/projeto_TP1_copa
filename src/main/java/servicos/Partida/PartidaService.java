package servicos.Partida;
import modelo.enumerations.FasePartida;
import modelo.classes.Estadio;
import modelo.classes.*;
import modelo.enumerations.StatusPartida;
import persistencia.ArbitroDAO;
import persistencia.EstadioDAO;
import persistencia.PartidaDAO;
import persistencia.SelecaoDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PartidaService {

    private PartidaDAO partidaDAO;
    private SelecaoDAO selecaoDAO;
    private EstadioDAO estadioDAO;
    private ArbitroDAO arbitroDAO;

    // Formato de data usado no sistema — dd/MM/yyyy
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PartidaService() {
        this.partidaDAO = new PartidaDAO();
        this.selecaoDAO = new SelecaoDAO();
        this.estadioDAO = new EstadioDAO();
        this.arbitroDAO = new ArbitroDAO();
    }

    public List<Selecao> listarSelecoes() { return selecaoDAO.carregaLista(); }
    public List<Estadio> listarEstadios() { return estadioDAO.carregaLista(); }
    public List<Arbitro> listarArbitros() { return arbitroDAO.carregaLista(); }

    public void cadastrarPartida(Selecao timeCasa,
                                 Selecao timeVisitante,
                                 Estadio estadio,
                                 String cidade,
                                 String data,
                                 String horario,
                                 Arbitro arbitro,
                                 FasePartida fase) throws Exception {

        // --- Validações básicas ---
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
        if (data.trim().isEmpty()) {
            throw new Exception("A data é obrigatória!");
        }
        if (horario.trim().isEmpty()) {
            throw new Exception("O horário é obrigatório!");
        }

        // --- Converte a data da nova partida para LocalDate ---
        LocalDate dataNovaPartida = converterData(data);

        // --- Validações de conflito ---
        validarConflitoHorario(timeCasa, timeVisitante, data, horario);
        validarConflitoEstadio(estadio, data, horario);
        validarOrdemCronologica(timeCasa, timeVisitante, dataNovaPartida);

        // --- Monta e salva ---
        int numero = Partida.getContPartidas() + 1;
        Partida.setContPartidas(numero);

        Partida partida = new Partida(
                timeCasa, timeVisitante, numero,
                cidade, data, horario, estadio, fase
        );
        partida.setArbitroPrincipal(arbitro);

        partidaDAO.salvar(partida);
    }

    // ================================================================
    //  NOVA VALIDAÇÃO — ordem cronológica
    // ================================================================

    // Regra: se um time já tem uma partida agendada para uma data POSTERIOR,
    // não é possível cadastrar uma nova partida para ele em data ANTERIOR.
    // Isso garante que as partidas de cada time seguem uma ordem cronológica.
    private void validarOrdemCronologica(Selecao timeCasa, Selecao timeVisitante,
                                         LocalDate dataNovaPartida) throws Exception {

        for (Partida p : partidaDAO.carregaLista()) {

            // Verifica se a partida existente envolve algum dos dois times
            boolean envolveTimeCasa = p.getTimeCasa().equals(timeCasa)
                    || p.getTimeVisitante().equals(timeCasa);

            boolean envolveTimeVisitante = p.getTimeCasa().equals(timeVisitante)
                    || p.getTimeVisitante().equals(timeVisitante);

            if (!envolveTimeCasa && !envolveTimeVisitante) continue;

            // Tenta converter a data da partida existente
            LocalDate dataExistente = converterData(p.getData());

            // Se a partida existente é POSTERIOR à nova, não pode cadastrar
            // pois criaria uma partida "antes" de uma já agendada
            if (dataExistente.isAfter(dataNovaPartida)) {

                if (envolveTimeCasa) {
                    throw new Exception(
                            timeCasa.getPais() + " já tem uma partida agendada para " +
                                    p.getData() + " (Partida Nº " + p.getNumeroPartidas() + "). " +
                                    "Não é possível cadastrar uma partida para uma data anterior (" +
                                    dataNovaPartida.format(FMT) + ")."
                    );
                }

                if (envolveTimeVisitante) {
                    throw new Exception(
                            timeVisitante.getPais() + " já tem uma partida agendada para " +
                                    p.getData() + " (Partida Nº " + p.getNumeroPartidas() + "). " +
                                    "Não é possível cadastrar uma partida para uma data anterior (" +
                                    dataNovaPartida.format(FMT) + ")."
                    );
                }
            }
        }
    }

    // ================================================================
    //  VALIDAÇÕES EXISTENTES
    // ================================================================

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

    private void validarConflitoEstadio(Estadio estadio, String data, String horario) throws Exception {
        for (Partida p : partidaDAO.carregaLista()) {
            if (p.getEstadio().equals(estadio) && p.getData().equals(data) && p.getHorario().equals(horario)) {
                throw new Exception("Este estádio já tem partida nesse horário!");
            }
        }
    }

    // ================================================================
    //  UTILITÁRIO — converter String para LocalDate com mensagem clara
    // ================================================================

    private LocalDate converterData(String data) throws Exception {
        try {
            return LocalDate.parse(data.trim(), FMT);
        } catch (DateTimeParseException e) {
            throw new Exception("Data inválida: '" + data + "'. Use o formato dd/MM/yyyy.");
        }
    }

    // ================================================================
    //  DEMAIS MÉTODOS
    // ================================================================

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

    public void atualizarStatus(int numeroPartida, StatusPartida novoStatus) throws Exception {
        Partida partida = partidaDAO.buscarPorNumero(numeroPartida);
        if (partida == null) {
            throw new Exception("Partida não encontrada!");
        }
        partida.setStatus(novoStatus);
        partidaDAO.atualizar(partida);
    }

    public List<Partida> buscarPorFase(FasePartida fase) { return partidaDAO.buscarPorFase(fase); }
    public List<Partida> buscarPorData(String data)      { return partidaDAO.buscarPorData(data); }
    public List<Partida> buscarPorSelecao(Selecao s)     { return partidaDAO.buscarPorSelecao(s); }
    public List<Partida> listarPartidas()                { return partidaDAO.carregaLista(); }
}