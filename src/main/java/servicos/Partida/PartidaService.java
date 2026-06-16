package servicos.Partida;

import modelo.classes.*;
import modelo.enumerations.FasePartida;
import modelo.enumerations.StatusPartida;
import persistencia.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PartidaService {

    private PartidaDAO            partidaDAO;
    private SelecaoDAO            selecaoDAO;
    private EstadioDAO            estadioDAO;
    private ArbitroDAO            arbitroDAO;
    private ClassificacaoGrupoDAO classificacaoDAO;

    private static final DateTimeFormatter FMT      = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // Duração de uma partida: 2 tempos de 45min + 15min de intervalo = 1h45min
    private static final int DURACAO_PARTIDA_MINUTOS = 105;

    public PartidaService() {
        this.partidaDAO       = new PartidaDAO();
        this.selecaoDAO       = new SelecaoDAO();
        this.estadioDAO       = new EstadioDAO();
        this.arbitroDAO       = new ArbitroDAO();
        this.classificacaoDAO = new ClassificacaoGrupoDAO();

        int maiorNumero = this.partidaDAO.carregarMaiorNumero();
        if (Partida.getContPartidas() < maiorNumero) {
            Partida.setContPartidas(maiorNumero);
        }
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
        if (!horario.trim().matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            throw new Exception("Horário inválido! Use o formato hh:mm (ex: 16:00).");
        }

        // --- Validação: mata-mata só depois que grupos terminarem ---
        if (fase != FasePartida.FASE_DE_GRUPOS) {
            validarGruposEncerrados();
        }

        // --- Validação: elegibilidade ---
        validarElegibilidade(timeCasa, fase);
        validarElegibilidade(timeVisitante, fase);

        // --- Validação: grupos distintos não jogam entre si na fase de grupos ---
        if (fase == FasePartida.FASE_DE_GRUPOS) {
            validarMesmoGrupo(timeCasa, timeVisitante);
        }

        // --- Validação: limite de confrontos entre os dois times ---
        validarConfrontosDiretos(timeCasa, timeVisitante, fase);

        // --- Validações de conflito de horário e estádio ---
        validarConflitoHorario(timeCasa, timeVisitante, data, horario);
        validarConflitoEstadio(estadio, data, horario);

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
    //  NOVA — Seleções do mesmo grupo na fase de grupos
    // ================================================================

    private void validarMesmoGrupo(Selecao timeCasa, Selecao timeVisitante) throws Exception {
        if (!timeCasa.getGrupo().equals(timeVisitante.getGrupo())) {
            throw new Exception(
                    timeCasa.getPais() + " (Grupo " + timeCasa.getGrupo() + ") e " +
                            timeVisitante.getPais() + " (Grupo " + timeVisitante.getGrupo() + ") " +
                            "são de grupos diferentes e não podem se enfrentar na fase de grupos!"
            );
        }
    }

    // ================================================================
    //  NOVA — Limite de confrontos diretos entre dois times
    // ================================================================

    // Na fase de grupos: os dois times só podem se enfrentar 1 vez
    // Fora da fase de grupos: os dois times também só podem se enfrentar 1 vez
    // (em fases diferentes conta separado)
    private void validarConfrontosDiretos(Selecao timeCasa, Selecao timeVisitante,
                                          FasePartida fase) throws Exception {
        boolean esFaseGrupos = fase == FasePartida.FASE_DE_GRUPOS;
        int confrontos = 0;

        for (Partida p : partidaDAO.carregaLista()) {
            boolean mesmoConfrontoA = p.getTimeCasa().equals(timeCasa)
                    && p.getTimeVisitante().equals(timeVisitante);
            boolean mesmoConfrontoB = p.getTimeCasa().equals(timeVisitante)
                    && p.getTimeVisitante().equals(timeCasa);

            if (!mesmoConfrontoA && !mesmoConfrontoB) continue;

            boolean confrontoEhFaseGrupos = p.getFase() == FasePartida.FASE_DE_GRUPOS;

            // Conta só confrontos da mesma "categoria" (grupos vs mata-mata)
            if (esFaseGrupos == confrontoEhFaseGrupos) {
                confrontos++;
            }
        }

        if (confrontos >= 1) {
            String categoria = esFaseGrupos ? "fase de grupos" : "mata-mata";
            throw new Exception(
                    timeCasa.getPais() + " e " + timeVisitante.getPais() +
                            " já se enfrentaram na " + categoria + ". " +
                            "Dois times só podem se enfrentar uma vez por fase!"
            );
        }
    }

    // ================================================================
    //  ATUALIZADA — Conflito de horário com margem de 1h45min
    // ================================================================

    private void validarConflitoHorario(Selecao timeCasa, Selecao timeVisitante,
                                        String data, String horario) throws Exception {

        LocalTime horaNovaPartida = LocalTime.parse(horario.trim(), FMT_HORA);

        for (Partida p : partidaDAO.carregaLista()) {

            // Só verifica partidas no mesmo dia
            if (!p.getData().equals(data)) continue;

            boolean envolveTimeCasa = p.getTimeCasa().equals(timeCasa)
                    || p.getTimeVisitante().equals(timeCasa);
            boolean envolveTimeVisitante = p.getTimeCasa().equals(timeVisitante)
                    || p.getTimeVisitante().equals(timeVisitante);

            if (!envolveTimeCasa && !envolveTimeVisitante) continue;

            LocalTime horaExistente = LocalTime.parse(p.getHorario().trim(), FMT_HORA);

            // Calcula a diferença em minutos entre os dois horários
            long diferencaMinutos = Math.abs(
                    horaNovaPartida.toSecondOfDay() - horaExistente.toSecondOfDay()
            ) / 60;

            // Se a diferença for menor que 1h45min, há conflito
            if (diferencaMinutos < DURACAO_PARTIDA_MINUTOS) {
                String timeConflitante = envolveTimeCasa
                        ? timeCasa.getPais()
                        : timeVisitante.getPais();

                throw new Exception(
                        timeConflitante + " já tem uma partida às " + p.getHorario() +
                                " neste dia. É necessário um intervalo mínimo de 1h45min entre partidas " +
                                "(horário solicitado: " + horario + ")."
                );
            }
        }
    }

    // ================================================================
    //  ATUALIZADA — Conflito de estádio com margem de 1h45min
    // ================================================================

    private void validarConflitoEstadio(Estadio estadio, String data, String horario) throws Exception {
        LocalTime horaNovaPartida = LocalTime.parse(horario.trim(), FMT_HORA);

        for (Partida p : partidaDAO.carregaLista()) {
            if (!p.getEstadio().equals(estadio)) continue;
            if (!p.getData().equals(data)) continue;

            LocalTime horaExistente = LocalTime.parse(p.getHorario().trim(), FMT_HORA);

            long diferencaMinutos = Math.abs(
                    horaNovaPartida.toSecondOfDay() - horaExistente.toSecondOfDay()
            ) / 60;

            if (diferencaMinutos < DURACAO_PARTIDA_MINUTOS) {
                throw new Exception(
                        "O estádio " + estadio.getNome() + " já tem uma partida às " +
                                p.getHorario() + " neste dia. " +
                                "É necessário um intervalo mínimo de 1h45min entre partidas " +
                                "(horário solicitado: " + horario + ")."
                );
            }
        }
    }

    // ================================================================
    //  VALIDAÇÃO — Grupos encerrados antes do mata-mata
    // ================================================================

    private void validarGruposEncerrados() throws Exception {
        List<Selecao> todasSelecoes = selecaoDAO.carregaLista();

        if (todasSelecoes.isEmpty()) {
            throw new Exception("Nenhuma seleção cadastrada!");
        }

        for (Selecao selecao : todasSelecoes) {
            ClassificacaoGrupo grupo = classificacaoDAO.buscarPorGrupo(selecao.getGrupo());

            if (grupo == null) {
                throw new Exception(
                        "A fase de grupos ainda não terminou! " +
                                selecao.getPais() + " (Grupo " + selecao.getGrupo() +
                                ") ainda não disputou nenhuma partida."
                );
            }

            PontuacaoSelecao pontuacao = grupo.getPontuacao(selecao);

            if (pontuacao == null || pontuacao.getPartidasJogadas() < 3) {
                int jogadas = pontuacao == null ? 0 : pontuacao.getPartidasJogadas();
                throw new Exception(
                        "A fase de grupos ainda não terminou! " +
                                selecao.getPais() + " (Grupo " + selecao.getGrupo() +
                                ") jogou apenas " + jogadas + " de 3 partidas."
                );
            }
        }
    }

    // ================================================================
    //  VALIDAÇÃO — Elegibilidade
    // ================================================================

    private void validarElegibilidade(Selecao selecao, FasePartida fase) throws Exception {
        if (fase == FasePartida.FASE_DE_GRUPOS) return;

        if (fase == FasePartida.DISPUTA_DE_TERCEIRO_LUGAR) {
            if (!selecao.isPerdeuSemifinal()) {
                throw new Exception(
                        selecao.getPais() + " não perdeu a semifinal e " +
                                "não pode disputar o terceiro lugar!"
                );
            }
            return;
        }

        if (!selecao.isAtiva()) {
            throw new Exception(
                    selecao.getPais() + " já foi eliminada da competição " +
                            "e não pode ser cadastrada em novas partidas!"
            );
        }
    }

    // ================================================================
    //  DEMAIS MÉTODOS
    // ================================================================

    private LocalDate converterData(String data) throws Exception {
        try {
            return LocalDate.parse(data.trim(), FMT);
        } catch (DateTimeParseException e) {
            throw new Exception("Data inválida: '" + data + "'. Use o formato dd/MM/yyyy.");
        }
    }

    public void removerPartida(int numero) throws Exception {
        if (partidaDAO.buscarPorNumero(numero) == null) {
            throw new Exception("Partida número " + numero + " não encontrada!");
        }
        partidaDAO.remover(numero);
        List<Partida> listaAtual = partidaDAO.carregaLista();
        if (listaAtual.isEmpty()) {
            Partida.setContPartidas(0);
        }
    }



    public void atualizarPartida(Partida partida) throws Exception {
        if (partidaDAO.buscarPorNumero(partida.getNumeroPartidas()) == null) {
            throw new Exception("Partida não encontrada para atualização!");
        }
        partidaDAO.atualizar(partida);
    }

    public void atualizarStatus(int numeroPartida, StatusPartida novoStatus) throws Exception {
        Partida partida = partidaDAO.buscarPorNumero(numeroPartida);
        if (partida == null) throw new Exception("Partida não encontrada!");
        partida.setStatus(novoStatus);
        partidaDAO.atualizar(partida);
    }

    public List<Partida> buscarPorFase(FasePartida fase) { return partidaDAO.buscarPorFase(fase); }
    public List<Partida> buscarPorData(String data)      { return partidaDAO.buscarPorData(data); }
    public List<Partida> buscarPorSelecao(Selecao s)     { return partidaDAO.buscarPorSelecao(s); }
    public List<Partida> listarPartidas()                { return partidaDAO.carregaLista(); }
}