package servicos.Partida;

import modelo.classes.*;
import persistencia.ClassificacaoGrupoDAO;
import persistencia.ResultadoPartidaDAO;
import persistencia.SelecaoDAO;

import java.util.ArrayList;
import java.util.List;

public class ClassificacaoService {

    private ClassificacaoGrupoDAO classificacaoDAO;
    private ResultadoPartidaDAO   resultadoDAO;

    public ClassificacaoService() {
        this.classificacaoDAO = new ClassificacaoGrupoDAO();
        this.resultadoDAO     = new ResultadoPartidaDAO();
    }

    // ---------------------------------------------------------------
    // Chamado toda vez que um ResultadoPartida é registrado
    // Atualiza automaticamente a pontuação do grupo
    // ---------------------------------------------------------------
    public void processarResultado(ResultadoPartida resultado) throws Exception {
        Partida partida = resultado.getPartida();

        // Só processa partidas da fase de grupos
        if (partida.getFase() != modelo.enumerations.FasePartida.FASE_DE_GRUPOS) return;

        Selecao timeCasa      = partida.getTimeCasa();
        Selecao timeVisitante = partida.getTimeVisitante();

        // Ambos devem estar no mesmo grupo
        if (timeCasa.getGrupo() != timeVisitante.getGrupo()) {
            throw new Exception("Times de grupos diferentes não podem se enfrentar na fase de grupos!");
        }

        String grupo = timeCasa.getGrupo();

        // Busca ou cria a classificação do grupo
        ClassificacaoGrupo classificacao = classificacaoDAO.buscarPorGrupo(grupo);
        if (classificacao == null) {
            classificacao = new ClassificacaoGrupo(grupo);
        }

        // Garante que as duas seleções estão no grupo
        classificacao.adicionarSelecao(timeCasa);
        classificacao.adicionarSelecao(timeVisitante);

        // Extrai o placar para registrar gols
        // Placar no formato "2x1" — casa x visitante
        int[] gols = extrairGols(resultado.getPlacar());
        int golsCasa      = gols[0];
        int golsVisitante = gols[1];

        PontuacaoSelecao ptsCasa      = classificacao.getPontuacao(timeCasa);
        PontuacaoSelecao ptsVisitante = classificacao.getPontuacao(timeVisitante);

        if (golsCasa > golsVisitante) {
            // Time da casa venceu
            ptsCasa.registrarVitoria(golsCasa, golsVisitante);
            ptsVisitante.registrarDerrota(golsVisitante, golsCasa);

        } else if (golsVisitante > golsCasa) {
            // Time visitante venceu
            ptsVisitante.registrarVitoria(golsVisitante, golsCasa);
            ptsCasa.registrarDerrota(golsCasa, golsVisitante);

        } else {
            // Empate
            ptsCasa.registrarEmpate(golsCasa, golsVisitante);
            ptsVisitante.registrarEmpate(golsVisitante, golsCasa);
        }

        classificacaoDAO.salvarGrupo(classificacao);
    }

    // ---------------------------------------------------------------
    // Retorna a tabela de classificação de um grupo
    // ---------------------------------------------------------------
    public ClassificacaoGrupo getClassificacaoGrupo(char grupo) {
        return classificacaoDAO.buscarPorGrupo(String.valueOf(grupo));
    }

    // ---------------------------------------------------------------
    // Retorna todos os grupos
    // ---------------------------------------------------------------
    public List<ClassificacaoGrupo> getTodosOsGrupos() {
        return classificacaoDAO.carregaLista();
    }

    // ---------------------------------------------------------------
    // Verifica se todos os grupos terminaram e retorna as classificadas
    // ---------------------------------------------------------------
    public List<Selecao> getClassificadasOitavas() throws Exception {
        List<ClassificacaoGrupo> grupos = classificacaoDAO.carregaLista();

        if (grupos.size() < 8) {
            throw new Exception("Nem todos os grupos foram iniciados ainda!");
        }

        List<Selecao> classificadas = new ArrayList<>();
        for (ClassificacaoGrupo g : grupos) {
            if (!g.todasJogaramTresPartidas()) {
                throw new Exception("Grupo " + g.getGrupo() +
                        " ainda não terminou a fase de grupos!");
            }
            classificadas.addAll(g.getClassificadas());
        }

        return classificadas; // 16 seleções classificadas
    }

    // ---------------------------------------------------------------
    // Extrai os gols do placar no formato "2x1"
    // ---------------------------------------------------------------
    private int[] extrairGols(String placar) throws Exception {
        if (placar == null || !placar.contains("x")) {
            throw new Exception("Placar inválido: '" + placar +
                    "'. Use o formato 'golsCasa x golsVisitante' (ex: 2x1).");
        }
        try {
            String[] partes = placar.toLowerCase().split("x");
            return new int[]{
                    Integer.parseInt(partes[0].trim()),
                    Integer.parseInt(partes[1].trim())
            };
        } catch (NumberFormatException e) {
            throw new Exception("Placar inválido: '" + placar + "'. Os gols devem ser números.");
        }
    }

    // Adicione no ClassificacaoService existente:

    // Marca o perdedor como eliminado após resultado do mata-mata
    public void processarEliminacao(ResultadoPartida resultado, SelecaoDAO selecaoDAO) throws Exception {
        Partida partida = resultado.getPartida();
        modelo.enumerations.FasePartida fase = partida.getFase();

        // Fase de grupos não elimina ninguém aqui — isso é feito pela classificação
        if (fase == modelo.enumerations.FasePartida.FASE_DE_GRUPOS) return;

        Selecao perdedor = resultado.getTimePerdedor();
        Selecao vencedor = resultado.getTimeVencedor();

        if (fase == modelo.enumerations.FasePartida.SEMIFINAL) {
            // Perdedor da semi vai para disputa de 3º lugar — não elimina ainda
            perdedor.setPerdeuSemifinal(true);
            selecaoDAO.atualizaSelecao(perdedor);

        } else if (fase == modelo.enumerations.FasePartida.DISPUTA_DE_TERCEIRO_LUGAR) {
            // Perdedor da disputa de 3º é eliminado
            perdedor.eliminar();
            selecaoDAO.atualizaSelecao(perdedor);

        } else {
            // Oitavas, quartas, final — perdedor é eliminado direto
            perdedor.eliminar();
            selecaoDAO.atualizaSelecao(perdedor);
        }
    }

    // Elimina as seleções que não se classificaram após a fase de grupos
    public void processarEliminacaoGrupos(SelecaoDAO selecaoDAO) throws Exception {
        List<ClassificacaoGrupo> grupos = classificacaoDAO.carregaLista();

        for (ClassificacaoGrupo g : grupos) {
            if (!g.todasJogaramTresPartidas()) {
                throw new Exception("Grupo " + g.getGrupo() + " ainda não terminou!");
            }

            List<PontuacaoSelecao> tabela = g.getClassificacao();

            // 3ª e 4ª colocadas são eliminadas
            for (int i = 2; i < tabela.size(); i++) {
                Selecao eliminada = tabela.get(i).getSelecao();
                eliminada.eliminar();
                selecaoDAO.atualizaSelecao(eliminada);
            }
        }
    }
}