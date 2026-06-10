package controllersJavaFX;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelo.classes.Partida;
import modelo.classes.ResultadoPartida;
import modelo.classes.Usuario;
import modelo.enumerations.FasePartida;
import modelo.enumerations.TipoPerfil;
import persistencia.PartidaDAO;
import persistencia.ResultadoPartidaDAO;
import persistencia.UsuarioDAO;
import servicos.usuario.SessaoUsuario;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatoriosController {

    @FXML private Label    labelUsuarioLogado;
    @FXML private Label    labelTitulo;
    @FXML private TextArea terminalOutput;
    @FXML private Button   btnUsuarios;
    @FXML private Button   btnPartidas;

    private final UsuarioDAO         usuarioDAO  = new UsuarioDAO();
    private final PartidaDAO         partidaDAO  = new PartidaDAO();
    private final ResultadoPartidaDAO resultadoDAO = new ResultadoPartidaDAO();

    // ════════════════════════════════════════════════════════
    //   INICIALIZAÇÃO
    // ════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado != null)
            labelUsuarioLogado.setText(logado.getNome() + " · " + logado.getPerfil());

        // Abre já com o relatório de usuários
        handleRelatorioUsuarios();
    }

    // ════════════════════════════════════════════════════════
    //   RELATÓRIO DE USUÁRIOS
    // ════════════════════════════════════════════════════════

    @FXML
    private void handleRelatorioUsuarios() {
        marcarAtivo(btnUsuarios, btnPartidas);
        labelTitulo.setText("relatório · usuários");

        List<Usuario> todos = usuarioDAO.carregaLista();

        long adm  = todos.stream().filter(u -> u.getPerfil() == TipoPerfil.ADMINISTRADOR).count();
        long org  = todos.stream().filter(u -> u.getPerfil() == TipoPerfil.ORGANIZADOR).count();
        long op   = todos.stream().filter(u -> u.getPerfil() == TipoPerfil.OPERADOR).count();
        long arb  = todos.stream().filter(u -> u.getPerfil() == TipoPerfil.ARBITRO).count();

        StringBuilder sb = new StringBuilder();
        String linha = "=".repeat(50);
        String sublinha = "-".repeat(50);

        sb.append(linha).append("\n");
        sb.append("RELATÓRIO DE USUÁRIOS\n");
        sb.append("Gerado em: ").append(agora()).append("\n");
        sb.append(linha).append("\n");
        sb.append("\n");

        // Resumo por perfil
        sb.append("RESUMO POR PERFIL\n");
        sb.append(sublinha).append("\n");
        sb.append(String.format("  %-20s %s\n", "Perfil", "Quantidade"));
        sb.append(sublinha).append("\n");
        sb.append(String.format("  %-20s %d\n", "ADMINISTRADOR", adm));
        sb.append(String.format("  %-20s %d\n", "ORGANIZADOR",   org));
        sb.append(String.format("  %-20s %d\n", "OPERADOR",      op));
        sb.append(String.format("  %-20s %d\n", "ÁRBITRO",       arb));
        sb.append(sublinha).append("\n");
        sb.append(String.format("  %-20s %d\n", "TOTAL", todos.size()));
        sb.append("\n");

        // Lista completa agrupada por perfil
        sb.append("LISTA COMPLETA\n");
        sb.append(sublinha).append("\n");

        for (TipoPerfil perfil : TipoPerfil.values()) {
            List<Usuario> grupo = todos.stream()
                    .filter(u -> u.getPerfil() == perfil)
                    .toList();

            if (grupo.isEmpty()) continue;

            sb.append("\n  › ").append(perfil.name()).append("\n");
            for (Usuario u : grupo) {
                sb.append(String.format(
                        "    · %-20s  login: %-12s  país: %s\n",
                        u.getNome(), u.getLogin(), u.getPais()
                ));
            }
        }

        sb.append("\n").append(sublinha).append("\n");

        terminalOutput.setText(sb.toString());
        terminalOutput.positionCaret(0);
    }

    // ════════════════════════════════════════════════════════
    //   RELATÓRIO DE PARTIDAS
    // ════════════════════════════════════════════════════════

    @FXML
    private void handleRelatorioPartidas() {
        marcarAtivo(btnPartidas, btnUsuarios);
        labelTitulo.setText("relatório · partidas");

        List<Partida>          partidas   = partidaDAO.carregaLista();
        List<ResultadoPartida> resultados = resultadoDAO.carregaLista();

        StringBuilder sb = new StringBuilder();
        String linha    = "=".repeat(50);
        String sublinha = "-".repeat(50);

        sb.append(linha).append("\n");
        sb.append("RELATÓRIO DE PARTIDAS\n");
        sb.append("Gerado em: ").append(agora()).append("\n");
        sb.append(linha).append("\n");
        sb.append("\n");

        // ── Resumo geral ──────────────────────────────────
        int totalPendentes   = partidas.size();
        int totalFinalizadas = resultados.size();

        sb.append("RESUMO GERAL\n");
        sb.append(sublinha).append("\n");
        sb.append(String.format("  %-25s %d\n", "Partidas pendentes",   totalPendentes));
        sb.append(String.format("  %-25s %d\n", "Partidas finalizadas", totalFinalizadas));
        sb.append(String.format("  %-25s %d\n", "Total",                totalPendentes + totalFinalizadas));
        sb.append("\n");

        // ── Por fase ──────────────────────────────────────
        sb.append("POR FASE\n");
        sb.append(sublinha).append("\n");

        for (FasePartida fase : FasePartida.values()) {
            long qtdPend = partidas.stream()
                    .filter(p -> p.getFase() == fase).count();
            long qtdFin  = resultados.stream()
                    .filter(r -> r.getPartida().getFase() == fase).count();
            long total   = qtdPend + qtdFin;
            if (total > 0) {
                sb.append(String.format(
                        "  %-32s %d  (pendentes: %d | finalizadas: %d)\n",
                        faseLegivel(fase), total, qtdPend, qtdFin
                ));
            }
        }

        sb.append("\n");

        // ── Partidas pendentes ────────────────────────────
        if (!partidas.isEmpty()) {
            sb.append("PARTIDAS PENDENTES\n");
            sb.append(sublinha).append("\n");
            for (Partida p : partidas) {
                sb.append(String.format(
                        "  Nº %-4d  %-16s x  %-16s  %s %s  [%s]\n",
                        p.getNumeroPartidas(),
                        p.getTimeCasa()      != null ? p.getTimeCasa().getPais()      : "-",
                        p.getTimeVisitante() != null ? p.getTimeVisitante().getPais() : "-",
                        p.getData(),
                        p.getHorario(),
                        faseLegivel(p.getFase())
                ));
            }
            sb.append("\n");
        }

        // ── Resultados registrados ────────────────────────
        if (!resultados.isEmpty()) {
            int totalAmarelos  = resultados.stream().mapToInt(ResultadoPartida::getCartoesAmarelos).sum();
            int totalVermelhos = resultados.stream().mapToInt(ResultadoPartida::getCartoesVermelhos).sum();

            sb.append("RESULTADOS REGISTRADOS\n");
            sb.append(sublinha).append("\n");
            sb.append(String.format("  %-28s %d\n", "Total cartões amarelos:",  totalAmarelos));
            sb.append(String.format("  %-28s %d\n", "Total cartões vermelhos:", totalVermelhos));
            sb.append("\n");

            for (ResultadoPartida r : resultados) {
                Partida p = r.getPartida();
                String penaltis = (r.getPlacarPenaltis() != null && !r.getPlacarPenaltis().isBlank())
                        ? "  pen: " + r.getPlacarPenaltis() : "";

                sb.append(String.format(
                        "  Nº %-4d  %-16s x  %-16s  %s%s  🟨 %d  🟥 %d\n",
                        p.getNumeroPartidas(),
                        r.getTimeVencedor() != null ? r.getTimeVencedor().getPais() : "-",
                        r.getTimePerdedor() != null ? r.getTimePerdedor().getPais() : "-",
                        r.getPlacar(),
                        penaltis,
                        r.getCartoesAmarelos(),
                        r.getCartoesVermelhos()
                ));
            }
            sb.append("\n");
        } else {
            sb.append("RESULTADOS REGISTRADOS\n");
            sb.append(sublinha).append("\n");
            sb.append("  Nenhum resultado registrado ainda.\n\n");
        }

        sb.append(sublinha).append("\n");

        terminalOutput.setText(sb.toString());
        terminalOutput.positionCaret(0);
    }

    // ════════════════════════════════════════════════════════
    //   LIMPAR
    // ════════════════════════════════════════════════════════

    @FXML
    private void handleLimpar() {
        terminalOutput.clear();
    }

    // ════════════════════════════════════════════════════════
