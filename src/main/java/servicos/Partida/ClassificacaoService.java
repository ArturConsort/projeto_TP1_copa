package servicos.Partida;

import modelo.classes.*;
import modelo.enumerations.FasePartida;
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
    // Atualiza pontuação do grupo ao registrar resultado
    // ---------------------------------------------------------------
    public void processarResultado(ResultadoPartida resultado) throws Exception {
        Partida partida = resultado.getPartida();

        if (partida.getFase() != FasePartida.FASE_DE_GRUPOS) return;

        Selecao timeCasa      = partida.getTimeCasa();
        Selecao timeVisitante = partida.getTimeVisitante();
        String  grupo         = timeCasa.getGrupo();

        ClassificacaoGrupo classificacao = classificacaoDAO.buscarPorGrupo(grupo);
        if (classificacao == null) classificacao = new ClassificacaoGrupo(grupo);

        classificacao.adicionarSelecao(timeCasa);
        classificacao.adicionarSelecao(timeVisitante);

        int[] gols        = extrairGols(resultado.getPlacar());
        int golsCasa      = gols[0];
        int golsVisitante = gols[1];

        PontuacaoSelecao ptsCasa      = classificacao.getPontuacao(timeCasa);
        PontuacaoSelecao ptsVisitante = classificacao.getPontuacao(timeVisitante);

        if (golsCasa > golsVisitante) {
            ptsCasa.registrarVitoria(golsCasa, golsVisitante);
            ptsVisitante.registrarDerrota(golsVisitante, golsCasa);
        } else if (golsVisitante > golsCasa) {
            ptsVisitante.registrarVitoria(golsVisitante, golsCasa);
            ptsCasa.registrarDerrota(golsCasa, golsVisitante);
        } else {
            ptsCasa.registrarEmpate(golsCasa, golsVisitante);
            ptsVisitante.registrarEmpate(golsVisitante, golsCasa);
        }

        classificacaoDAO.salvarGrupo(classificacao);
    }

    // ---------------------------------------------------------------
    // Processa eliminação no mata-mata
    // ---------------------------------------------------------------
    public void processarEliminacao(ResultadoPartida resultado, SelecaoDAO selecaoDAO) throws Exception {
        Partida    partida = resultado.getPartida();
        FasePartida fase   = partida.getFase();

        if (fase == FasePartida.FASE_DE_GRUPOS) return;

        // Busca sempre a versão mais recente do arquivo
        Selecao perdedor = selecaoDAO.buscarPorPais(resultado.getTimePerdedor().getPais());
        if (perdedor == null) return;

        if (fase == FasePartida.SEMIFINAL) {
            // Perdedor da semi vai para disputa de 3º — não elimina ainda
            perdedor.setPerdeuSemifinal(true);
            selecaoDAO.atualizaSelecao(perdedor);

        } else if (fase == FasePartida.DISPUTA_DE_TERCEIRO_LUGAR) {
            perdedor.eliminar();
            selecaoDAO.atualizaSelecao(perdedor);

        } else {
            // Oitavas, quartas, final
            perdedor.eliminar();
            selecaoDAO.atualizaSelecao(perdedor);
        }
    }

    // ---------------------------------------------------------------
    // Elimina 3ª e 4ª de cada grupo após todos terminarem
    // ---------------------------------------------------------------
    public void processarEliminacaoGrupos(SelecaoDAO selecaoDAO) throws Exception {
        List<Selecao> todasSelecoes = selecaoDAO.carregaLista();

        // Verifica se TODAS as seleções já jogaram 3 partidas antes de eliminar qualquer uma
        for (Selecao selecao : todasSelecoes) {
            ClassificacaoGrupo grupo = classificacaoDAO.buscarPorGrupo(selecao.getGrupo());
            if (grupo == null) throw new Exception("Nem todos os grupos terminaram.");

            PontuacaoSelecao pts = grupo.getPontuacao(selecao);
            if (pts == null || pts.getPartidasJogadas() < 3) {
                throw new Exception("Nem todos os grupos terminaram.");
            }
        }

        // Só chega aqui se TODOS jogaram 3 — elimina 3ª e 4ª de cada grupo
        List<ClassificacaoGrupo> grupos = classificacaoDAO.carregaLista();
        for (ClassificacaoGrupo g : grupos) {
            List<PontuacaoSelecao> tabela = g.getClassificacao();
            for (int i = 2; i < tabela.size(); i++) {
                // Busca a versão mais recente do arquivo — evita usar cópia antiga
                Selecao doArquivo = selecaoDAO.buscarPorPais(
                        tabela.get(i).getSelecao().getPais()
                );
                if (doArquivo != null && doArquivo.isAtiva()) {
                    doArquivo.eliminar();
                    selecaoDAO.atualizaSelecao(doArquivo);
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------------
    public ClassificacaoGrupo getClassificacaoGrupo(String grupo) {
        return classificacaoDAO.buscarPorGrupo(grupo);
    }

    public List<ClassificacaoGrupo> getTodosOsGrupos() {
        return classificacaoDAO.carregaLista();
    }

    public List<Selecao> getClassificadasOitavas() throws Exception {
        List<ClassificacaoGrupo> grupos = classificacaoDAO.carregaLista();

        if (grupos.size() < 8) {
            throw new Exception("Nem todos os grupos foram iniciados ainda!");
        }

        List<Selecao> classificadas = new ArrayList<>();
        for (ClassificacaoGrupo g : grupos) {
            if (!g.todasJogaramTresPartidas()) {
                throw new Exception("Grupo " + g.getGrupo() + " ainda não terminou!");
            }
            classificadas.addAll(g.getClassificadas());
        }

        return classificadas;
    }

    // ---------------------------------------------------------------
    // Utilitário
    // ---------------------------------------------------------------
    private int[] extrairGols(String placar) throws Exception {
        if (placar == null || !placar.contains("x")) {
            throw new Exception("Placar inválido: '" + placar + "'. Use o formato '2x1'.");
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
}