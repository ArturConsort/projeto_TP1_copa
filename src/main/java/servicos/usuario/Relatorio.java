package servicos.usuario;

import modelo.classes.Partida;
import modelo.classes.ResultadoPartida;
import modelo.classes.Usuario;
import modelo.enumerations.FasePartida;
import modelo.enumerations.TipoPerfil;
import persistencia.PartidaDAO;
import persistencia.ResultadoPartidaDAO;
import persistencia.UsuarioDAO;

import java.util.List;

public class Relatorio {

    private UsuarioDAO usuarioDAO;
    private PartidaDAO partidaDAO;
    private ResultadoPartidaDAO resultadoDAO;


    public Relatorio() {
        this.usuarioDAO  = new UsuarioDAO();
        this.partidaDAO  = new PartidaDAO();
        this.resultadoDAO = new ResultadoPartidaDAO();
    }



    // ================================================================
    //   RELATÓRIO DE USUÁRIOS
    // ================================================================

    public void exibirRelatorioUsuarios(){

        List<Usuario> todosUsuarios = usuarioDAO.carregaLista();

        long administrador = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.ADMINISTRADOR).count();
        long organizador = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.ORGANIZADOR).count();
        long operador = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.OPERADOR).count();
        long arbitro = todosUsuarios.stream().filter(u -> u.getPerfil() == TipoPerfil.ARBITRO).count();

        System.out.println("=== RELATÓRIO de USUÁRIOS ===");
        System.out.println("Quantidade total de usuários: " + todosUsuarios.size());
        System.out.println("Administradores: " + administrador);
        System.out.println("Organizadores: " + organizador);
        System.out.println("Operadores: " + operador);
        System.out.println("Árbitros: " + arbitro);

    }


    // ================================================================
    //   RELATÓRIO GERAL — PARTIDAS E RESULTADOS
    // ================================================================

    public void exibirRelatorioPartidas() {
        List<Partida>         partidas   = partidaDAO.carregaLista();
        List<ResultadoPartida> resultados = resultadoDAO.carregaLista();

        exibirSecaoPartidas(partidas, resultados);
        System.out.println();
        exibirSecaoResultados(resultados);
    }


    // ----------------------------------------------------------------
    //   Seção 1 — Partidas
    // ----------------------------------------------------------------

    private void exibirSecaoPartidas(List<Partida> partidas, List<ResultadoPartida> resultados) {
        int totalPartidas    = partidas.size();
        int totalFinalizadas = resultados.size();
        int totalPendentes   = totalPartidas; // partidas pendentes ficam em partidas.dat

        System.out.println("=== RELATÓRIO GERAL ===");
        System.out.println();
        System.out.println("--- Partidas ---");
        System.out.println("Pendentes:          " + totalPendentes);
        System.out.println("Finalizadas:        " + totalFinalizadas);
        System.out.println("Total:              " + (totalPendentes + totalFinalizadas));
        System.out.println();

        // Partidas por fase (considera pendentes + finalizadas)
        System.out.println("Por fase:");
        for (FasePartida fase : FasePartida.values()) {
            long qtdPendentes = partidas.stream()
                    .filter(p -> p.getFase() == fase)
                    .count();
            long qtdFinalizadas = resultados.stream()
                    .filter(r -> r.getPartida().getFase() == fase)
                    .count();
            long total = qtdPendentes + qtdFinalizadas;
            if (total > 0) {
                System.out.printf("  %-30s %d%n", fase.name().replace("_", " "), total);
            }
        }

        // Lista partidas pendentes
        if (!partidas.isEmpty()) {
            System.out.println();
            System.out.println("Partidas pendentes:");
            partidas.forEach(p -> System.out.printf(
                    "  Nº %-3d | %-15s x %-15s | %s %s | %s%n",
                    p.getNumeroPartidas(),
                    p.getTimeCasa()      != null ? p.getTimeCasa().getPais()      : "-",
                    p.getTimeVisitante() != null ? p.getTimeVisitante().getPais() : "-",
                    p.getData(),
                    p.getHorario(),
                    p.getFase().name().replace("_", " ")
            ));
        }
    }


    // ----------------------------------------------------------------
    //   Seção 2 — Resultados
    // ----------------------------------------------------------------

    private void exibirSecaoResultados(List<ResultadoPartida> resultados) {
        if (resultados.isEmpty()) {
            System.out.println("--- Resultados ---");
            System.out.println("Nenhum resultado registrado ainda.");
            return;
        }

        int totalAmarelos  = resultados.stream().mapToInt(ResultadoPartida::getCartoesAmarelos).sum();
        int totalVermelhos = resultados.stream().mapToInt(ResultadoPartida::getCartoesVermelhos).sum();

        System.out.println("--- Resultados ---");
        System.out.println("Total de cartões amarelos:  " + totalAmarelos);
        System.out.println("Total de cartões vermelhos: " + totalVermelhos);
        System.out.println();

        System.out.println("Resultados registrados:");
        resultados.forEach(r -> {
            Partida p = r.getPartida();
            String penaltis = (r.getPlacarPenaltis() != null && !r.getPlacarPenaltis().isBlank())
                    ? " (pênaltis: " + r.getPlacarPenaltis() + ")"
                    : "";

            System.out.printf(
                    "  Nº %-3d | %-15s x %-15s | Placar: %-8s%s | Amarelos: %d | Vermelhos: %d%n",
                    p.getNumeroPartidas(),
                    r.getTimeVencedor() != null ? r.getTimeVencedor().getPais() : "-",
                    r.getTimePerdedor() != null ? r.getTimePerdedor().getPais() : "-",
                    r.getPlacar(),
                    penaltis,
                    r.getCartoesAmarelos(),
                    r.getCartoesVermelhos()
            );
        });
    }



}