//   GERAR ARQUIVO .TXT
// ════════════════════════════════════════════════════════

    @FXML
    private void handleGerarArquivo() {
        String conteudo = terminalOutput.getText();
        if (conteudo == null || conteudo.isBlank()) {
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING,
                    "Não há relatório gerado para salvar.",
                    javafx.scene.control.ButtonType.OK
            ).showAndWait();
            return;
        }

        String nomeArquivo = "relatorio_"
                + labelTitulo.getText().replace(" · ", "_").replace(" ", "_")
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".txt";

        // Salva direto na pasta raiz do projeto (onde ficam os .dat)
        File arquivo = new File(System.getProperty("user.dir"), nomeArquivo);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write(conteudo);
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Arquivo salvo em:\n" + arquivo.getAbsolutePath(),
                    javafx.scene.control.ButtonType.OK
            ).showAndWait();
        } catch (IOException e) {
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Erro ao salvar o arquivo:\n" + e.getMessage(),
                    javafx.scene.control.ButtonType.OK
            ).showAndWait();
        }
    }

    // ════════════════════════════════════════════════════════
    //   NAVEGAÇÃO
    // ════════════════════════════════════════════════════════

    @FXML
    private void handleVoltar() {
        navegarPara("/fxml/menu.fxml", "Menu — Copa do Mundo 2026");
    }

    @FXML
    private void handleLogout() {
        SessaoUsuario.getInstancia().encerrarSessao();
        navegarPara("/fxml/login.fxml", "Login — Copa do Mundo 2026");
    }

    private void navegarPara(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) terminalOutput.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(titulo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════
    //   UTILITÁRIOS
    // ════════════════════════════════════════════════════════

    private void marcarAtivo(Button ativo, Button inativo) {
        ativo.getStyleClass().setAll("btn-relatorio-ativo");
        inativo.getStyleClass().setAll("btn-relatorio");
    }

    private String agora() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private String faseLegivel(FasePartida fase) {
        if (fase == null) return "-";
        return switch (fase) {
            case FASE_DE_GRUPOS           -> "Fase de Grupos";
            case OITAVAS_DE_FINAL         -> "Oitavas de Final";
            case QUARTAS_DE_FINAL         -> "Quartas de Final";
            case SEMIFINAL                -> "Semifinal";
            case FINAL                    -> "Final";
            case DISPUTA_DE_TERCEIRO_LUGAR-> "Disputa de 3º Lugar";
        };
    }
}