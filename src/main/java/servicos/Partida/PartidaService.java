package servicos.Partida;

import modelo.classes.*;
import modelo.enumerations.FasePartida;
import modelo.enumerations.StatusPartida;
import persistencia.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PartidaService {

    private PartidaDAO             partidaDAO;
    private SelecaoDAO             selecaoDAO;
    private EstadioDAO             estadioDAO;
    private ArbitroDAO             arbitroDAO;
    private ClassificacaoGrupoDAO  classificacaoDAO;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PartidaService() {
        this.partidaDAO       = new PartidaDAO();
        this.selecaoDAO       = new SelecaoDAO();
        this.estadioDAO       = new EstadioDAO();
        this.arbitroDAO       = new ArbitroDAO();
        this.classificacaoDAO = new ClassificacaoGrupoDAO();
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
        if(timeCasa.qntJogadores() > 26 || timeCasa.qntJogadores() < 18 || timeVisitante.qntJogadores() > 26 || timeVisitante.qntJogadores() < 18){
            throw new Exception("As seleções precisam ter entre 18 e 26 jogadores");
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

        // --- Validação 1: mata-mata só depois que grupos terminarem ---
        if (fase != FasePartida.FASE_DE_GRUPOS) {
            validarGruposEncerrados();
        }

        // --- Validação 2: seleções ativas e elegíveis para a fase ---
        validarElegibilidade(timeCasa, fase);
        validarElegibilidade(timeVisitante, fase);

        // --- Validações de conflito ---
        LocalDate dataNovaPartida = converterData(data);
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
    //  VALIDAÇÃO 1 — Grupos encerrados antes do mata-mata
    // ================================================================

    private void validarGruposEncerrados() throws Exception {
        List<Selecao> todasSelecoes = selecaoDAO.carregaLista();

        if (todasSelecoes.isEmpty()) {
            throw new Exception("Nenhuma seleção cadastrada!");
        }

        List<ClassificacaoGrupo> grupos = classificacaoDAO.carregaLista();

        for (Selecao selecao : todasSelecoes) {
            // Busca a pontuação dessa seleção no seu grupo
            ClassificacaoGrupo grupo = classificacaoDAO.buscarPorGrupo(selecao.getGrupo());

            // Se o grupo nem existe ainda, essa seleção não jogou nenhuma partida
            if (grupo == null) {
                throw new Exception(
                        "A fase de grupos ainda não terminou! " +
                                selecao.getPais() + " (Grupo " + selecao.getGrupo() +
                                ") ainda não disputou nenhuma partida."
                );
            }

            PontuacaoSelecao pontuacao = grupo.getPontuacao(selecao);

            // Se a seleção não está na tabela ou jogou menos de 3 partidas
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
    //  VALIDAÇÃO 2 — Elegibilidade da seleção para a fase
    // ================================================================

    private void validarElegibilidade(Selecao selecao, FasePartida fase) throws Exception {

        if (fase == FasePartida.DISPUTA_DE_TERCEIRO_LUGAR) {
            // Só quem perdeu a semifinal pode jogar a disputa de 3º lugar
            if (!selecao.isPerdeuSemifinal()) {
                throw new Exception(
                        selecao.getPais() + " não perdeu a semifinal e " +
                                "não pode disputar o terceiro lugar!"
                );
            }
            // Não precisa estar ativa pois perdeu a semi mas ainda joga
            return;
        }

        // Para todas as outras fases do mata-mata, a seleção precisa estar ativa
        if (!selecao.isAtiva()) {
            throw new Exception(
                    selecao.getPais() + " já foi eliminada da competição " +
                            "e não pode ser cadastrada em novas partidas!"
            );
        }

        // Na fase de grupos, verifica se a seleção está no grupo correto
        if (fase == FasePartida.FASE_DE_GRUPOS) {
            // Sem restrição adicional além de estar ativa
            return;
        }
    }

    // ================================================================
    //  VALIDAÇÕES EXISTENTES
    // ================================================================

    private void validarOrdemCronologica(Selecao timeCasa, Selecao timeVisitante,
                                         LocalDate dataNovaPartida) throws Exception {
        for (Partida p : partidaDAO.carregaLista()) {
            boolean envolveTimeCasa = p.getTimeCasa().equals(timeCasa)
                    || p.getTimeVisitante().equals(timeCasa);
            boolean envolveTimeVisitante = p.getTimeCasa().equals(timeVisitante)
                    || p.getTimeVisitante().equals(timeVisitante);

            if (!envolveTimeCasa && !envolveTimeVisitante) continue;

            LocalDate dataExistente = converterData(p.getData());

            if (dataExistente.isAfter(dataNovaPartida)) {
                if (envolveTimeCasa) {
                    throw new Exception(
                            timeCasa.getPais() + " já tem partida agendada para " +
                                    p.getData() + " (Partida Nº " + p.getNumeroPartidas() + "). " +
                                    "Não é possível cadastrar para data anterior (" +
                                    dataNovaPartida.format(FMT) + ")."
                    );
                }
                if (envolveTimeVisitante) {
                    throw new Exception(
                            timeVisitante.getPais() + " já tem partida agendada para " +
                                    p.getData() + " (Partida Nº " + p.getNumeroPartidas() + "). " +
                                    "Não é possível cadastrar para data anterior (" +
                                    dataNovaPartida.format(FMT) + ")."
                    );
                }
            }
        }
    }

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
            if (p.getEstadio().equals(estadio) && p.getData().equals(data)
                    && p.getHorario().equals(horario)) {
                throw new Exception("Este estádio já tem partida nesse horário!");
            }
        }
    }

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
        if (partida == null) throw new Exception("Partida não encontrada!");
        partida.setStatus(novoStatus);
        partidaDAO.atualizar(partida);
    }

    public List<Partida> buscarPorFase(FasePartida fase) { return partidaDAO.buscarPorFase(fase); }
    public List<Partida> buscarPorData(String data)      { return partidaDAO.buscarPorData(data); }
    public List<Partida> buscarPorSelecao(Selecao s)     { return partidaDAO.buscarPorSelecao(s); }
    public List<Partida> listarPartidas()                { return partidaDAO.carregaLista(); }
}